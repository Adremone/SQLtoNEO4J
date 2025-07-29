SELECT 
    'SubscriberPostalAddress' AS KIND,
    p.address_street_name AS "address.streetName",
    p.address_street_number AS "address.streetNumber",
    p.address_town AS "address.city",
    p.address_postcode AS "address.postcode",
    p.address_country AS "address.country",
    '"' || s.object_id || '-PostalAddress' || '"' AS localName,
    '"' || s.object_id || '-PostalAddress' || '"' AS globalName
FROM postal_address p
JOIN subscriber s ON s.object_id = p.party_role_id;
