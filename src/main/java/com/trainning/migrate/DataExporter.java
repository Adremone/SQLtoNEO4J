package com.trainning.migrate;

import com.trainning.migrate.containers.AssociationMap;
import com.trainning.migrate.containers.AssociationValue;
import com.trainning.migrate.containers.EntityMap;
import com.trainning.migrate.containers.EntityValue;
import org.neo4j.driver.Driver;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;


public class DataExporter {
    private static final Driver cqlConntection = ConnectionHelper.createNEO4jConnection();

    private static String generateAssociationQuery(AssociationValue association) throws Exception{
        String csvFileName = association.getOutputFileName();
        String associationName = association.getName();
        String to = association.getTo().substring(0,1).toUpperCase()+association.getTo().substring(1);
        String from = association.getFrom().substring(0,1).toUpperCase()+association.getFrom().substring(1);
        System.out.println(to);
        System.out.println(from);
        System.out.println(associationName);
        System.out.println(csvFileName);
        String cypherQuery = "USING PERIODIC COMMIT 1000\nLOAD CSV WITH HEADERS FROM 'file:///"+association.getOutputFileName()+".csv' AS row\n";
        String direction = "->";
        if(associationName == null||associationName.isBlank()) associationName = "Relation";
        if(associationName.contains("-")) direction = "<-";
        associationName = associationName.replace("-","");
        cypherQuery+= "MATCH (left:Neo4jDomainObject {globalName:row.`"+association.getFrom()+"`,kind:'"+from+"'}), (right:Neo4jDomainObject {globalName:row.`"+association.getTo()+"`,kind:'"+to+"'})\nMERGE (left)-[:"+associationName.toUpperCase()+"]"+direction+"(right);";
        return cypherQuery;
    }

    public static String generateCypherQuery(String type,String csvFileName) throws Exception{
        // utility to dynamically create a cypher file
        String commonProperties = "optlock:0,\nschemaVersion:0,\nindexCompanion:\"LIVE\"";
        String setProperties = "var.uuid=randomUUID(),\nvar.createdBy=\"doc-uiv-inst2.admin\",\nvar.createdDate=\"2025-07-11T11:36:41.525Z\";";
        String properties = "{";
        String labels = "Neo4jDomainObject:Neo4jDomainNodeObject:LIVE:\n`com.nokia.nsw.uiv.model.common.Entity`:";
        String line = "";
        int size = 0;
        if(TransformationUtils.getNodeLabels().get(type)!=null){
            size = TransformationUtils.getNodeLabels().get(type).size();
        }
        for(int i=0;i<size;i++){
            String label = TransformationUtils.getNodeLabels().get(type).get(i);
            if(i!=size-1) labels += "`" + label + "`:\n";
            else labels += "`" + label + "`\n";
        }
        String query = "";
        String csv_directory = "src/main/resources/output/entities";
        System.out.println(csvFileName);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(csv_directory+"/"+csvFileName+".csv"));
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
            query = "USING PERIODIC COMMIT 1000\nLOAD CSV WITH HEADERS FROM 'file:///"+csvFileName+".csv"+"' AS row\nMERGE(var:"+labels+properties+") ON CREATE SET \n" + setProperties;
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
        return query;
    }

    public static void generateCypherQueries(EntityMap entities, AssociationMap associations) throws Exception{
        // csv generated is of type kind_type so take type from there
        String outputDirectory = "src/main/resources/cql/associations/";
        Path path = Paths.get(outputDirectory);
        Files.createDirectories(path);
        String parsedFileName = "";
        for(Map.Entry<String, AssociationValue> entry:associations.entrySet()){
            AssociationValue association = entry.getValue();
            String queryName = association.getIdentifier();
            String countNo = entry.getKey();
            if(Integer.parseInt(countNo)<=9) countNo = "0" + countNo;
            parsedFileName = "AS"+countNo+" "+queryName.substring(0,1).toUpperCase()+queryName.substring(1)+".cql";
            String query = generateAssociationQuery(association);
            System.out.println(outputDirectory+ parsedFileName);
            BufferedWriter bw = new BufferedWriter(new FileWriter(outputDirectory+ parsedFileName));
            System.out.println(query);
            bw.append(query);
            bw.flush();
        }
    }

    public static void insertData(){

    }
}
