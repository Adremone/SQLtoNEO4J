Select 
    'OLTPhysicalDevice' as Kind,
    Name as localName,
    description,
    vendor_Name as vendor,
    model_Name as model,
    name as displayName,
    device_id as "properties.deviceID",
    equipment_id as "properties.equipmentID",
    vendor_Type_name as "properties.vendorType"
from oltphysical_device;