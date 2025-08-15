package com.trainning.migrate;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.logging.Logger;
import com.trainning.migrate.TransformationUtils;
import com.trainning.migrate.containers.AssociationMap;
import com.trainning.migrate.containers.AssociationValue;
import com.trainning.migrate.containers.EntityValue;
import com.trainning.migrate.containers.EntryMap;
import io.github.cdimascio.dotenv.Dotenv;

public class DataImporter {
    private static final Logger log = Logger.getLogger(DataImporter.class.getName());
    private static final HashMap<String,String>sqlQueries = TransformationUtils.getSqlQueries();
    private static final HashMap<String,String>fileNames = TransformationUtils.getEntityFileNames();
    private static final HashMap<String,Long>rowCountMap = TransformationUtils.getRowCount();
    private static final String CSV_DIR = "src/main/resources/output/";
    private static final ArrayList<String>givenEntityFileNames = new ArrayList<>();
    private static final ArrayList<String>givenAssociationFileNames = new ArrayList<>();
    private static final String USERNAME = Dotenv.load().get("SQL_USER");
    private static final String PASSWORD = Dotenv.load().get("SQL_PASSWORD");
    private static final String URL = Dotenv.load().get("SQL_URI");
    private static final Connection conn = ConnectionHelper.createSQLConnection(DataImporter.URL, DataImporter.USERNAME, DataImporter.PASSWORD);

    private static final AssociationMap associations = TransformationUtils.getAssociations();
    private static final EntryMap entities = TransformationUtils.getEntities();

    public static void parseEntityFileNames(String path,List<String>fileNames) throws IOException {
        fileNames.addAll(Files.readAllLines(Path.of(path)));
    }

    public static void makeEntityQueries() throws IOException {
        String entityFileNames = "src/main/resources/entityOutputFileNames.txt";
        String directory = "src/main/resources/entities";
        parseEntityFileNames(entityFileNames,givenEntityFileNames);
        File folder = new File(directory);
        String query = "";
        String name = "";
        int queryNo = 0;
        Pattern pattern = Pattern.compile("(?is)EN(\\d+) export(.+)\\.sql",Pattern.CASE_INSENSITIVE);
        int count = 0;
        log.info("------------------- Extracting queries --------------------");
        for(File file: Objects.requireNonNull(folder.listFiles())){
            Matcher matcher = pattern.matcher(file.getName());
            log.info(file.getName());
            if(matcher.matches()){
                queryNo = Integer.parseInt(matcher.group(1));
                log.info(String.valueOf(queryNo));
                name = matcher.group(2);
            }else{
                log.info("Entity name not found.\nUsing filename instead");
                name = file.getName();
            }
            query = new String(Files.readAllBytes(file.toPath())).replace(";","");
            String[] parts = DataImporter.givenEntityFileNames.get(count).split("_");
            String type = "";
            if(parts.length>=2){
                type = parts[1].replace(".csv","");
            }
            EntityValue entity = new EntityValue();
            entity.setSqlQuery(query);
            entity.setName(name);
            entity.setOutputFileName(DataImporter.givenEntityFileNames.get(count));
            entity.setType(type);
            entities.put(String.valueOf(queryNo),entity);
            count++;
        }
        for(String info:entities.list()) log.info(info);

    }

    public static void makeEntityQueries(String queryDirectory) throws IOException {
        String entityFileNames = "src/main/resources/entityOutputFileNames.txt";
        parseEntityFileNames(entityFileNames,givenEntityFileNames);
        // add exception to check directory here
        File folder = new File(queryDirectory);
        String query = "";
        String name = "";
        int queryNo = 0;
        Pattern pattern = Pattern.compile("(?is)EN(\\d+) export(.+)\\.sql",Pattern.CASE_INSENSITIVE);
        int count = 0;
        log.info("------------------- Extracting queries --------------------");
        for(File file: Objects.requireNonNull(folder.listFiles())){
            Matcher matcher = pattern.matcher(file.getName());
            log.info(file.getName());
            if(matcher.matches()){
                queryNo = Integer.parseInt(matcher.group(1));
                log.info(String.valueOf(queryNo));
                name = matcher.group(2);
            }else{
                log.info("Entity name not found.\nUsing filename instead");
                name = file.getName();
            }
            query = new String(Files.readAllBytes(file.toPath())).replace(";","");
            sqlQueries.put(name,query);
            fileNames.put(name,DataImporter.givenEntityFileNames.get(count));
            log.info(String.valueOf(count)+":Extracted query for "+name);
            count++;
        }
        log.info("Total Queries Extracted:"+count+1);
        log.info("-----------------------------------------------------------");
    }

