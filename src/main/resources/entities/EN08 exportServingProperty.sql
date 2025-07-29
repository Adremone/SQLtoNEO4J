SELECT 
    'ServingProperty' as kind,
    serving.name as localName,
    serving.description as description,
    serving.Name as displayName,
    serving.latitude as x,
    serving.longitude as y,
    serving.address_street_name as "address.streetName",
    serving.address_street_number as "address.streetNr",
    serving.address_town as "address.city",
    serving.address_postcode as "address.postcode",
    serving.address_country as "address.country",
    serving.serving_Property_Type_name as "properties.servingPropertyType",
    serving.serving_Property_Status_name as "properties.servingPropertyStatus",
    serving.SERVICABILITY_NAME as serviceability,
    geoArea.name || ',' || coArea.name || ',' || street.name || ',' || site.name as context,
    geoArea.name || ',' || coArea.name || ',' || street.name || ',' || site.name || ',' || serving.name as globalName
FROM SERVING_PROPERTY serving
	LEFT OUTER JOIN SITE site ON serving.in_site_id = site.object_id
    LEFT OUTER JOIN STREET street ON site.ON_STREET_ID = street.OBJECT_ID
	LEFT OUTER JOIN COVERAGE_AREA coArea ON street.IN_COVERAGE_AREA_ID = coArea.OBJECT_ID 
	LEFT OUTER JOIN GEOGRAPHIC_AREA geoArea ON coArea.CONTAINING_GEO_AREA_ID = geoArea.OBJECT_ID;