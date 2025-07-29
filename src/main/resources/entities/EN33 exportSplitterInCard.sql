SELECT 
    'SplitterInCard' as Kind,
    incard.name as localName,
    incard.description as description,
    incard.name as displayName,
    device.name || ',' || chassis.name || ',' || slot.name as context,
    device.name || ',' || chassis.name || ',' || shelf.name || ',' || slot.name || ',' || incard.name as "globalName",
    incard.number_of_ports as "properties.numOfPorts"
FROM SPLITTER_IN_CARD incard
    JOIN SPLITTER_SLOT slot on incard.in_slot_uniq = slot.object_id
    JOIN SPLITTER_CHASSIS_SHELF shelf on slot.in_shelf_uniq = shelf.object_id
    JOIN SPLITTER_CHASSIS chassis on chassis.object_id = shelf.in_chassis_id
    join SPLITTER_PHYSICAL_DEVICE device on device.object_id = chassis.of_device_uniq
--    join SPLITTER_CONTAINER container on container.object_id = device.IN_SPLITTER_CONTAINER_ID
--    join OLTPHYSICAL_DEVICE olt_device on olt_device.object_id = container.of_device_id
;
