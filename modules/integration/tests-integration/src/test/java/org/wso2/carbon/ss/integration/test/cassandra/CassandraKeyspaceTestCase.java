/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *   * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.wso2.carbon.ss.integration.test.cassandra;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.*;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.cassandra.mgt.stub.ks.xsd.ColumnFamilyInformation;
import org.wso2.carbon.cassandra.mgt.stub.ks.xsd.ColumnInformation;
import org.wso2.carbon.cassandra.mgt.stub.ks.xsd.KeyspaceInformation;
import org.wso2.carbon.ss.SSIntegrationTest;
import org.wso2.ss.integration.common.clients.CassandraKeyspaceAdminClient;
import org.wso2.ss.integration.common.utils.CassandraClientHelper;
import org.wso2.ss.integration.common.utils.CassandraUtils;

import static org.testng.Assert.*;

public class CassandraKeyspaceTestCase extends SSIntegrationTest {

    private static final Log log = LogFactory.getLog(CassandraKeyspaceTestCase.class);

    private CassandraKeyspaceAdminClient client;
<<<<<<< HEAD
    private final String KEYSPACE_NAME = "TestKeyspace123";
    private final String COLUMN_FAMILY_NAME = "TestColumnFamily123";
    private final String COLUMN_NAME = "TestColumn123";
    private final String INDEX_TYPE = "keys";
    private final String INDEX_NAME = "test";
    private final double KEY_CACHE_SIZE = 0.5;
    private final double ROW_CACHE_SIZE = 0.5;
    private final int REPLICATION_FACTOR = 1;
=======
    private final String ENVIRONMENT_NAME="DEFAULT";
    private final String KEYSPACE_NAME="TestKeyspace123";
    private final String COLUMN_FAMILY_NAME="TestColumnFamily123";
    private final String COLUMN_NAME="TestColumn123";
    private final String INDEX_TYPE ="keys";
    private final String INDEX_NAME="test";
    private final double KEY_CACHE_SIZE=0.5;
    private final double ROW_CACHE_SIZE=0.5;
    private final int REPLICATION_FACTOR=1;
>>>>>>> 328b51eee71db8aa23649c4e9319a8b06fb896a3
    private TestUserMode userMode;

    @BeforeClass(alwaysRun = true)
    public void initializeTest() throws Exception {
        super.init(userMode);
        client = new CassandraKeyspaceAdminClient(ssContext.getContextUrls().getBackEndUrl(), sessionCookie);
    }

    @Factory(dataProvider = "userModeProvider")
    public CassandraKeyspaceTestCase(TestUserMode userMode) {
        this.userMode = userMode;
    }

    @Test(groups = "wso2.ss", description = "Add keyspace")
    public void addKeyspace()
            throws Exception {
        boolean isKeyspaceContains = false;
        KeyspaceInformation keyspaceInformation = new KeyspaceInformation();
        keyspaceInformation.setName(KEYSPACE_NAME);
        keyspaceInformation.setReplicationFactor(REPLICATION_FACTOR);
        keyspaceInformation.setStrategyClass(CassandraUtils.SIMPLE_CLASS);
<<<<<<< HEAD
        client.addKeyspace(keyspaceInformation);
        for (String keyspace : client.listKeyspacesOfCurrentUSer()) {
            if (KEYSPACE_NAME.equals(keyspace)) {
                isKeyspaceContains = true;
            }
        }
        assertTrue(isKeyspaceContains);
        keyspaceInformation = client.getKeyspaceOfCurrentUser(KEYSPACE_NAME);
=======
        client.addKeyspace(ENVIRONMENT_NAME, keyspaceInformation);
        for(String keyspace:client.listKeyspacesOfCurrentUSer(ENVIRONMENT_NAME))
        {
            if(KEYSPACE_NAME.equals(keyspace))
            {
                isKeyspaceContains=true;
            }
        }
        assertTrue(isKeyspaceContains);
        keyspaceInformation=client.getKeyspaceOfCurrentUser(ENVIRONMENT_NAME, KEYSPACE_NAME);
>>>>>>> 328b51eee71db8aa23649c4e9319a8b06fb896a3
        assertNotNull(keyspaceInformation);
        assertEquals(keyspaceInformation.getName(), KEYSPACE_NAME);
        assertEquals(keyspaceInformation.getReplicationFactor(), REPLICATION_FACTOR);
        assertEquals(keyspaceInformation.getStrategyClass(), CassandraUtils.SIMPLE_CLASS);
    }

