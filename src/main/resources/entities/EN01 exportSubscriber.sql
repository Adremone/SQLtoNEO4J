Select
    'SUBSCRIBER' as kind,
    subscriber.name as localName,
    subscriber.description as description,
    subscriber.name as displayName,
    subscriber.subscriber_type_name as "properties.subscriber_type",
    subscriber.subscriber_status_name as "properties.subscriber_status",
    MAX(CASE WHEN attr.name = 'MAC_ADDRESS' THEN attr.value END) AS "properties.macAddress",
    MAX(CASE WHEN attr.name = 'IP' THEN attr.value END) AS "properties.ip",
    MAX(CASE WHEN attr.name = 'IPTYPE' THEN attr.value END) AS "properties.ipType",
    MAX(CASE WHEN attr.name = 'IPTYPE_REL' THEN attr.value END) AS "properties.ipTypeRel",
    MAX(CASE WHEN attr.name = 'CPE_PASSWORD' THEN attr.value END) AS "properties.cpePassword",
    MAX(CASE WHEN attr.name = 'AD_UID' THEN attr.value END) AS "properties.adUID",
    MAX(CASE WHEN attr.name = 'AD_PASSWORD' THEN attr.value END) AS "properties.adPassword",
    MAX(CASE WHEN attr.name = 'PRECONNECTED' THEN attr.value END) AS "properties.preconnected",
    MAX(CASE WHEN attr.name = 'OFFER' THEN attr.value END) AS "properites.offer",
    MAX(CASE WHEN attr.name = 'CUST_NAME' THEN attr.value END) AS "properties.custName"
from subscriber
    join subscriber_attribute attr on attr.subscriber_id = subscriber.object_id
    group by
    subscriber.object_id, 
    subscriber.name, 
    subscriber.description, 
    subscriber.subscriber_type_name, 
    subscriber.subscriber_status_name
;