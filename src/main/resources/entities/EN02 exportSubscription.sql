SELECT 'Subscription' as kind,
       subscriber_id || ',' || name as globalName,
       subscriber_id as subscriber,
       name as localName,
       name as displayName,
       SUBSCRIPTION_STATUS_NAME as ServiceOperationalState
from subscription
;
