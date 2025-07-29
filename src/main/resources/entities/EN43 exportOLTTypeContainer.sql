SELECT 
    'OLTModelContainer' as Kind,
    name as localName,
    name as globalName,
    name as displayName,
    description
FROM ONT_TYPE_CONTAINER models 
    JOIN ont_type_container container on models.of_container_id = container.object_id
    JOIN oltphysical_device device on device.object_id = container.of_device_id
;
