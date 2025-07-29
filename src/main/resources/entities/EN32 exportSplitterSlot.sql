SELECT 
    slot.name as localName,
    device.name || ',' || chassis.name || ',' || shelf.name  as context,
    'slot' as description,
    slot.name as displayName,
    device.name || ',' || chassis.name || ',' || shelf.name || slot.name as globalName
FROM SPLITTER_SLOT slot 
    JOIN SPLITTER_CHASSIS_SHELF shelf on slot.in_shelf_uniq = shelf.object_id
    JOIN SPLITTER_CHASSIS chassis on chassis.object_id = shelf.in_chassis_id
    join SPLITTER_PHYSICAL_DEVICE device on device.object_id = chassis.of_device_uniq
    join SPLITTER_CONTAINER container on container.object_id = device.IN_SPLITTER_CONTAINER_ID
    join OLTPHYSICAL_DEVICE olt_device on olt_device.object_id = container.of_device_id;