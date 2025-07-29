SELECT 
    'SubServingProperty' as kind,
    subserving.name as localName,
    subserving.description as description,
    subserving.raccordable_date as "properties.raccordable_date",
    subserving.linked as "properties.linked",
    geoArea.name || ',' || coArea.name || ',' || street.name || ',' || site.name || ',' || serving.name as context,
    geoArea.name || ',' || coArea.name || ',' || street.name || ',' || site.name || ',' || serving.name || ',' || subserving.name as globalName
FROM SUB_SERVING_PROPERTY subserving
    LEFT OUTER JOIN SERVING_PROPERTY serving on subserving.in_serving_property_id = serving.object_id
	LEFT OUTER JOIN SITE site ON serving.in_site_id = site.object_id
    LEFT OUTER JOIN STREET street ON site.ON_STREET_ID = street.OBJECT_ID
	LEFT OUTER JOIN COVERAGE_AREA coArea ON street.IN_COVERAGE_AREA_ID = coArea.OBJECT_ID 
	LEFT OUTER JOIN GEOGRAPHIC_AREA geoArea ON coArea.CONTAINING_GEO_AREA_ID = geoArea.OBJECT_ID;
