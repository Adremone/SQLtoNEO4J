package com.trainning.migrate;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;


@Getter
@Setter
public class TransformationUtils {
    @Getter @Setter
    private static LinkedHashMap<String,String> entityFileNames = new LinkedHashMap<>(); // csv ouputNames
    @Getter @Setter
    private static HashMap<String,String> entityTypeMap = new HashMap<>();
    @Getter @Setter
    private static HashMap<String,String>AssociationTypeMap = new HashMap<>();
    private static boolean generateCypher;
    @Getter
    private static HashMap<String,ArrayList<String>>nodeLabels = new HashMap<>();
    @Getter
    private static LinkedHashMap<String,String> associationFileNames = new LinkedHashMap<>(); // csv ouputNames

    static {
        nodeLabels.put("customer", new ArrayList<>(Arrays.asList(
                "com.nokia.nsw.uiv.model.common.party.PartyRole",
                "com.nokia.nsw.uiv.model.common.party.Customer"
        )));
        nodeLabels.put("service", new ArrayList<>(Arrays.asList(
                "com.nokia.nsw.uiv.model.service.Service"
        )));
        nodeLabels.put("geographicArea",new ArrayList<>(Arrays.asList(
                "com.nokia.nsw.uiv.model.location.Place",
                "com.nokia.nsw.uiv.model.location.GeographicLocation",
                "com.nokia.nsw.uiv.model.location.GeographicArea"
        )));
        nodeLabels.put("geographicSite",new ArrayList<>(Arrays.asList(
                "com.nokia.nsw.uiv.model.location.Place",
                "com.nokia.nsw.uiv.model.location.GeographicSite"
        )));

        nodeLabels.put("ipSubnet",new ArrayList<>(Arrays.asList(
                "com.nokia.nsw.uiv.numbermanagement.Pool"
        )));
        nodeLabels.put("configuration",new ArrayList<>(Arrays.asList(
                "com.nokia.nsw.uiv.model.resource.logical.Configuration",
                "`com.nokia.nsw.uiv.model.resource.logical.LogicalResource",
                "com.nokia.nsw.uiv.model.resource.Resource"
        )));
        nodeLabels.put("physicalComponent",new ArrayList<>(Arrays.asList(
               "com.nokia.nsw.uiv.model.resource.infra.physical.PhysicalComponent",
                "com.nokia.nsw.uiv.model.resource.infra.InfraComponent",
                "com.nokia.nsw.uiv.model.resource.infra.InfraResource",
                "com.nokia.nsw.uiv.model.resource.Resource"
        )));
        nodeLabels.put("physicalDevice",new ArrayList<>(Arrays.asList(
                "com.nokia.nsw.uiv.model.resource.infra.physical.PhysicalDevice",
                "com.nokia.nsw.uiv.model.resource.infra.InfraComponent",
                "com.nokia.nsw.uiv.model.resource.infra.InfraResource",
                "com.nokia.nsw.uiv.model.resource.Resource"
        )));
        nodeLabels.put("physicalPort",new ArrayList<>(Arrays.asList(
                "com.nokia.nsw.uiv.model.resource.infra.physical.PhysicalPort",
                "com.nokia.nsw.uiv.model.resource.infra.InfraComponent",
                "com.nokia.nsw.uiv.model.resource.infra.InfraResource",
                "com.nokia.nsw.uiv.model.resource.Resource"
        )));
        nodeLabels.put("physicalLink",new ArrayList<>(
                Arrays.asList(
                        "com.nokia.nsw.uiv.model.resource.infra.physical.PhysicalLink",
                        "com.nokia.nsw.uiv.model.resource.infra.InfraComponent",
                        "com.nokia.nsw.uiv.model.resource.infra.InfraResource",
                        "com.nokia.nsw.uiv.model.resource.Resource"
                )));
        nodeLabels.put("pool",new ArrayList<>(Arrays.asList(
                        "com.nokia.nsw.uiv.numbermanagement.Pool"
                )));
        nodeLabels.put("group",new ArrayList<>(
                Arrays.asList(
                        "com.nokia.nsw.uiv.model.common.Group"
                )));
        nodeLabels.put("logicalDevice",new ArrayList<>(Arrays.asList(
                        "com.nokia.nsw.uiv.model.resource.logical.LogicalDevice",
                        "com.nokia.nsw.uiv.model.resource.logical.LogicalResource",
                        "com.nokia.nsw.uiv.model.resource.Resource"
                )));
        nodeLabels.put("property",new ArrayList<>(Arrays.asList(
                        "com.nokia.nsw.uiv.model.location.Property",
                        "com.nokia.nsw.uiv.model.location.GeographicLocation",
                        "com.nokia.nsw.uiv.model.location.Place"
                )));
        nodeLabels.put("identifier",new ArrayList<>(Arrays.asList(
                "com.nokia.nsw.uiv.numbermanagement.Identifier"
        )));
    }
    @Getter
    @Setter
    private static LinkedHashMap<String,String>sqlQueries = new LinkedHashMap<>();
    @Getter
    @Setter
    private static HashMap<String,String>cypherQueries = new LinkedHashMap<>();
    @Getter
    @Setter
    private static HashMap<String,Long>rowCount = new LinkedHashMap<>(); // get unique row count for each sql query
}
