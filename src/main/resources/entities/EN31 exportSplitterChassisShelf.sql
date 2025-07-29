SELECT 
    'SplitterChassisShelf' as Kind,
    shelf.name as localName,
    shelf.description as description,
    shelf.name as displayName,
    device.name || ',' || chassis.name as "context",
    device.name || ',' || chassis.name || ',' || shelf.name as "globalName"
FROM SPLITTER_CHASSIS_SHELF shelf
    join SPLITTER_CHASSIS chassis on chassis.object_id = shelf.in_chassis_id
    join SPLITTER_PHYSICAL_DEVICE device on device.object_id = chassis.of_device_id;