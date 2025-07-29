SELECT 
    'SplitterOutPort' as kind,
    port.name as localName,
    port.description as description,
    port.port_number as portNumber,
    device.name || ',' || chassis.name || ',' || shelf.name || ',' || slot.name || ',' || card.name as context,
    device.name || ',' || chassis.name || ',' || shelf.name || ',' || slot.name || ',' || card.name || ',' || port.name as globalName,
    port.port_status_name as resourceUsageState,
    CASE 
        WHEN port.port_status_name = 'available' THEN ''
        WHEN port.port_status_name IS NULL THEN ''
        ELSE port.port_status_name
    END AS operationalState
FROM SPLITTER_OUT_PORT port
    JOIN SPLITTER_OUT_CARD card ON port.on_card_id = card.object_id
    JOIN SPLITTER_SLOT slot ON card.in_slot_uniq = slot.object_id
    JOIN SPLITTER_CHASSIS_SHELF shelf ON slot.in_shelf_uniq = shelf.object_id
    JOIN SPLITTER_CHASSIS chassis ON chassis.object_id = shelf.in_chassis_id
    JOIN SPLITTER_PHYSICAL_DEVICE device ON device.object_id = chassis.of_device_uniq
--    JOIN SPLITTER_CONTAINER container ON container.object_id = device.IN_SPLITTER_CONTAINER_ID
--    JOIN OLTPHYSICAL_DEVICE olt_device ON olt_device.object_id = container.of_device_id
;
