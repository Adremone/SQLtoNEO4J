package com.trainning.migrate.containers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssociationValue {
    private String name;
    private String to;
    private String from;
    private long count;
    private String outputFileName;
}
