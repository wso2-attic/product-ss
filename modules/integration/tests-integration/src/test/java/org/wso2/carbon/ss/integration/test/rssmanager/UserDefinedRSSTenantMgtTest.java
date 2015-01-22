/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

public class UserDefinedRSSTenantMgtTest extends SSIntegrationTest {

    private static final Log log = LogFactory.getLog(RSSMgtTestCase.class);
    private RSSManagerClient client;
    private final String DEFAULT_ENVIRONMENT_NAME = "DEFAULT";
    private final String SYSTEM_TYPE = "SYSTEM";
    private final String USER_DEFINED_TYPE = "USER_DEFINED";

    @BeforeClass(alwaysRun = true)
    public void initializeTest() throws Exception {
        super.init(TestUserMode.TENANT_USER);
        client = new RSSManagerClient(ssContext.getContextUrls().getBackEndUrl(), sessionCookie);
        createPrivilegeTemplate();
    }

    @DataProvider(name = "databases")
    public Object[][] databases() {
        return new Object[][]{
                {"UDtestdb1"},
                {"UDtestdb2"},
                {"UDtestdb3"}
        };

    }

    @DataProvider(name = "users")
    public Object[][] users() {
        return new Object[][]{
                {"UDuser1"},
                {"UDuser2"},
                {"UDuser3"}
        };
    }

    @DataProvider(name = "templates")
    public Object[][] templates() {
        return new Object[][]{
                {"UDtemplate1"},
                {"UDtemplate2"},
                {"UDtemplate3"}
        };
    }

    @DataProvider(name = "attachUsers")
    public Object[][] attachUsers() {
        return new Object[][]{
                {"UDuser1", "UDtestdb1", "UDtemplate1"},
                {"UDuser2", "UDtestdb1", "UDtemplate1"}
        };
    }

    @Test(groups = "wso2.ss", description = "create rss instance")
    public void createRssInstance() throws AxisFault {
        RSSInstanceInfo rssInstanceInfo = new RSSInstanceInfo();
        rssInstanceInfo.setDbmsType("H2");
        rssInstanceInfo.setRssInstanceName("UDRSS2");
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
        rssInstanceInfo = client.getRSSInstance(DEFAULT_ENVIRONMENT_NAME, rssInstanceInfo.getRssInstanceName(), USER_DEFINED_TYPE);
        assertNotNull(rssInstanceInfo);
        assertEquals(rssInstanceInfo.getRssInstanceName(), "UDRSS2");
        assertEquals(rssInstanceInfo.getDbmsType(), "H2");
    }

    @Test(groups = "wso2.ss", description = "create user defined database", dataProvider = "databases",
            dependsOnMethods = {"createRssInstance"})
    public void createDB(String dbName) throws AxisFault {
        DatabaseInfo database = new DatabaseInfo();
        database.setName(dbName);
        database.setType(USER_DEFINED_TYPE);
        RSSInstanceInfo rssInstanceInfo = client.getRSSInstance(DEFAULT_ENVIRONMENT_NAME, "UDRSS2", USER_DEFINED_TYPE);
        database.setRssInstanceName(rssInstanceInfo.getRssInstanceName());
        client.createDatabase(DEFAULT_ENVIRONMENT_NAME, database);
        database = client.getDatabase(DEFAULT_ENVIRONMENT_NAME, "UDRSS2", dbName, USER_DEFINED_TYPE);
        assertNotNull(database);
        assertEquals(dbName, database.getName());
    }

    @Test(groups = "wso2.ss", description = "get database list", dependsOnMethods = {"createDB"})
    public void getDatabasesList() throws AxisFault {
        assertTrue(client.getDatabaseList(DEFAULT_ENVIRONMENT_NAME).length == 3);
    }

    @Test(groups = "wso2.ss", description = "create database user", dataProvider = "users")
    public void createDbUser(String userName) throws AxisFault {
        DatabaseUserInfo databaseUser = new DatabaseUserInfo();
        databaseUser.setUsername(userName);
        databaseUser.setPassword("user");
        databaseUser.setType(USER_DEFINED_TYPE);
        databaseUser.setRssInstanceName("UDRSS2");
        client.createDatabaseUser(DEFAULT_ENVIRONMENT_NAME, databaseUser);
        databaseUser = client.getDatabaseUser(DEFAULT_ENVIRONMENT_NAME,"UDRSS2", userName, USER_DEFINED_TYPE);
        assertNotNull(databaseUser);
        assertEquals(userName, databaseUser.getName());
    }

    @Test(groups = "wso2.ss", description = "create snapshot", dependsOnMethods = {"createDB"})
    public void createSnapshot() throws AxisFault {
        client.createSnapshot(DEFAULT_ENVIRONMENT_NAME, "UDtestdb1", USER_DEFINED_TYPE);
    }

    @Test(groups = "wso2.ss", description = "assign user to database", dependsOnMethods = {"createDB", "createDbUser"},
            dataProvider = "attachUsers")
    public void attachUserToDB(String user, String db, String temp) throws AxisFault {
        client.attachUserToDatabase(DEFAULT_ENVIRONMENT_NAME, "UDRSS2", db, user, temp, USER_DEFINED_TYPE);
        DatabaseUserInfo[] databaseUserInfos = client.getUsersAttachedToDatabase(DEFAULT_ENVIRONMENT_NAME,
                                                                                 "UDRSS2", db, USER_DEFINED_TYPE);
        boolean userExist = false;
        for (DatabaseUserInfo databaseUserInfo : databaseUserInfos) {
            if (user.equalsIgnoreCase(databaseUserInfo.getName())) {
                userExist = true;
            }
        }
        assertTrue(userExist);
    }