    @Test(dependsOnMethods = "addKeyspace", description = "update keyspace by super tenant")
    public void updateKeyspace()
            throws Exception {
        boolean isKeyspaceContains = false;
        KeyspaceInformation keyspaceInformation = new KeyspaceInformation();
        keyspaceInformation.setName(KEYSPACE_NAME);
        keyspaceInformation.setReplicationFactor(REPLICATION_FACTOR);
        keyspaceInformation.setStrategyClass(CassandraUtils.OLD_NETWORK_CLASS);
<<<<<<< HEAD
        client.updateKeyspace(keyspaceInformation);
        for (String keyspace : client.listKeyspacesOfCurrentUSer()) {
            if (KEYSPACE_NAME.equals(keyspace)) {
                isKeyspaceContains = true;
            }
        }
        assertTrue(isKeyspaceContains);
        keyspaceInformation = client.getKeyspaceOfCurrentUser(KEYSPACE_NAME);
=======
        client.updateKeyspace(ENVIRONMENT_NAME, keyspaceInformation);
        for(String keyspace:client.listKeyspacesOfCurrentUSer(ENVIRONMENT_NAME))
        {
            if(KEYSPACE_NAME.equals(keyspace))
            {
                isKeyspaceContains=true;
            }
        }
        assertTrue(isKeyspaceContains);
        keyspaceInformation=client.getKeyspaceOfCurrentUser(ENVIRONMENT_NAME, KEYSPACE_NAME);
>>>>>>> 328b51eee71db8aa23649c4e9319a8b06fb896a3
        assertNotNull(keyspaceInformation);
        assertEquals(keyspaceInformation.getName(), KEYSPACE_NAME);
        assertEquals(keyspaceInformation.getReplicationFactor(), REPLICATION_FACTOR);
        assertEquals(keyspaceInformation.getStrategyClass(), CassandraUtils.OLD_NETWORK_CLASS);
    }

    @Test(dependsOnMethods = {"addKeyspace", "updateKeyspace", "updateColumnFamily", "addColumnFamily", "addColumn", "updateColumnBySuperTenant", "deleteColumnBySuperTenant", "deleteColumnFamily"}, description = "delete keyspace by super tenant")
    public void deleteKeyspaceBySuperTenant()
            throws Exception {
        assertTrue(client.deleteKeyspace(ENVIRONMENT_NAME, KEYSPACE_NAME));
    }

