package com.trainning.migrate.containers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class EntityMap extends LinkedHashMap<String, EntityValue> {
    public String print(String entityNo){
        StringBuilder s = new StringBuilder();
        String type = super.get(entityNo).getType();
        String name = super.get(entityNo).getName();
        Long rowCount = super.get(entityNo).getCount();
        s.append(name);
        s.append("-");
        s.append(type);
        s.append(":");
        s.append(rowCount);
        return s.toString();
    }

    public void put(String number,String name,String type,String outputFileName,Long count){
        EntityValue entry = new EntityValue();
        entry.setName(name);
        entry.setCount(count);
        entry.setType(type);
        entry.setOutputFileName(outputFileName);
        super.put(number,entry);
    }

    public ArrayList<String> list(){
        StringBuilder s = new StringBuilder();
        ArrayList<String> entites = new ArrayList<>();
        for(Map.Entry<String, EntityValue> p:super.entrySet()){
            s.setLength(0);
            if(Integer.parseInt(p.getKey())<10) s.append("0");
            s.append(p.getKey());
            s.append("-");
            s.append(p.getValue().getType());
            s.append("-");
            s.append(p.getValue().getCount());
            s.append("-");
            s.append(p.getValue().getOutputFileName());
            entites.add(s.toString());
        }
        return entites;
    }

}