    @Test(groups = "wso2.ss", description = "detach user from database", dependsOnMethods = {"attachUserToDB"})
    public void detachUserFromDB() throws AxisFault {
        client.detachUserFromDatabase(DEFAULT_ENVIRONMENT_NAME, "UDRSS2", "UDtestdb1", "UDuser2", USER_DEFINED_TYPE);
        DatabaseUserInfo[] databaseUserInfos = client.getUsersAttachedToDatabase(DEFAULT_ENVIRONMENT_NAME,
                                                                                 "UDRSS2", "UDtestdb1", USER_DEFINED_TYPE);
        boolean userExist = false;
        for (DatabaseUserInfo databaseUserInfo : databaseUserInfos) {
            if ("UDuser2".equalsIgnoreCase(databaseUserInfo.getName())) {
                userExist = true;
            }
        }
        assertFalse(userExist);
    }

    @Test(groups = "wso2.ss", expectedExceptions = AxisFault.class, description = "drop attached user ",
            dependsOnMethods = {"attachUserToDB"})
    public void dropAttachedUser() throws AxisFault {
        client.dropDatabaseUser(DEFAULT_ENVIRONMENT_NAME, "UDRSS2", "UDuser1", USER_DEFINED_TYPE);
    }

    @Test(groups = "wso2.ss", description = "drop detached user ", dependsOnMethods = {"detachUserFromDB"})
    public void dropDatabaseUser() throws AxisFault {
        client.dropDatabaseUser(DEFAULT_ENVIRONMENT_NAME, "UDRSS2", "UDuser2", USER_DEFINED_TYPE);
        boolean isExist = false;
        for (DatabaseUserInfo databaseUserInfo : client.getDatabaseUsers(DEFAULT_ENVIRONMENT_NAME)) {
            if ("UDuser2".equalsIgnoreCase(databaseUserInfo.getName())) {
                isExist = true;
            }
        }
        assertFalse(isExist);
    }

    @Test(groups = "wso2.ss", description = "create datasource from existing database", dependsOnMethods = {
            "attachUserToDB"}, dataProvider = "databases")
    public void createDatasource(String dbName) throws AxisFault {
        DatabaseUserInfo dBInfo[] = client.getUsersAttachedToDatabase(
                DEFAULT_ENVIRONMENT_NAME, "UDRSS2", dbName, USER_DEFINED_TYPE);
        if (dBInfo != null) {
            if (dBInfo.length > 0) {
                UserDatabaseEntryInfo entry = new UserDatabaseEntryInfo();
                entry.setRssInstanceName("UDRSS2");
                entry.setDatabaseName(dbName);
                log.info("\n db = " + dbName + "\n");
                entry.setUsername(dBInfo[0].getUsername());
                entry.setType(USER_DEFINED_TYPE);
                client.createCarbonDataSource(DEFAULT_ENVIRONMENT_NAME, "ds_" + System.currentTimeMillis(), entry);
                assertTrue(true);
            }
        }
    }

    @Test(groups = "wso2.ss", description = "drop database", dependsOnMethods = {"createDB", "getDatabasesList",
                                                                                 "detachUserFromDB"})
    public void dropDatabase() throws AxisFault {
        client.dropDatabase(DEFAULT_ENVIRONMENT_NAME, "UDRSS2", "UDtestdb1", USER_DEFINED_TYPE);
        boolean isExist = false;
        for (DatabaseInfo databaseInfo : client.getDatabaseList(DEFAULT_ENVIRONMENT_NAME)) {
            if ("UDdb1".equalsIgnoreCase(databaseInfo.getName())) {
                isExist = true;
            }
        }
        assertFalse(isExist);
    }


    @AfterClass(alwaysRun = true)
    public void cleanUp() throws Exception {
        client.dropDatabase(DEFAULT_ENVIRONMENT_NAME, "UDRSS2", "UDtestdb2", USER_DEFINED_TYPE);
        client.dropDatabase(DEFAULT_ENVIRONMENT_NAME, "UDRSS2", "UDtestdb3", USER_DEFINED_TYPE);
        client.dropDatabaseUser(DEFAULT_ENVIRONMENT_NAME, "UDRSS2", "UDuser1", USER_DEFINED_TYPE);
        client.dropDatabaseUser(DEFAULT_ENVIRONMENT_NAME, "UDRSS2", "UDuser3", USER_DEFINED_TYPE);
        client.dropDatabasePrivilegesTemplate(DEFAULT_ENVIRONMENT_NAME, "UDtemplate1");
    }

    private void createPrivilegeTemplate() throws AxisFault {
        DatabasePrivilegeTemplateInfo template = new DatabasePrivilegeTemplateInfo();
        template.setName("UDtemplate1");
        MySQLPrivilegeSetInfo privileges = new MySQLPrivilegeSetInfo();
        privileges.setAlterPriv("Y");
        template.setPrivileges(privileges);
        client.createDatabasePrivilegesTemplate(DEFAULT_ENVIRONMENT_NAME, template);
    }

}
