/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.ss.integration.test.rssmanager;

import org.apache.axis2.AxisFault;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.rssmanager.common.RSSManagerHelper;
import org.wso2.carbon.rssmanager.core.dto.xsd.DatabaseInfo;
import org.wso2.carbon.rssmanager.core.dto.xsd.DatabasePrivilegeTemplateInfo;
import org.wso2.carbon.rssmanager.core.dto.xsd.DatabaseUserInfo;
import org.wso2.carbon.rssmanager.core.dto.xsd.MySQLPrivilegeSetInfo;
import org.wso2.carbon.rssmanager.core.dto.xsd.RSSInstanceInfo;
import org.wso2.carbon.rssmanager.core.dto.xsd.SSHInformationConfigInfo;
import org.wso2.carbon.rssmanager.core.dto.xsd.SnapshotConfigInfo;
import org.wso2.carbon.rssmanager.core.dto.xsd.UserDatabaseEntryInfo;
import org.wso2.carbon.ss.SSIntegrationTest;
import org.wso2.ss.integration.common.clients.RSSManagerClient;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class RSSTenantMgtTest extends SSIntegrationTest{

    private static final Log log = LogFactory.getLog(RSSMgtTestCase.class);
    private RSSManagerClient client;
    private final String DEFAULT_ENVIRONMENT_NAME = "DEFAULT";
    private final String SYSTEM_TYPE = "SYSTEM";
    private final String USER_DEFINED_TYPE = "USER_DEFINED";

    @BeforeClass(alwaysRun = true)
    public void initializeTest() throws Exception {
        super.init(TestUserMode.TENANT_USER);
        client = new RSSManagerClient(ssContext.getContextUrls().getBackEndUrl(), sessionCookie);
    }

    @DataProvider(name = "databases")
    public Object[][] databases(){
        return new Object[][] {
                { "testdb1"},
                { "testdb2"},
                {"testdb3"}
        };

    }

    @DataProvider(name = "users")
    public Object[][] users(){
        return new Object[][] {
                { "user1"},
                { "user2"},
                {"user3"}
        };
    }

    @DataProvider(name = "templates")
    public Object[][] templates(){return new Object[][] {
            { "testtemplate1"},
            { "testtemplate2"},
            {"testtemplate3"}
    };
    }

    @DataProvider(name = "attachUsers")
    public Object[][] attachUsers(){
        return new Object[][] {
                { "user1","testdb1","testtemplate1"},
                { "user2","testdb1","testtemplate1"}
        };
    }

    @Test(groups = "wso2.ss", description = "create database", dataProvider = "databases", priority=1)
    public void createDB(String dbName) throws AxisFault {
        DatabaseInfo database = new DatabaseInfo();
        database.setName(dbName);
        database.setType(SYSTEM_TYPE);
        client.createDatabase(DEFAULT_ENVIRONMENT_NAME, database);
        String qualifiedDBName = dbName + "_" + RSSManagerHelper.processDomainName(tenantInfo.getDomain());
        database = client.getDatabase(DEFAULT_ENVIRONMENT_NAME, SYSTEM_TYPE, qualifiedDBName, SYSTEM_TYPE);
        assertNotNull(database);
        assertEquals(qualifiedDBName, database.getName());
    }

    @Test(groups = "wso2.ss", description = "create rss instance")
    public void createRssInstance() throws AxisFault {
        RSSInstanceInfo rssInstanceInfo = new RSSInstanceInfo();
        rssInstanceInfo.setDbmsType("H2");
        rssInstanceInfo.setName("RSS1");
        rssInstanceInfo.setEnvironmentName(DEFAULT_ENVIRONMENT_NAME);
        rssInstanceInfo.setServerCategory("LOCAL");
        rssInstanceInfo.setInstanceType(USER_DEFINED_TYPE);
        rssInstanceInfo.setServerURL("jdbc:h2:repository/database");
        rssInstanceInfo.setUsername("root");
        rssInstanceInfo.setPassword("root");
        rssInstanceInfo.setDriverClass("org.h2.Driver");
        SnapshotConfigInfo snapshotConfigInfo = new SnapshotConfigInfo();
        snapshotConfigInfo.setTargetDirectory(System.getProperty("user.dir"));
        rssInstanceInfo.setSnapshotConfig(snapshotConfigInfo);
        rssInstanceInfo.setSshInformationConfig(new SSHInformationConfigInfo());
        client.createRSSInstance(DEFAULT_ENVIRONMENT_NAME, rssInstanceInfo);
        assertNotNull(client.getRSSInstance(DEFAULT_ENVIRONMENT_NAME, rssInstanceInfo.getName(), USER_DEFINED_TYPE));
    }

    @Test(groups = "wso2.ss", description = "create user defined database", dependsOnMethods = {"createRssInstance"})
    public void createUserDefinedDB() throws AxisFault {
        String dbName = "newdb1";
        DatabaseInfo database = new DatabaseInfo();
        database.setName(dbName);
        database.setType(USER_DEFINED_TYPE);
        RSSInstanceInfo rssInstanceInfo = client.getRSSInstance(DEFAULT_ENVIRONMENT_NAME, "RSS1", USER_DEFINED_TYPE);
        database.setRssInstanceName(rssInstanceInfo.getName());
        client.createDatabase(DEFAULT_ENVIRONMENT_NAME, database);
        database = client.getDatabase(DEFAULT_ENVIRONMENT_NAME, rssInstanceInfo.getName(), dbName,
                                      USER_DEFINED_TYPE);
        assertNotNull(database);
        assertEquals(dbName, database.getName());
    }

    @Test(groups = "wso2.ss", description = "create snapshot", dependsOnMethods = {"createUserDefinedDB"})
    public void createSnapshot() throws AxisFault {
        client.createSnapshot(DEFAULT_ENVIRONMENT_NAME, "newdb1", USER_DEFINED_TYPE);
    }

    @Test(groups = "wso2.ss", description = " get database list ", dependsOnMethods = {"createDB", "createUserDefinedDB"}, priority = 1)
    public void getDatabasesList() throws AxisFault {
        assertTrue(client.getDatabaseList(DEFAULT_ENVIRONMENT_NAME).length == 4);
    }

    @Test(groups = "wso2.ss", description = "create database user", dataProvider = "users", priority=1)
    public void createDbUser(String userName) throws AxisFault {
        DatabaseUserInfo databaseUser = new DatabaseUserInfo();
        databaseUser.setUsername(userName);
        databaseUser.setPassword("user");
        databaseUser.setType(SYSTEM_TYPE);
        client.createDatabaseUser(DEFAULT_ENVIRONMENT_NAME, databaseUser);
        String qualifiedDBUsername = userName + "_" + getDatabaseUserPostfix(tenantInfo.getDomain());
        databaseUser = client.getDatabaseUser(DEFAULT_ENVIRONMENT_NAME, SYSTEM_TYPE, qualifiedDBUsername, SYSTEM_TYPE);
        assertNotNull(databaseUser);
        assertEquals(qualifiedDBUsername, databaseUser.getName());
    }


    @Test(groups = "wso2.ss", description = "create databae priviledge template", dataProvider = "templates", priority=1)
    public void createPrivilegeTemplate(String tempName) throws AxisFault{
        DatabasePrivilegeTemplateInfo template = new DatabasePrivilegeTemplateInfo();
        template.setName(tempName);
        MySQLPrivilegeSetInfo privileges = new MySQLPrivilegeSetInfo();
        privileges.setAlterPriv("Y");
        template.setPrivileges(privileges);
        client.createDatabasePrivilegesTemplate(DEFAULT_ENVIRONMENT_NAME, template);
        template = client.getDatabasePrivilegesTemplate(DEFAULT_ENVIRONMENT_NAME, tempName);
        assertNotNull(template);
        assertEquals(tempName, template.getName());
    }

    @Test(groups = "wso2.ss", description = "assign user to database", dependsOnMethods={"createDB","createDbUser","createPrivilegeTemplate"}, dataProvider = "attachUsers",priority=2)
    public void attachUserToDB(String user,String db, String temp) throws AxisFault{
        String qualifiedDBName = db + "_" + RSSManagerHelper.processDomainName(tenantInfo.getDomain());
        String qualifiedDBUsername = user + "_" + getDatabaseUserPostfix(tenantInfo.getDomain());
        client.attachUserToDatabase(DEFAULT_ENVIRONMENT_NAME, "WSO2RSS1", qualifiedDBName, qualifiedDBUsername, temp, SYSTEM_TYPE);
        DatabaseUserInfo[] databaseUserInfos = client.getUsersAttachedToDatabase(DEFAULT_ENVIRONMENT_NAME, "WSO2RSS1", qualifiedDBName,
                SYSTEM_TYPE);
        boolean userExist = false;
        for(DatabaseUserInfo databaseUserInfo : databaseUserInfos) {
            if(qualifiedDBUsername.equalsIgnoreCase(databaseUserInfo.getName())) {
                userExist = true;
            }
        }
        assertTrue(userExist);
    }

    @Test(groups = "wso2.ss", description = "detach user from database", dependsOnMethods={"attachUserToDB"}, priority=2)
    public void detachUserFromDB() throws AxisFault{
        String qualifiedDBName = "testdb1_" + RSSManagerHelper.processDomainName(tenantInfo.getDomain());
        String qualifiedDBUsername = "user2_" + getDatabaseUserPostfix(tenantInfo.getDomain());
        client.detachUserFromDatabase(DEFAULT_ENVIRONMENT_NAME, "WSO2RSS1", qualifiedDBName, qualifiedDBUsername, SYSTEM_TYPE);
        DatabaseUserInfo[] databaseUserInfos = client.getUsersAttachedToDatabase(DEFAULT_ENVIRONMENT_NAME, "WSO2RSS1", qualifiedDBName,
                SYSTEM_TYPE);
        boolean userExist = false;
        for(DatabaseUserInfo databaseUserInfo : databaseUserInfos) {
            if(qualifiedDBUsername.equalsIgnoreCase(databaseUserInfo.getName())) {
                userExist = true;
            }
        }
        assertFalse(userExist);
    }

    @Test(groups = "wso2.ss", description = "delete database privilege template",dependsOnMethods={"createPrivilegeTemplate"}, priority=2)
    public void deletePrivilegeTemplate() throws AxisFault{
        client.dropDatabasePrivilegesTemplate(DEFAULT_ENVIRONMENT_NAME, "testtemplate1");
        boolean isExist = false;
        for(DatabasePrivilegeTemplateInfo templateInfo :client.getDatabasePrivilegesTemplates(DEFAULT_ENVIRONMENT_NAME)) {
            if("testtemplate1".equalsIgnoreCase(templateInfo.getName())) {
                isExist = true;
            }
        }
        assertFalse(isExist);
    }

    @Test(groups = "wso2.ss", description = "edit database privilege template", dependsOnMethods={"createPrivilegeTemplate"}, priority=2)
    public void editPrivilegeTemplate() throws AxisFault{
        DatabasePrivilegeTemplateInfo template;
        template = client.getDatabasePrivilegesTemplate(DEFAULT_ENVIRONMENT_NAME, "testtemplate3");
        MySQLPrivilegeSetInfo privileges = (MySQLPrivilegeSetInfo)template.getPrivileges();
        privileges.setAlterPriv("N");
        template.setPrivileges(privileges);
        client.editDatabasePrivilegesTemplate(DEFAULT_ENVIRONMENT_NAME, template);
        DatabasePrivilegeTemplateInfo tempInfo = client.getDatabasePrivilegesTemplate(DEFAULT_ENVIRONMENT_NAME,
                "testtemplate3");
        Assert.assertEquals(tempInfo.getPrivileges().getAlterPriv(), "N");
    }

    @Test(groups = "wso2.ss", expectedExceptions = AxisFault.class, description = "drop attached user ",dependsOnMethods={"createDB","createPrivilegeTemplate","createDbUser","attachUserToDB"}, priority=2)
    public void dropAttachedUser() throws AxisFault{
        String qualifiedDBUsername = "user1_" + getDatabaseUserPostfix(tenantInfo.getDomain());
        client.dropDatabaseUser(DEFAULT_ENVIRONMENT_NAME, "WSO2RSS1", qualifiedDBUsername, SYSTEM_TYPE);
    }

    @Test(groups = "wso2.ss", description = "drop attached user ",dependsOnMethods={"createDB","createPrivilegeTemplate","createDbUser","attachUserToDB"}, priority=2)
    public void dropDatabaseUser() throws AxisFault{
        String qualifiedDBUsername = "user2_" + getDatabaseUserPostfix(tenantInfo.getDomain());
        client.dropDatabaseUser(DEFAULT_ENVIRONMENT_NAME, "WSO2RSS1", qualifiedDBUsername, SYSTEM_TYPE);
        boolean isExist = false;
        for(DatabaseUserInfo databaseUserInfo :client.getDatabaseUsers(DEFAULT_ENVIRONMENT_NAME)) {
            if("user2".equalsIgnoreCase(databaseUserInfo.getName())) {
                isExist = true;
            }
        }
        assertFalse(isExist);
    }

    @Test(groups = "wso2.ss", description = "create datasource from existing database", dependsOnMethods = { "attachUserToDB" }, priority = 2, dataProvider = "databases")
    public void createDatasource(String dbName) throws AxisFault {
        String qualifidDBName = dbName + "_" + RSSManagerHelper.processDomainName(tenantInfo.getDomain());
        DatabaseUserInfo dBInfo[] = client.getUsersAttachedToDatabase(
                DEFAULT_ENVIRONMENT_NAME, "WSO2RSS1", qualifidDBName, SYSTEM_TYPE);
        if (dBInfo != null) {
            if (dBInfo.length > 0) {
                UserDatabaseEntryInfo entry = new UserDatabaseEntryInfo();
                entry.setRssInstanceName("WSO2RSS1");
                entry.setDatabaseName(qualifidDBName);
                entry.setUsername(dBInfo[0].getUsername());
                entry.setType(SYSTEM_TYPE);;
                client.createCarbonDataSource(DEFAULT_ENVIRONMENT_NAME,
                        "ds_" + System.currentTimeMillis(), entry);
                assertTrue(true);
            }
        }
    }

    @Test(groups = "wso2.ss", description = "drop database", dependsOnMethods = {"createDB", "getDatabasesList",
                                                                                 "detachUserFromDB"})
    public void dropDatabase() throws AxisFault {
        String qualifiedDBName = "testdb1_" + RSSManagerHelper.processDomainName(tenantInfo.getDomain());
        client.dropDatabase(DEFAULT_ENVIRONMENT_NAME, "WSO2RSS1", qualifiedDBName, SYSTEM_TYPE);
        boolean isExist = false;
        for (DatabaseInfo databaseInfo : client.getDatabaseList(DEFAULT_ENVIRONMENT_NAME)) {
            if (qualifiedDBName.equalsIgnoreCase(databaseInfo.getName())) {
                isExist = true;
            }
        }
        assertFalse(isExist);
    }

    @AfterClass(alwaysRun = true)
    public void cleanUp() throws Exception {
        DatabaseInfo[] databaseMetaDatas = client.getDatabaseList(DEFAULT_ENVIRONMENT_NAME);
        if (databaseMetaDatas.length > 0) {
            for (DatabaseInfo databaseMetaData : databaseMetaDatas) {
                client.dropDatabase(DEFAULT_ENVIRONMENT_NAME, databaseMetaData.getRssInstanceName(),
                                    databaseMetaData.getName(), databaseMetaData.getType());
            }
        }else{
            Assert.fail(" No DB created ");
        }
        DatabaseUserInfo[] databaseUsers = client.getDatabaseUsers(DEFAULT_ENVIRONMENT_NAME);
        if (databaseUsers.length > 0) {
            for (DatabaseUserInfo databaseUserMetaData : databaseUsers) {
                if (databaseUserMetaData.getUsername().equals("user1")) {
                    client.dropDatabaseUser(DEFAULT_ENVIRONMENT_NAME,databaseUserMetaData.getRssInstanceName(), "user1", SYSTEM_TYPE);
                }
            }
        }else{
            Assert.fail(" No User created ");
        }

    }

    private static String getDatabaseUserPostfix(String tenantDomain) {
        byte[] bytes = intToByteArray(tenantDomain.hashCode());
        return Base64.encodeBase64URLSafeString(bytes).replace("_", "$");
    }

    private static byte[] intToByteArray(int value) {
        byte[] b = new byte[6];

        for(int i = 0; i < 6; ++i) {
            int offset = (b.length - 1 - i) * 8;
            b[i] = (byte)(value >>> offset & 255);
        }

        return b;
    }
}
