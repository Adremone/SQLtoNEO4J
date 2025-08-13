package com.trainning.migrate.containers;

import java.util.LinkedHashMap;

public class EntryMap extends LinkedHashMap<String, EntityValue> {
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

}
