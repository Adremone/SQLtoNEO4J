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
import com.trainning.migrate.containers.AssociationMap;
import com.trainning.migrate.containers.AssociationValue;
import com.trainning.migrate.containers.EntityValue;
import com.trainning.migrate.containers.EntityMap;
import com.trainning.migrate.utils.Props;
import io.github.cdimascio.dotenv.Dotenv;
import org.neo4j.driver.Driver;

public class DataImporter {
    private static final Logger log = Logger.getLogger(DataImporter.class.getName());
    private static final ArrayList<String>givenEntityFileNames = new ArrayList<>();
    private static final ArrayList<String>givenAssociationFileNames = new ArrayList<>();
    private static final String USERNAME = Dotenv.load().get("SQL_USER");
    private static final String PASSWORD = Dotenv.load().get("SQL_PASSWORD");
    private static final String URL = Dotenv.load().get("SQL_URI");
    private static final Connection conn = ConnectionHelper.createSQLConnection(URL, USERNAME, PASSWORD);
    private static final Driver cqlConntection = ConnectionHelper.createNEO4jConnection();

    public static void parseEntityFileNames(String path,List<String>fileNames) throws IOException {
        fileNames.addAll(Files.readAllLines(Path.of(path)));
    }

    public static void makeEntityQueries(EntityMap entities,String directory) throws IOException {
        Props properties = new Props();
        String entityFileNames = properties.getProps().getProperty("filenames.entity.path");
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
                log.info("Entity name not found.\nUsing filename instead.");
                name = file.getName();
            }
            query = new String(Files.readAllBytes(file.toPath())).replace(";","");
            query+="FETCH FIRST 100 ROWS ONLY";
            String[] parts = DataImporter.givenEntityFileNames.get(count).split("_");
            String type = "";
            if(parts.length>=2) type = parts[1].replace(".csv","");
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

    private static String getCountQuery(String query){
        if(query==null||query.isBlank()||!query.matches("(?is).*FROM.*")) return "";
        return "SELECT COUNT(*) FROM (" + query +")";
    }

    public static void makeAssociationQueries(EntityMap entities,AssociationMap associations,String queryDirectory) throws IOException {
        String associationFileNames = "src/main/resources/filenames/associationOutputFileNames.txt";
        parseEntityFileNames(associationFileNames,givenAssociationFileNames);
        File folder = new File(queryDirectory);
        String query = "";
        System.out.println("inside");
        Pattern p = Pattern.compile("(?is)AS_(\\d+) (\\w+)_as_(\\w+)\\s*\\.sql");
        for(File f:folder.listFiles()){
            Matcher matcher = p.matcher(f.getName());
            if(matcher.matches()){
                int associationNo = Integer.parseInt(matcher.group(1));
                System.out.println(associationNo);
                String from = matcher.group(2);
                String to = matcher.group(3);
                AssociationValue associationValue = new AssociationValue();
                associationValue.setTo(to);
                associationValue.setFrom(from);
                associationValue.setIdentifier(f.getName().replaceAll("AS_(\\d+)","").replaceAll(".sql",""));
                associationValue.setCount(0);
                String[] parts = givenAssociationFileNames.get(associationNo-1).split(":");
                associationValue.setOutputFileName(parts[0]);
                if(parts.length>1) associationValue.setName(parts[1]);
                System.out.println(associationValue);
                String countQuery = getCountQuery(query);
                query = new String(Files.readAllBytes(f.toPath()));
                associationValue.setSqlQuery(query.replace(";",""));
                associationValue.print();
                associations.put(String.valueOf(associationNo),associationValue);
            }
        }
        for(String a:associations.listAssociations()){
            System.out.println(a);
        }
    }

    public static void createCSV(EntityMap entities, String outputDirectory) throws Exception {
        int fetchSize = 1000;  // smaller fetch size for safer streaming
        // find some way to trigger error
//        if (sqlQueries.isEmpty()) {
//            log.info("Please populate the queries using makeQueries Method");
//            return;
//        }
        Path path = Paths.get(outputDirectory);
        Files.createDirectories(path);

        log.info("------------------------------------------");
        log.info(String.format("|  %-25s | %8s  |%n", "Entity Name", "RowCount"));
        log.info("------------------------------------------");

        // Get row counts first
        for (Map.Entry<String, EntityValue> entry : entities.entrySet()) {
            try (Statement s = conn.createStatement()) {
                s.setFetchSize(fetchSize);
                EntityValue entity = entry.getValue();
                String name = entry.getKey();
                long rowCount = 0;
                log.info("---" + entry.getKey()+" "+entity.getName());
                String countQuery = "";

                countQuery = getCountQuery(entity.getSqlQuery());
                if (!countQuery.isBlank()) {
                    try (ResultSet rs = s.executeQuery(countQuery)) {
                        if (rs.next()) {
                            rowCount = rs.getInt(1);
                        }
                    }
                }
                entity.setCount(rowCount);
                entities.put(entry.getKey(),entity);
                log.info(String.format("|  %-25s | %8d  |%n", name, rowCount));
            }
        }
        log.info("---------------------------------------");
        System.out.println(outputDirectory);
        // Now fetch data per query
        for (Map.Entry<String, EntityValue> entry : entities.entrySet()) {
            String index = entry.getKey();
            EntityValue entity = entry.getValue();
            if (entity.getSqlQuery().isBlank()) {
                log.info("Entity " + index + ":" + entity.getName() + "has no query");
                continue;
            }
            int currRow = 0;
            String fileName = outputDirectory+"/" + entity.getOutputFileName() + ".csv";
//            log.info(fileName);
            try (Statement s = conn.createStatement()) {
                s.setFetchSize(fetchSize);
                System.out.println(fileName);
                System.out.println(entity);
                try (ResultSet rs = s.executeQuery(entity.getSqlQuery());
                    BufferedWriter csvWriter = new BufferedWriter(new FileWriter(fileName), 16 * 1024)) { // 16KB buffer

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
                            log.info("\rNo of rows inserted for " + entity.getName() + ": " + currRow + "\\" + entity.getCount());
                            System.out.print("\033[H\033[2J");
                            System.out.flush();
                        }

                        if (currRow % 10000 == 0) {
                            csvWriter.flush();
                        }
                    }

                    csvWriter.flush();
                    log.info("\033[H\033[2J");
                    System.out.flush();
                }
            }

            log.info("\nFile " + fileName + " parsed successfully!");
        }
    }

    public static void createAssociationCSV(EntityMap entities, AssociationMap associations, String outputDirectory) throws Exception{
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
                String fileName = outputDirectory+"/" + association.getOutputFileName().split(":")[0] + ".csv";
                System.out.println(fileName);

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
