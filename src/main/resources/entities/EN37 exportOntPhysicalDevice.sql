SELECT 
    Name,
    description,
    serial_Number,
    vendor_Name,
    model_Name,
    equipment_ID,
    main_Soft_Version as "properites.mainSoftVersion",
    ont_ID as "properties.ontID",
    vendor_Type as "properties.vendorType",
    serv_Prof as "properties.servProf",
    aut_Mode as "properties.autMode",
    alias as "properties.alias", 
    name as displayName,
    name as globalName
FROM ONTPHYSICAL_DEVICE 
;