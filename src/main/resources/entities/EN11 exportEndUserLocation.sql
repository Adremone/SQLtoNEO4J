SELECT 'EndUserLocation' as kind,
        name as localName,
        name as displayName,
        description,
        address_street_name as "address.streetName",
        address_street_number as "address.streetNr",
        address_town as  "address.city",
        address_postcode as "address.postcode",
        address_country as "address.country",
        geographic_area_type_name as "properties.geographicAreaType",
        servicability_name as "properties.servicability"
from End_user_location;
