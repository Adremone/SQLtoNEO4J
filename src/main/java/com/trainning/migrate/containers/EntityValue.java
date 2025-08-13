package com.trainning.migrate.containers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EntityValue {
    String name;
    String type;
    String outputFileName;
    Long count;
}
