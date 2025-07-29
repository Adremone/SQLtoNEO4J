SELECT 
    device.name || ',' || chassis.name || ',' || shelf.name || ',' || slot.name || ',' || card.name as context,
    port.name as name,
    port.description as description,
    port.port_number as portNumber,
    port.name as displayName
--    device.name || ',' || chassis.name || ',' || shelf.name || ',' || slot.name || ',' || card.name as globalName
FROM OLTNETWORK_PORT port
    JOIN OLTNETWORK_CARD card on card.object_id = port.on_card_id
    JOIN OLTSLOT slot on slot.object_id = card.in_slot_id
    JOIN OLTCHASSIS_SHELF shelf on shelf.object_id = slot.in_shelf_uniq
    JOIN OLTCHASSIS chassis on chassis.object_id = shelf.in_chassis_id
    JOIN OLTPHYSICAL_DEVICE device on device.object_id = chassis.of_device_uniq;