    @Test(dependsOnMethods = {"addKeyspace", "updateKeyspace"}, description = "Add column family by super tenant")
    public void addColumnFamily()
            throws Exception {
        boolean isCFContains = false;
        ColumnFamilyInformation columnFamilyInformation = new ColumnFamilyInformation();
        columnFamilyInformation.setName(COLUMN_FAMILY_NAME);
        columnFamilyInformation.setKeyspace(KEYSPACE_NAME);
        columnFamilyInformation.setId(2);
        columnFamilyInformation.setGcGraceSeconds(CassandraUtils.DEFAULT_GCGRACE);
        columnFamilyInformation.setMaxCompactionThreshold(CassandraUtils.DEFAULT_MAX_COMPACTION_THRESHOLD);
        columnFamilyInformation.setMinCompactionThreshold(CassandraUtils.DEFAULT_MIN_COMPACTION_THRESHOLD);
        columnFamilyInformation.setRowCacheSavePeriodInSeconds(CassandraUtils.DEFAULT_RAW_CACHE_TIME);
        columnFamilyInformation.setKeyCacheSize(KEY_CACHE_SIZE);
        columnFamilyInformation.setRowCacheSize(ROW_CACHE_SIZE);
        columnFamilyInformation.setType(CassandraUtils.COLUMN_TYPE_STANDARD);
        columnFamilyInformation.setComparatorType(CassandraUtils.BYTESTYPE);
        columnFamilyInformation.setDefaultValidationClass(CassandraUtils.BYTESTYPE);
        columnFamilyInformation.setSubComparatorType(CassandraUtils.ASCIITYPE);
        columnFamilyInformation.setComment("Test column family");
<<<<<<< HEAD
        client.addColumnFamily(columnFamilyInformation);
        for (String columnFamily : client.listColumnFamiliesOfCurrentUser(KEYSPACE_NAME)) {
            if (COLUMN_FAMILY_NAME.equals(columnFamily)) {
                isCFContains = true;
            }
        }
        assertTrue(isCFContains);
        columnFamilyInformation = client.getColumnFamilyInformationOfCurrentUser(KEYSPACE_NAME, COLUMN_FAMILY_NAME);
        assertEquals(columnFamilyInformation.getName(), COLUMN_FAMILY_NAME);
        assertEquals(columnFamilyInformation.getKeyspace(), KEYSPACE_NAME);
=======
        client.addColumnFamily(ENVIRONMENT_NAME, columnFamilyInformation);
        for(String columnFamily:client.listColumnFamiliesOfCurrentUser(ENVIRONMENT_NAME, KEYSPACE_NAME))
        {
            if(COLUMN_FAMILY_NAME.equals(columnFamily))
            {
                isCFContains=true;
            }
        }
        assertTrue(isCFContains);
        columnFamilyInformation=client.getColumnFamilyInformationOfCurrentUser(ENVIRONMENT_NAME, KEYSPACE_NAME, COLUMN_FAMILY_NAME);
        assertEquals(columnFamilyInformation.getName(),COLUMN_FAMILY_NAME);
        assertEquals(columnFamilyInformation.getKeyspace(),KEYSPACE_NAME);
>>>>>>> 328b51eee71db8aa23649c4e9319a8b06fb896a3
        //assertEquals(columnFamilyInformation.getId(),2,"CF id mismatch");
        assertEquals(columnFamilyInformation.getGcGraceSeconds(), CassandraUtils.DEFAULT_GCGRACE);
        assertEquals(columnFamilyInformation.getMaxCompactionThreshold(), CassandraUtils.DEFAULT_MAX_COMPACTION_THRESHOLD);
        assertEquals(columnFamilyInformation.getMinCompactionThreshold(), CassandraUtils.DEFAULT_MIN_COMPACTION_THRESHOLD);
        //assertEquals(columnFamilyInformation.getRowCacheSavePeriodInSeconds(),CassandraUtils.DEFAULT_RAW_CACHE_TIME);
        //assertEquals(columnFamilyInformation.getKeyCacheSize(),KEY_CACHE_SIZE);
        //assertEquals(columnFamilyInformation.getRowCacheSize(),ROW_CACHE_SIZE);
        assertEquals(columnFamilyInformation.getType(), CassandraUtils.COLUMN_TYPE_STANDARD);
        //assertEquals(columnFamilyInformation.getComparatorType(),CassandraUtils.BYTESTYPE);
        //assertEquals(columnFamilyInformation.getDefaultValidationClass(),CassandraUtils.BYTESTYPE);
        //assertEquals(columnFamilyInformation.getSubComparatorType(),CassandraUtils.ASCIITYPE);
        //assertEquals(columnFamilyInformation.getComment(),"Test column family");
    }

