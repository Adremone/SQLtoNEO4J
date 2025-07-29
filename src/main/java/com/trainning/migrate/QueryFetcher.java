package com.trainning.migrate;

import java.io.*;
import java.nio.file.Files;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryFetcher {
    // { name --> [query,fileName,rowCount] }
    private static final LinkedHashMap<String,ArrayList<String>>queryMap = new LinkedHashMap<>();
    private static final ArrayList<String>filenames = new ArrayList<>(List.of(
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
    private static String username = "SRITEMP";
    private static String password = "Comptel_2017";
    private static String url = "jdbc:oracle:thin:@//localhost:1524/SRI_PDB";
    private static Connection conn = ConnectionHelper.createConnection(QueryFetcher.url, QueryFetcher.username, QueryFetcher.password);

    public static void makeQueries(String queryDirectory) throws IOException {
        // add exception to check directory here
        File folder = new File(queryDirectory);
        String query = "";
        String name = "";
        Pattern pattern = Pattern.compile("EN\\d+ export(.+)\\.sql",Pattern.CASE_INSENSITIVE);
        int count = 0;
        System.out.println("------------------- Extracting queries --------------------");
        for(File file:folder.listFiles()){
            Matcher matcher = pattern.matcher(file.getName());
            System.out.println(file.getName());
            if(matcher.matches()){
                name = matcher.group(1);
            }else{
                System.out.println("Entity name not found.\nUsing filename instead");
                name = file.getName();
            }
            query = new String(Files.readAllBytes(file.toPath())).replace(";","");
            QueryFetcher.queryMap.put(name,new ArrayList<>(List.of(query,QueryFetcher.filenames.get(count))));
            count++;
            System.out.println(String.valueOf(count)+":Extracted query for "+name);
        }
        System.out.println("Total Queries Extracted:"+String.valueOf(count+1));
        System.out.println("------------------------ ----------------------------------");
    }

    private static String getCountQuery(String query) throws Exception{
        if(query==null||query.isBlank()||!query.matches("(?is).*FROM.*")) return "";
        String tablesQuery = query.split("(?is)FROM")[1];
        tablesQuery = tablesQuery.split("(?is)group by")[0];
        String countQuery = "SELECT COUNT(*) FROM "+tablesQuery;
        return countQuery;
    }

    public static void fetchCSV() throws Exception {
        int fetchSize = 10000;

        if (QueryFetcher.queryMap.size() == 0) {
            System.out.println("Please populate the queries using makeQueries Method");
            return;
        }

        System.out.println("-----------------------------------------");
        System.out.printf("|  %-25s | %8s  |%n", "Entity Name", "RowCount");
        System.out.println("-----------------------------------------");

        // Get row counts first
        for (Map.Entry<String, ArrayList<String>> entry : QueryFetcher.queryMap.entrySet()) {
            try (Statement s = conn.createStatement()) {
                s.setFetchSize(fetchSize);
                ArrayList<String> values = entry.getValue();
                String name = entry.getKey();
                String countQuery = getCountQuery(values.get(0));
                int rowCount = 0;

                if (!countQuery.isBlank()) {
                    try (ResultSet rs = s.executeQuery(countQuery)) {
                        if (rs.next()) {
                            rowCount = rs.getInt(1);
                        }
                    }
                }

                values.add(String.valueOf(rowCount));
                System.out.printf("%-25s | %8d%n", name, rowCount);
            }
        }

        // Now fetch data per query
        for (Map.Entry<String, ArrayList<String>> entry : QueryFetcher.queryMap.entrySet()) {
            ArrayList<String> values = entry.getValue();
            String name = entry.getKey();
            String query = values.get(0);
            if (query.isBlank()) {
                System.out.println("Entity " + name + " has no query");
                continue;
            }
            int currRow = 0;
            String fileName = values.get(1);

            try (Statement s = conn.createStatement()) {
                s.setFetchSize(fetchSize);
                try (ResultSet rs = s.executeQuery(query);
                     BufferedWriter csvWriter = new BufferedWriter(new FileWriter(fileName))) {

                    ResultSetMetaData meta = rs.getMetaData();
                    int columnCount = meta.getColumnCount();

                    // Write headers
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
                        System.out.print("\rNo of rows inserted for " + name + ": " + currRow + "\\" + values.get(2));
                        System.out.flush();
                    }
                }
            }
            System.out.println("\nFile " + fileName + " parsed successfully!");
        }
    }
}
