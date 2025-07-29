SELECT 
    'IpV6' as kind,
    ip_status as state,
    -- case statement here
    'private' as addressScope,
    description as description,
    name as network_address,
    '128' as prefix_length,
    node_address_scope as "properties.nodeAddressScope",
    node_address as "properties.nodeAddress",
    description as poolType
FROM IpV6;