    @Test(dependsOnMethods = {"addKeyspace", "updateKeyspace", "addColumnFamily"}, description = "Update column family by super tenant")
    public void updateColumnFamily()
            throws Exception {
<<<<<<< HEAD
        boolean isCFContains = false;
        KeyspaceInformation keyspaceInformation = client.getKeyspaceOfCurrentUser(KEYSPACE_NAME);
        ColumnFamilyInformation columnFamilyInformation = CassandraClientHelper.getColumnFamilyInformationOfCurrentUser(keyspaceInformation, COLUMN_FAMILY_NAME);
        columnFamilyInformation.setDefaultValidationClass(CassandraUtils.ASCIITYPE);
        client.updateColumnFamily(columnFamilyInformation);
        for (String columnFamily : client.listColumnFamiliesOfCurrentUser(KEYSPACE_NAME)) {
            if (COLUMN_FAMILY_NAME.equals(columnFamily)) {
                isCFContains = true;
            }
        }
        assertTrue(isCFContains);
        columnFamilyInformation = client.getColumnFamilyInformationOfCurrentUser(KEYSPACE_NAME, COLUMN_FAMILY_NAME);
        assertEquals(columnFamilyInformation.getName(), COLUMN_FAMILY_NAME);
        assertEquals(columnFamilyInformation.getKeyspace(), KEYSPACE_NAME);
=======
        boolean isCFContains=false;
        KeyspaceInformation keyspaceInformation=client.getKeyspaceOfCurrentUser(ENVIRONMENT_NAME, KEYSPACE_NAME);
        ColumnFamilyInformation columnFamilyInformation= CassandraClientHelper.getColumnFamilyInformationOfCurrentUser(keyspaceInformation, COLUMN_FAMILY_NAME);
        columnFamilyInformation.setDefaultValidationClass(CassandraUtils.ASCIITYPE);
        client.updateColumnFamily(ENVIRONMENT_NAME, columnFamilyInformation);
        for(String columnFamily:client.listColumnFamiliesOfCurrentUser(ENVIRONMENT_NAME, KEYSPACE_NAME))
        {
            if(COLUMN_FAMILY_NAME.equals(columnFamily))
            {
                isCFContains=true;
            }
        }
        assertTrue(isCFContains);
        columnFamilyInformation=client.getColumnFamilyInformationOfCurrentUser(ENVIRONMENT_NAME, KEYSPACE_NAME, COLUMN_FAMILY_NAME);
        assertEquals(columnFamilyInformation.getName(),COLUMN_FAMILY_NAME);
        assertEquals(columnFamilyInformation.getKeyspace(),KEYSPACE_NAME);
>>>>>>> 328b51eee71db8aa23649c4e9319a8b06fb896a3
        //assertEquals(columnFamilyInformation.getId(),2);
        assertEquals(columnFamilyInformation.getGcGraceSeconds(), CassandraUtils.DEFAULT_GCGRACE);
        assertEquals(columnFamilyInformation.getMaxCompactionThreshold(), CassandraUtils.DEFAULT_MAX_COMPACTION_THRESHOLD);
        assertEquals(columnFamilyInformation.getMinCompactionThreshold(), CassandraUtils.DEFAULT_MIN_COMPACTION_THRESHOLD);
        //assertEquals(columnFamilyInformation.getRowCacheSavePeriodInSeconds(),CassandraUtils.DEFAULT_RAW_CACHE_TIME);
        //assertEquals(columnFamilyInformation.getKeyCacheSize(),0.75);
        //assertEquals(columnFamilyInformation.getRowCacheSize(),0.75);
        assertEquals(columnFamilyInformation.getType(), CassandraUtils.COLUMN_TYPE_STANDARD);
        //assertEquals(columnFamilyInformation.getComparatorType(),CassandraUtils.BYTESTYPE);
        //assertEquals(columnFamilyInformation.getDefaultValidationClass(),CassandraUtils.ASCIITYPE);
        //assertEquals(columnFamilyInformation.getSubComparatorType(),CassandraUtils.ASCIITYPE);
        //assertEquals(columnFamilyInformation.getComment(),"Test column family");
    }

