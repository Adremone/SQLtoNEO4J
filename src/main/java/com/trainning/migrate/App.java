package com.trainning.migrate;

public class App {
    public static void main(String[] args) throws Exception {
        String entityDirectory = "src/main/resources/entities";
        String associationDirectory = "src/main/resources/associations/";
//        DataImporter.makeEntityQueries(entityDirectory);
//        DataImporter.createCSV("src/main/resources/output/entity");
//        DataExporter.generateCypherQueries();
        DataImporter.makeEntityQueries();
        DataImporter.makeAssociationQueries(associationDirectory);
        DataImporter.createAssociationCSV("src/main/resources/output/associations/");
        DataExporter.generateCypherQueries();
    }
}