    private static String getCountQuery(String query){
        if(query==null||query.isBlank()||!query.matches("(?is).*FROM.*")) return "";
        return "SELECT COUNT(*) FROM (" + query +")";
    }

    public static void makeAssociationQueries(String queryDirectory) throws IOException {
        String associationFileNames = "src/main/resources/associationOutputFileNames.txt";
        parseEntityFileNames(associationFileNames,givenAssociationFileNames);
        File folder = new File(queryDirectory);
        String query = "";
        Pattern p = Pattern.compile("(?is)AS_(\\d+) (\\w+)_as_(\\w+)\\s*\\.sql");
        for(File f:folder.listFiles()){
            Matcher matcher = p.matcher(f.getName());
            System.out.println(f.getName());
            if(matcher.matches()){
                int associationNo = Integer.parseInt(matcher.group(1));
                String from = matcher.group(2);
                String to = matcher.group(3);
                int count = 0;
                AssociationValue a = new AssociationValue();
                a.setTo(to);
                a.setFrom(from);
                a.setIdentifier(f.getName().replaceAll("AS_(\\d+)","").replaceAll(".sql",""));
                a.setCount(0);
                a.setOutputFileName(givenAssociationFileNames.get(associationNo-1));
                String countQuery = getCountQuery(query);
                query = new String(Files.readAllBytes(f.toPath()));
                a.setSqlQuery(query.replace(";",""));
                a.print();
                associations.put(String.valueOf(associationNo),a);
            }
        }
        for(String a:associations.listAssociations()){
            System.out.println(a);
        }
    }

    public static void createCSV(String outputDirectory) throws Exception {
        int fetchSize = 1000;  // smaller fetch size for safer streaming

        if (sqlQueries.isEmpty()) {
            log.info("Please populate the queries using makeQueries Method");
            return;
        }

        Path path = Paths.get(outputDirectory);
        Files.createDirectories(path);

        log.info("------------------------------------------");
        log.info(String.format("|  %-25s | %8s  |%n", "Entity Name", "RowCount"));
        log.info("------------------------------------------");

        // Get row counts first
        for (Map.Entry<String, String> entry : sqlQueries.entrySet()) {
            try (Statement s = conn.createStatement()) {
                s.setFetchSize(fetchSize);
                String name = entry.getKey();
                long rowCount = 0;
                System.out.println("---"+entry.getKey());
                String countQuery = "";

                countQuery = getCountQuery(entry.getValue());
                if (!countQuery.isBlank()) {
                    try (ResultSet rs = s.executeQuery(countQuery)) {
                        if (rs.next()) {
                            rowCount = rs.getInt(1);
                        }
                    }
                }
                rowCountMap.put(name, rowCount);
                log.info(String.format("|  %-25s | %8d  |%n", name, rowCount));
            }
        }
        log.info("---------------------------------------");

        // Now fetch data per query
        for (Map.Entry<String, String> entry : sqlQueries.entrySet()) {
            String name = entry.getKey();
            String query = entry.getValue();
            if (query.isBlank()) {
                log.info("Entity " + name + " has no query");
                continue;
            }
            int currRow = 0;
            String fileName = CSV_DIR + fileNames.get(name) + ".csv";

            try (Statement s = conn.createStatement()) {
                s.setFetchSize(fetchSize);
                try (ResultSet rs = s.executeQuery(query);
                     BufferedWriter csvWriter = new BufferedWriter(new FileWriter(fileName), 16 * 1024)) { // 16KB buffer

                    ResultSetMetaData meta = rs.getMetaData();
                    int columnCount = meta.getColumnCount();

                    // Write CSV header
                    for (int i = 1; i <= columnCount; i++) {
                        csvWriter.append(meta.getColumnName(i));
                        if (i < columnCount) csvWriter.append(",");
                    }
                    csvWriter.append("\n");

                    // Write rows
                    while (rs.next()) {
                        for (int i = 1; i <= columnCount; i++) {
                            String currVal = rs.getString(i);
                            if (currVal != null && currVal.contains(",")) {
                                currVal = "\"" + currVal.replace("\"", "\"\"") + "\"";
                            }
                            csvWriter.append(currVal != null ? currVal : "");
                            if (i < columnCount) csvWriter.append(",");
                        }
                        csvWriter.append("\n");

                        currRow++;

                        if (currRow % 1000 == 0) {
                            log.info("\rNo of rows inserted for " + name + ": " + currRow + "\\" + rowCountMap.get(name));
                            System.out.print("\033[H\033[2J");
                            System.out.flush();
                        }

                        if (currRow % 10000 == 0) {
                            csvWriter.flush();
                        }
                    }

                    csvWriter.flush();
                    System.out.print("\033[H\033[2J");
                    System.out.flush();
                }
            }

            log.info("\nFile " + fileName + " parsed successfully!");
        }
    }

