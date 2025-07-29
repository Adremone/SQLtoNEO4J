SELECT 
    site.name as localName,
    site.description as description,
    site.site_name as "properties.siteName",
    geoArea.name || ',' || coArea.name || ',' || street.name as context
FROM SITE site
    LEFT OUTER JOIN STREET street ON site.ON_STREET_ID = street.OBJECT_ID
	LEFT OUTER JOIN COVERAGE_AREA coArea ON street.IN_COVERAGE_AREA_ID = coArea.OBJECT_ID 
	LEFT OUTER JOIN GEOGRAPHIC_AREA geoArea ON coArea.CONTAINING_GEO_AREA_ID = geoArea.OBJECT_ID
;
