SELECT 
    device.name || ',' || chassis.name || ',' || shelf.name as context,
    slot.name as name,
    slot.description as description,
    slot.slot_reference as "properties.slotReference",
    slot.name as displayName,
    chassis.name || ',' || slot.name as globalName
FROM OLTSLOT slot
    JOIN OLTCHASSIS_SHELF shelf on shelf.object_id = slot.in_shelf_uniq
    JOIN OLTCHASSIS chassis on chassis.object_id = shelf.in_chassis_id
    JOIN OLTPHYSICAL_DEVICE device on device.object_id = chassis.of_device_uniq;
