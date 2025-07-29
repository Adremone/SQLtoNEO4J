SELECT 
    street.name as localName, 
    geoArea.name || ',' || coArea.name as context,
    street.description as description
FROM STREET street 
	LEFT OUTER JOIN COVERAGE_AREA coArea ON street.IN_COVERAGE_AREA_ID = coArea.OBJECT_ID 
	LEFT OUTER JOIN GEOGRAPHIC_AREA geoArea ON coArea.CONTAINING_GEO_AREA_ID = geoArea.OBJECT_ID;


