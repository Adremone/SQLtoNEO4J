SELECT 
    'IpV4' as kind,
    ip_status as state,
    -- case statement here
    'private' as addressScope,
    hostNumberScope as "properties.hostNumberScope",
    '32' as subnetMask,
    name as networkAddress,
    description as poolType
FROM IpV4;