package com.trainning.migrate.containers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;


public class AssociationMap extends LinkedHashMap<String, AssociationValue>{

    public String print(String assoicationNo){
        StringBuilder s = new StringBuilder();
        String to = super.get(assoicationNo).getTo();
        String outgoing = super.get(assoicationNo).getFrom();
        String name = super.get(assoicationNo).getName();
        long rowCount = super.get(assoicationNo).getCount();
        s.append(outgoing);
        s.append("---[");
        s.append(name);
        s.append("]-->");
        s.append(to);
        s.append(":");
        s.append(rowCount);
        return s.toString();
    }

    public void put(String number,String name,String to,String from){
        AssociationValue entry = new AssociationValue();
        entry.setName(name);
        entry.setTo(to);
        entry.setFrom(from);
        super.put(number,entry);
    }


    public ArrayList<String> listAssociations(){
        StringBuilder s = new StringBuilder();
        ArrayList<String> associations = new ArrayList<>();
        for(Map.Entry<String, AssociationValue> p:super.entrySet()){
            s.setLength(0);
            s.append("AS_");
            if(Integer.parseInt(p.getKey())<10) s.append("0");
            s.append(p.getKey());
            s.append(" ");
            s.append(p.getValue().getFrom());
            s.append("-");
            s.append("->");
            s.append(p.getValue().getTo());
            s.append("::");
            s.append(p.getValue().getName());
            associations.add(s.toString());
        }
        return associations;
    }

}
