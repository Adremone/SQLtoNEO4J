package com.trainning.migrate.utils;

import com.trainning.migrate.App;
import com.trainning.migrate.DataExporter;
import com.trainning.migrate.DataImporter;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Data
public class Props {
    private Properties props = new Properties();
    public Props(){
        try(InputStream stream = App.class.getResourceAsStream("config.properties")){
            if(stream==null){
                throw new IOException("Please create config.properties file in resources folder");
            }
            props.load(stream);
        }catch (IOException e){
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
