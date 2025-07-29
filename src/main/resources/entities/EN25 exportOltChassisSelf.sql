SELECT
    'OLTChassisShelf' AS kind,
    opd.name AS context,
    TRIM(ocs.name) AS localName,
    TRIM(ocs.DESCRIPTION) AS description
FROM
    OLTCHASSIS_SHELF ocs 
    JOIN OLTCHASSIS oc on ocs.in_chassis_id = oc.object_id
    JOIN OLTPHYSICAL_DEVICE opd on oc.of_device_uniq = opd.object_id;

