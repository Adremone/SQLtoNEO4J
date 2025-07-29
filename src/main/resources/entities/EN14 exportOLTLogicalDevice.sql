Select 
    'OLTLogicalDevice' as Kind,
    '' as context,
    Name as localName,
    description,
    name as displayName,
    '' || ',' || name as globalName
from oltlogical_device;
