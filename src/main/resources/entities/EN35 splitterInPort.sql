SELECT 
    'SplitterInPort' as Kind,
    inport.name as localName,
    inport.description as description,
    inport.port_number as portNumber,
    device.name || ',' || chassis.name || ',' || shelf.name || ',' || slot.name || ',' || card.name as context,
    device.name || ',' || chassis.name || ',' || shelf.name || ',' || slot.name || ',' || card.name || inport.name as "globalName"
FROM SPLITTER_IN_PORT inport
    JOIN SPLITTER_IN_CARD card on inport.on_card_id = card.object_id
    JOIN SPLITTER_SLOT slot on card.in_slot_uniq = slot.object_id
    JOIN SPLITTER_CHASSIS_SHELF shelf on slot.in_shelf_uniq = shelf.object_id
    JOIN SPLITTER_CHASSIS chassis on chassis.object_id = shelf.in_chassis_id
    join SPLITTER_PHYSICAL_DEVICE device on device.object_id = chassis.of_device_uniq
--    join SPLITTER_CONTAINER container on container.object_id = device.IN_SPLITTER_CONTAINER_ID
--    join OLTPHYSICAL_DEVICE olt_device on olt_device.object_id = container.of_device_id
;
