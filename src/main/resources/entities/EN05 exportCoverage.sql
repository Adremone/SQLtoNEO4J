SELECT 
    geoArea.name as ContainingGeoArea,
    cvgArea.Name as localName,
    cvgArea.Description,
    cvgArea.GEOGRAPHIC_AREA_TYPE_NAME as "properties.geographicAreaType",
    cvgArea.name as displayName,
    geoArea.name||','||cvgArea.name as globalName
FROM Coverage_area cvgArea 
	LEFT OUTER JOIN GEOGRAPHIC_AREA geoArea ON cvgArea.CONTAINING_GEO_AREA_ID = geoArea.OBJECT_ID;


