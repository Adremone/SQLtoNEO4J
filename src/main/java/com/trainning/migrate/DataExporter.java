package com.trainning.migrate;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.trainning.migrate.TransformationUtils;

public class DataExporter {
    public boolean GENERATE_CYPHER = false;
    public HashMap<String,String>entityFileNames;
    public HashMap<String,String>queries;
    public HashMap<String,Long>nodeCount;
    public HashMap<String,Long>relationCount;

    private static final LinkedHashMap<String,String> fileNames = TransformationUtils.getEntityFileNames();


    public static String generateCypherQuery(String fileName,String type,String queryName) throws Exception{
        // utility to dynamically create a cypher file
        String commonProperties = "optLock:0,\nschemaVersion:0,\nindexComparison:\"LIVE\"";
        String setProperties = "var.uuid=randomUUID(),\nvar.createdBy=\"doc-uiv-inst2.admin\",\nvar.createdDate=\"2025-07-11T11:36:41.525Z\";";
        String properties = "{";
        String labels = "Neo4jDomainObject:Neo4jDomainNodeObject:LIVE:\n`com.nokia.nsw.uiv.model.common.Entity`:";
        String line = "";
        String parsedType = type.split("_")[1];
        int size = 0;
        if(TransformationUtils.getNodeLabels().get(parsedType)!=null){
            size = TransformationUtils.getNodeLabels().get(parsedType).size();
        }
        for(int i=0;i<size;i++){
            String label = TransformationUtils.getNodeLabels().get(parsedType).get(i);
            if(i!=size-1) labels += "`" + label + "`:\n";
            else labels += "`" + label + "`\n";
        }
        String query = "";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            line = reader.readLine();
            if (line != null) {
                String[] headers = line.split(",");
                for (int i=0;i<headers.length;i++) {
                    String header = headers[i];
                    properties = properties+"`"+header+"`:row.`"+header+"`,\n";
                }
            } else {
                System.out.println("Empty file.");
            }
            properties+=commonProperties;
            properties+="\n}";
            query = "USING PERIODIC COMMIT 1000\nLOAD CSV WITH HEADERS FROM 'file:///"+fileNames.get(queryName)+".csv"+"' AS row\nMERGE(var:"+labels+properties+") ON CREATE SET \n" + setProperties;
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
        return query;
    }

    public static void generateCypherQueries() throws Exception{
        // csv generated is of type kind_type so take type from there
        String outputDirectory = "src/main/resources/entities_cql/";
        Path path = Paths.get(outputDirectory);
        Files.createDirectories(path);
        int count = 1;
        String parsedFileName = "";
        for(String queryName:fileNames.keySet()){
            String countNo = "";
            if(count<=9) countNo = "0" + String.valueOf(count);
            else countNo = String.valueOf(count);
            parsedFileName = countNo+" export"+queryName.substring(0,1).toUpperCase()+queryName.substring(1)+".cql";
            System.out.println(parsedFileName);
            String csvFileName = "src/main/resources/output/" + fileNames.get(queryName)+".csv";
            String[] parts = csvFileName.split("[/\\\\]");
            String type = parts[parts.length-1].replace(".csv","");
            String query = generateCypherQuery(csvFileName,type,queryName);
            System.out.println(outputDirectory+ parsedFileName);
            BufferedWriter bw = new BufferedWriter(new FileWriter(outputDirectory+ parsedFileName));
            bw.append(query);
            bw.flush();
            count++;
        }
    }
}
