UPDATE UM_PERMISSION SET UM_RESOURCE_ID = replace(UM_RESOURCE_ID, '/permission/admin/cassandra/data/', '/permission/admin/cassandra/default/system/');
UPDATE UM_PERMISSION SET UM_RESOURCE_ID = replace(UM_RESOURCE_ID, '/permission/admin/cassandra/data', '/permission/admin/cassandra/default');
