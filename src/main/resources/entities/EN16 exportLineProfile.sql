SELECT 
    'LineProfile' as kind,
    logical_device_uniq as context,
    name as localName,
    description as description,
    logical_device_uniq || ',' || name as globalName,
    logical_device_uniq || ',' || name as displayName,
    line_prof as "properties.lineProf",
    gem_port_ID as "properties.gemPortId",
    t_cont_id as "properties.tContId",
    user_vlan_id as "properties.userVlanId",
    vlan_id as "properties.vlanID",
    up_link_name as "properties.upLink",
    down_link_name as "properties.downlink"
FROM Line_profile;


