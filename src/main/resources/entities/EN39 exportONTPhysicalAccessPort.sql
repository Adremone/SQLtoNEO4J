SELECT 
    'ONTAccessPort' as Kind,
    oc.name as localName,
    oc.description,
    oc.number_of_ports as "properties.numberOfPorts",
    os.name as "properties.slotName",
    oc.of_device_uniq as context, 
    oc.of_device_uniq || ',' || oc.name as displayName,
    oc.name as globalName
FROM ONTACCESS_PORT oc
    JOIN ONTSLOT os on oc.in_slot_uniq = os.object_id
;