    public static void createAssociationCSV(String outputDirectory) throws Exception{
        int fetchSize = 1000;
        Path path = Paths.get(outputDirectory);
        Files.createDirectories(path);
        int count = 1;
        for (Map.Entry<String, AssociationValue> entry : associations.entrySet()) {
            try (Statement s = conn.createStatement()) {
                s.setFetchSize(fetchSize);
                String number = entry.getKey();
                AssociationValue association = entry.getValue();
                String identifier = association.getIdentifier();
                long rowCount = 0;
                System.out.println("---" + entry.getKey());
                String countQuery = "";

                countQuery = getCountQuery(association.getSqlQuery());
                if (!countQuery.isBlank()) {
                    try (ResultSet rs = s.executeQuery(countQuery)) {
                        if (rs.next()) {
                            rowCount = rs.getInt(1);
                        }
                    }
                }
                association.setCount(rowCount);
                log.info(String.format("|  %-25s | %8d  |%n", identifier, rowCount));

                if (association.getSqlQuery().isBlank()) {
                    log.info("Association " + identifier + " has no query");
                    continue;
                }

                int currRow = 0;
                String fileName = outputDirectory + association.getOutputFileName() + ".csv";


                try (ResultSet rs = s.executeQuery(association.getSqlQuery())){
                    BufferedWriter csvWriter = new BufferedWriter(new FileWriter(fileName), 16 * 1024); // 16KB buffer
                    ResultSetMetaData meta = rs.getMetaData();
                    int columnCount = meta.getColumnCount();

                    for (int i = 1; i <= columnCount; i++) {
                        csvWriter.append(meta.getColumnName(i));
                        if (i < columnCount) csvWriter.append(",");
                    }
                    csvWriter.append("\n");

                    while (rs.next()) {
                        for (int i = 1; i <= columnCount; i++) {
                            String currVal = rs.getString(i);
                            if (currVal != null && currVal.contains(",")) {
                                currVal = "\"" + currVal.replace("\"", "\"\"") + "\"";
                            }
                            csvWriter.append(currVal != null ? currVal : "");
                            if (i < columnCount) csvWriter.append(",");
                        }
                        csvWriter.append("\n");

                        currRow++;

                        if (currRow % 1000 == 0) {
                            log.info("\rNo of rows inserted for " + identifier + ": " + currRow + "\\" + association.getCount());
                            System.out.print("\033[H\033[2J");
                            System.out.flush();
                        }
                        if (currRow % 10000 == 0) csvWriter.flush();

                    }

                    csvWriter.flush();
                    System.out.print("\033[H\033[2J");
                    System.out.flush();
                }
            }
            count++;
        }
    }
}