    @Test(dependsOnMethods = {"addKeyspace", "updateKeyspace", "updateColumnFamily", "addColumnFamily", "addColumn", "updateColumnBySuperTenant", "deleteColumnBySuperTenant"}, description = "Add column family by super tenant")
    public void deleteColumnFamily()
            throws Exception {
<<<<<<< HEAD
        assertTrue(client.deleteColumnFamily(KEYSPACE_NAME, COLUMN_FAMILY_NAME));
=======
        assertTrue(client.deleteColumnFamily(ENVIRONMENT_NAME, KEYSPACE_NAME,COLUMN_FAMILY_NAME));
>>>>>>> 328b51eee71db8aa23649c4e9319a8b06fb896a3
    }

    @Test(dependsOnMethods = {"addKeyspace", "updateKeyspace", "updateColumnFamily", "addColumnFamily"}, description = "Add column family by super tenant")
    public void addColumn()
            throws Exception {
<<<<<<< HEAD
        KeyspaceInformation keyspaceInformation = client.getKeyspaceOfCurrentUser(KEYSPACE_NAME);
=======
        KeyspaceInformation keyspaceInformation =client.getKeyspaceOfCurrentUser(ENVIRONMENT_NAME, KEYSPACE_NAME);
>>>>>>> 328b51eee71db8aa23649c4e9319a8b06fb896a3
        ColumnFamilyInformation columnFamilyInformation = CassandraClientHelper.getColumnFamilyInformationOfCurrentUser(keyspaceInformation, COLUMN_FAMILY_NAME);
        ColumnInformation columnInformation = new ColumnInformation();
        columnInformation.setName(COLUMN_NAME);
        columnInformation.setIndexName(INDEX_NAME);
        columnInformation.setIndexType(INDEX_TYPE);
        columnInformation.setValidationClass(CassandraUtils.BYTESTYPE);
        columnFamilyInformation.addColumns(columnInformation);
<<<<<<< HEAD
        client.updateColumnFamily(columnFamilyInformation);
        columnFamilyInformation = client.getColumnFamilyInformationOfCurrentUser(KEYSPACE_NAME, COLUMN_FAMILY_NAME);
        columnInformation = CassandraClientHelper.getColumnInformation(columnFamilyInformation, COLUMN_NAME);
=======
        client.updateColumnFamily(ENVIRONMENT_NAME, columnFamilyInformation);
        columnFamilyInformation=client.getColumnFamilyInformationOfCurrentUser(ENVIRONMENT_NAME, KEYSPACE_NAME, COLUMN_FAMILY_NAME);
        columnInformation=CassandraClientHelper.getColumnInformation(columnFamilyInformation, COLUMN_NAME);
>>>>>>> 328b51eee71db8aa23649c4e9319a8b06fb896a3
        assertNotNull(columnInformation);
        assertEquals(columnInformation.getName(), COLUMN_NAME);
        assertEquals(columnInformation.getIndexName(), INDEX_NAME);
        //assertEquals(columnInformation.getIndexType(),INDEX_TYPE);
        //assertEquals(columnInformation.getValidationClass(),CassandraUtils.BYTESTYPE);
    }

