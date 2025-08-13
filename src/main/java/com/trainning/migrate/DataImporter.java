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

public class DataImporter {

    private static final Logger log = Logger.getLogger(DataImporter.class.getName());
    private static final HashMap<String,String>sqlQueries = TransformationUtils.getSqlQueries();
    private static final HashMap<String,String>fileNames = TransformationUtils.getEntityFileNames();
    private static final HashMap<String,String>associationFileNames = TransformationUtils.getAssociationFileNames();
    private static final HashMap<String,Long>rowCountMap = TransformationUtils.getRowCount();
    private static final String CSV_DIR = "src/main/resources/output/";
    private static final ArrayList<String>givenFileNames = new ArrayList<>(List.of(
            "subscriber_customer",
            "subscription_service",
            "subscriberPostalAddress_property",
            "geographicArea_geographicArea",
            "coverageArea_geographicArea",
            "street_geographicArea",
            "site_geographicArea",
            "servingProperty_geographicSite",
            "centralOffice_geographicArea",
            "subservingProperty_property",
            "endUserLocation_geographicArea",
            "splitterLocation_geographicArea",
            "centralOfficeContainer_group",
            "oltLogicalDevice_logicalDevice",
            "ontLogicalDevice_logicalDevice",
            "lineProfile_configuration",
            "ontIdPool_pool",
            "ipV4_ipSubnet",
            "ipV6_ipSubnet",
            "ipV4_pool",
            "ipV6_pool",
            "ontIdIdentifier_identifier",
            "oltPhysicalDevice_physicalDevice",
            "oltChassis_physicalComponent",
            "oltShelf_physicalComponent",
            "oltSlot_physicalComponent",
            "oltNetworkCard_physicalComponent",
            "oltNetworkPort_physicalPort",
            "splitterPhysicalDevice_physicalDevice",
            "splitterChassis_physicalComponent",
            "splitterChassisSelf_physicalComponent",
            "splitterSlot_physicalComponent",
            "splitterInCard_physicalComponent",
            "splitterOutCard_physicalComponent",
            "splitterInPort_physicalPort",
            "splitterOutPort_physicalPort",
            "ontPhysicalDevice_physicalDevice",
            "ontAccessCard_physicalDevice",
            "ontAccessPort_physicalPort",
            "physicalLink_physicalLink",
            "splitterContainer_group",
            "ontLinkContainer_group",
            "ontTypeContainer_group",
            "ontModel_logicalDevice"
    ));
    private static final String USERNAME = "SRITEMP";
    private static final String PASSWORD = "Comptel_2017";
    private static final String URL = "jdbc:oracle:thin:@//localhost:1524/SRI_PDB";
    private static final Connection conn = ConnectionHelper.createSQLConnection(DataImporter.URL, DataImporter.USERNAME, DataImporter.PASSWORD);

    public static void makeEntityQueries(String queryDirectory) throws IOException {
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
            fileNames.put(name,DataImporter.givenFileNames.get(count));
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

    public static void clearQueries(){
        sqlQueries.clear();
    }
}
