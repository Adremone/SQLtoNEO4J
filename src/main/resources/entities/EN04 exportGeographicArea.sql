SELECT geoArea.NAME AS localName,
    geoArea.DESCRIPTION AS DESCRIPTION,
    geoArea.Name as displayName,
    geoArea.Name as globalName,
    geoArea.GEOGRAPHIC_AREA_TYPE_NAME AS "properties.geographicAreaType"
FROM GEOGRAPHIC_AREA geoArea;



