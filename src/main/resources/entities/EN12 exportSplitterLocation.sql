SELECT 'SplitterLocation' as kind,
        name as localName,
        name as displayName,
        description,
        latitude as x,
        longitude as y,
        address_street_name as  "address.street_name",
        address_postcode as "address.postcode",
        address_country as "address.country",
        geographic_area_type_name as "properties.geographicAreaType"
from splitter_location;