SELECT 
    'ONTModel' as Kind,
    device.name as context,
    models.name as localName,
    models.vendor_Name as "properties.vendorName",
    models.ont_version as "properties.ontVersion",
    models.equipment_ID as "properties.equipmentID",
    models.main_Soft_Version as "properties.mainSoftVersion",
    models.vendor_Type_Name as "properties.vendorType",
    models.serv_Prof as "properties.servProf",
    models.auth_Mode_Name as "properties.autMode",
    'description' as description
FROM ont_models models 
    JOIN ont_type_container container on models.of_container_id = container.object_id
    JOIN oltphysical_device device on device.object_id = container.of_device_id
;
