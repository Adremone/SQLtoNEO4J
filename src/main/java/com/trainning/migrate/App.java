package com.trainning.migrate;

import com.trainning.migrate.containers.AssociationMap;
import com.trainning.migrate.containers.EntityMap;
import com.trainning.migrate.utils.Props;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class App {
    public static void main(String[] args) throws Exception {
        Props properties = new Props();
        EntityMap entites = new EntityMap();
        AssociationMap associations = new AssociationMap();
        try(InputStream stream = App.class.getResourceAsStream("/config.properties")){
            if(stream==null) throw new FileNotFoundException("Please create config.properties file in resources folder");
            properties.getProps().load(stream);
            DataImporter.makeEntityQueries(entites,properties.getProps().getProperty("input.entity.path"));
            DataImporter.createCSV(entites,properties.getProps().getProperty("output.entity.path"));
            DataExporter.generateCypherQueries(entites,associations);
            DataImporter.makeAssociationQueries(entites,associations,properties.getProps().getProperty("input.association.path"));
            DataImporter.createAssociationCSV(entites,associations,properties.getProps().getProperty("output.association.path"));
            DataExporter.generateCypherQueries(entites,associations);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }
}
