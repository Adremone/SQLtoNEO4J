SELECT 
  TRIM(OF_DEVICE_UNIQ) AS ofDevice,
  TRIM(NAME) AS name,
  TRIM(DESCRIPTION) AS description,
  CHASSIS_NUMBER,
  TRIM(NAME) AS displayName,
  TRIM(OF_DEVICE_UNIQ) || ',' || TRIM(NAME) AS globalName
FROM OLTChassis;