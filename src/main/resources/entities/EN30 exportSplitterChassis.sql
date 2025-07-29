SELECT 
    chassis.name as localName,
    chassis.description as description,
    chassis.chassis_number as "properties.chassis_number",
    device.name as "context"
FROM SPLITTER_CHASSIS chassis
    join SPLITTER_PHYSICAL_DEVICE device on device.object_id = chassis.of_device_uniq
    join SPLITTER_CONTAINER container on container.object_id = device.IN_SPLITTER_CONTAINER_ID
    join OLTPHYSICAL_DEVICE olt_device on olt_device.object_id = container.of_device_id;