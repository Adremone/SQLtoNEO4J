package com.trainning.migrate.containers;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssociationValue {
    private String identifier;
    private String name;
    private String to;
    private String from;
    private String outputFileName;
    private String sqlQuery;
    private String cypherQuery;
    private long count;

    public void print(){
        System.out.println("identifier:"+identifier);
        System.out.println("name:"+name);
        System.out.println("to:"+to);
        System.out.println("from:"+from);
        System.out.println("outputFileName:"+outputFileName);
        System.out.println("sqlQuery:"+sqlQuery);
        System.out.println("cypherQuery:"+cypherQuery);
        System.out.println("count:"+count);
    }
}
