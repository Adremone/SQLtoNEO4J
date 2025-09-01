package com.trainning.migrate.containers;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EntityValue {
    String name;
    String type;
    String outputFileName;
    Long count;
    private String sqlQuery;
    private String cypherQuery;
}
