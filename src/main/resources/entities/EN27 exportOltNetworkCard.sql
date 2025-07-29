SELECT 
    device.name || ',' || chassis.name || ',' || shelf.name || ',' || slot.name as context,
    card.name as name,
    card.description as description,
    card.in_slot_id as "properties.slotName", -- ask for property here
    card.number_of_ports as "properties.numberOfPorts",
    slot.slot_reference as "properties.slotReference",
    card.name as displayName,
    device.name || ',' || chassis.name || ',' || shelf.name || ',' || slot.name || ',' || card.name as globalName
FROM OLTNETWORK_CARD card
    JOIN OLTSLOT slot on slot.object_id = card.in_slot_id
    JOIN OLTCHASSIS_SHELF shelf on shelf.object_id = slot.in_shelf_uniq
    JOIN OLTCHASSIS chassis on chassis.object_id = shelf.in_chassis_id
    JOIN OLTPHYSICAL_DEVICE device on device.object_id = chassis.of_device_uniq;