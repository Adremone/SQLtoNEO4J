SELECT 
    'CentralOffice' as Kind,
    name as localName,
	name as displayName,
	description as description,
	latitude as x,
	longitude as y,
	address_street_name as "address.streetName",
	address_street_number as "address.streetNumber",
	address_town as "address.city",
	address_postcode as "address.postCode",
	address_country as "address.country",
	geographic_area_type_name as "properties.geographicAreaType" 
FROM CENTRAL_OFFICE;
