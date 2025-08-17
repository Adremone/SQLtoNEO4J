package com.trainning.migrate;

import com.trainning.migrate.utils.Props;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class App {
    public static void main(String[] args) throws Exception {
        Props properties = new Props();
        try(InputStream stream = App.class.getResourceAsStream("config.properties")){
            if(stream==null){
                throw new FileNotFoundException("Please create config.properties file in resources folder");
            }
            properties.getProps().load(stream);
            DataImporter.makeEntityQueries(properties.getProps().getProperty("input.entity.path"));
            DataImporter.createCSV(properties.getProps().getProperty("output.entity.path"));
            DataExporter.generateCypherQueries();
            DataImporter.makeEntityQueries();
            DataImporter.makeAssociationQueries(properties.getProps().getProperty("input.association.path"));
            DataImporter.createAssociationCSV(properties.getProps().getProperty("output.association.path"));
            DataExporter.generateCypherQueries();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }
}