    @Test(dependsOnMethods = {"addKeyspace", "updateKeyspace", "updateColumnFamily", "addColumnFamily", "addColumn", "updateColumnBySuperTenant"}, description = "Add column family by super tenant")
    public void deleteColumnBySuperTenant()
            throws Exception {
<<<<<<< HEAD
        KeyspaceInformation keyspaceInformation = client.getKeyspaceOfCurrentUser(KEYSPACE_NAME);
=======
        KeyspaceInformation keyspaceInformation =client.getKeyspaceOfCurrentUser(ENVIRONMENT_NAME, KEYSPACE_NAME);
>>>>>>> 328b51eee71db8aa23649c4e9319a8b06fb896a3
        if (keyspaceInformation != null) {
            ColumnFamilyInformation columnFamilyInformation =
                    CassandraClientHelper.getColumnFamilyInformationOfCurrentUser(keyspaceInformation, COLUMN_FAMILY_NAME);
            CassandraClientHelper.removeColumnInformation(columnFamilyInformation, COLUMN_NAME);
<<<<<<< HEAD
            client.updateColumnFamily(columnFamilyInformation);
            columnFamilyInformation = client.getColumnFamilyInformationOfCurrentUser(KEYSPACE_NAME, COLUMN_FAMILY_NAME);
            ColumnInformation columnInformation = CassandraClientHelper.getColumnInformation(columnFamilyInformation, COLUMN_NAME);
=======
            client.updateColumnFamily(ENVIRONMENT_NAME, columnFamilyInformation);
            columnFamilyInformation=client.getColumnFamilyInformationOfCurrentUser(ENVIRONMENT_NAME, KEYSPACE_NAME, COLUMN_FAMILY_NAME);
            ColumnInformation columnInformation=CassandraClientHelper.getColumnInformation(columnFamilyInformation,COLUMN_NAME);
>>>>>>> 328b51eee71db8aa23649c4e9319a8b06fb896a3
            assertNull(columnInformation);
        }
    }

    @Test(dependsOnMethods = {"addKeyspace", "updateKeyspace", "updateColumnFamily", "addColumnFamily", "addColumn"}, description = "Add column family by super tenant")
    public void updateColumnBySuperTenant()
            throws Exception {
<<<<<<< HEAD
        KeyspaceInformation keyspaceInformation = client.getKeyspaceOfCurrentUser(KEYSPACE_NAME);
        ColumnFamilyInformation columnFamilyInformation = CassandraClientHelper.getColumnFamilyInformationOfCurrentUser(keyspaceInformation, COLUMN_FAMILY_NAME);
        ColumnInformation columnInformation = CassandraClientHelper.getColumnInformation(columnFamilyInformation, COLUMN_NAME);
        client.updateColumnFamily(columnFamilyInformation);
        columnFamilyInformation = client.getColumnFamilyInformationOfCurrentUser(KEYSPACE_NAME, COLUMN_FAMILY_NAME);
        columnInformation = CassandraClientHelper.getColumnInformation(columnFamilyInformation, COLUMN_NAME);
=======
        KeyspaceInformation keyspaceInformation =client.getKeyspaceOfCurrentUser(ENVIRONMENT_NAME, KEYSPACE_NAME);
        ColumnFamilyInformation columnFamilyInformation = CassandraClientHelper.getColumnFamilyInformationOfCurrentUser(keyspaceInformation, COLUMN_FAMILY_NAME);
        ColumnInformation columnInformation=CassandraClientHelper.getColumnInformation(columnFamilyInformation,COLUMN_NAME);
        client.updateColumnFamily(ENVIRONMENT_NAME, columnFamilyInformation);
        columnFamilyInformation=client.getColumnFamilyInformationOfCurrentUser(ENVIRONMENT_NAME, KEYSPACE_NAME, COLUMN_FAMILY_NAME);
        columnInformation=CassandraClientHelper.getColumnInformation(columnFamilyInformation,COLUMN_NAME);
>>>>>>> 328b51eee71db8aa23649c4e9319a8b06fb896a3
        assertNotNull(columnInformation);
        assertEquals(columnInformation.getName(), COLUMN_NAME);
        assertEquals(columnInformation.getIndexName(), INDEX_NAME);
        //assertEquals(columnInformation.getIndexType(),INDEX_TYPE);
        //assertEquals(columnInformation.getValidationClass(),CassandraUtils.ASCIITYPE);
    }

    @DataProvider
    private static TestUserMode[][] userModeProvider() {
        return new TestUserMode[][]{
                new TestUserMode[]{TestUserMode.SUPER_TENANT_ADMIN},
                new TestUserMode[]{TestUserMode.TENANT_USER},
        };
    }

    @AfterClass(alwaysRun = true)
    public void cleanUp() {

    }
}
