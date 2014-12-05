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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Date;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.h2.jaqu.Db;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.rssmanager.core.dto.xsd.DatabaseInfo;
import org.wso2.carbon.rssmanager.core.dto.xsd.DatabasePrivilegeTemplateInfo;
import org.wso2.carbon.rssmanager.core.dto.xsd.DatabaseUserInfo;
import org.wso2.carbon.rssmanager.core.dto.xsd.MySQLPrivilegeSetInfo;
import org.wso2.carbon.rssmanager.core.dto.xsd.UserDatabaseEntryInfo;
import org.wso2.carbon.ss.SSIntegrationTest;
import org.wso2.ss.integration.common.clients.RSSManagerClient;

public class RSSTenantMgtTest extends SSIntegrationTest{

    private static final Log log = LogFactory.getLog(RSSMgtTestCase.class);
    private RSSManagerClient client;
    private final String DEFAULT_ENVIRONMENT_NAME = "DEFAULT";
    private final String SYSTEM_TYPE = "SYSTEM";

    @BeforeClass(alwaysRun = true)
    public void initializeTest() throws Exception {
        super.init(TestUserMode.TENANT_USER);
        client = new RSSManagerClient(ssContext.getContextUrls().getBackEndUrl(), sessionCookie);
    }

    @DataProvider(name = "databases")
    public Object[][] databases(){
        return new Object[][] {
                { "db1"},
                { "db2"},
                {"db3"}
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
            { "temp1"},
            { "temp2"},
            {"temp3"}
    };
    }
    
    @DataProvider(name = "attachUsers")
    public Object[][] attachUsers(){
        return new Object[][] {
                { "user1","db1","temp1"},
                { "user2","db1","temp1"}
        };
    }

    @Test(groups = "wso2.ss", description = "create database", dataProvider = "databases", priority=1)
    public void createDB(String dbName) throws AxisFault {
        DatabaseInfo database = new DatabaseInfo();
        database.setName(dbName);
        database.setType(SYSTEM_TYPE);
        client.createDatabase(DEFAULT_ENVIRONMENT_NAME, database);
        database = client.getDatabase(DEFAULT_ENVIRONMENT_NAME, SYSTEM_TYPE, dbName, SYSTEM_TYPE);
        assertNotNull(database);
        assertEquals(dbName, database.getName());
    }
    
    
    @Test(groups = "wso2.ss", description = " get database list ",dependsOnMethods={"createDB"}, priority=1)
    public void getDatabasesList() throws AxisFault {
        assertTrue(client.getDatabaseList(DEFAULT_ENVIRONMENT_NAME).length == 3);
    }

    @Test(groups = "wso2.ss", description = "create database user", dataProvider = "users", priority=1)
    public void createDbUser(String userName) throws AxisFault {
        DatabaseUserInfo databaseUser = new DatabaseUserInfo();
        databaseUser.setUsername(userName);
        databaseUser.setPassword("user");
        databaseUser.setType(SYSTEM_TYPE);
        client.createDatabaseUser(DEFAULT_ENVIRONMENT_NAME, databaseUser);
        databaseUser = client.getDatabaseUser(DEFAULT_ENVIRONMENT_NAME, SYSTEM_TYPE, userName, SYSTEM_TYPE);
        assertNotNull(databaseUser);
        assertEquals(userName, databaseUser.getName());
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
        client.attachUserToDatabase(DEFAULT_ENVIRONMENT_NAME, "WSO2RSS1", db, user, temp, SYSTEM_TYPE);
        DatabaseUserInfo[] databaseUserInfos = client.getUsersAttachedToDatabase(DEFAULT_ENVIRONMENT_NAME, "WSO2RSS1", db, SYSTEM_TYPE);
        boolean userExist = false;
        for(DatabaseUserInfo databaseUserInfo : databaseUserInfos) {
            if(user.equalsIgnoreCase(databaseUserInfo.getName())) {
                userExist = true;
            }
        }
        assertTrue(userExist);
    }
    
    @Test(groups = "wso2.ss", description = "detach user from database", dependsOnMethods={"attachUserToDB"}, priority=2)
    public void detachUserFromDB() throws AxisFault{
        client.detachUserFromDatabase(DEFAULT_ENVIRONMENT_NAME, "WSO2RSS1", "db1", "user2", SYSTEM_TYPE);
        DatabaseUserInfo[] databaseUserInfos = client.getUsersAttachedToDatabase(DEFAULT_ENVIRONMENT_NAME, "WSO2RSS1", "db1", SYSTEM_TYPE);
        boolean userExist = false;
        for(DatabaseUserInfo databaseUserInfo : databaseUserInfos) {
            if("user2".equalsIgnoreCase(databaseUserInfo.getName())) {
                userExist = true;
            }
        }
        assertFalse(userExist);
    }
    
    @Test(groups = "wso2.ss", description = "delete database privilege template",dependsOnMethods={"createPrivilegeTemplate"}, priority=2)
    public void deletePrivilegeTemplate() throws AxisFault{
        client.dropDatabasePrivilegesTemplate(DEFAULT_ENVIRONMENT_NAME, "temp2");
        boolean isExist = false;
        for(DatabasePrivilegeTemplateInfo templateInfo :client.getDatabasePrivilegesTemplates(DEFAULT_ENVIRONMENT_NAME)) {
            if("temp2".equalsIgnoreCase(templateInfo.getName())) {
                isExist = true;
            }
        }
        assertFalse(isExist);
    }
    
    @Test(groups = "wso2.ss", description = "edit database privilege template", dependsOnMethods={"createPrivilegeTemplate"}, priority=2)
    public void editPrivilegeTemplate() throws AxisFault{
        DatabasePrivilegeTemplateInfo template;
        template = client.getDatabasePrivilegesTemplate(DEFAULT_ENVIRONMENT_NAME, "temp3");
        MySQLPrivilegeSetInfo privileges = (MySQLPrivilegeSetInfo)template.getPrivileges();
        privileges.setAlterPriv("N");
        template.setPrivileges(privileges);
        client.editDatabasePrivilegesTemplate(DEFAULT_ENVIRONMENT_NAME, template);
        DatabasePrivilegeTemplateInfo tempInfo = client.getDatabasePrivilegesTemplate(DEFAULT_ENVIRONMENT_NAME, "temp3");
        Assert.assertEquals(tempInfo.getPrivileges().getAlterPriv(), "N");
    }
    
    @Test(groups = "wso2.ss", expectedExceptions = AxisFault.class, description = "drop attached user ",dependsOnMethods={"createDB","createPrivilegeTemplate","createDbUser","attachUserToDB"}, priority=2)
    public void dropAttachedUser() throws AxisFault{
        client.dropDatabaseUser(DEFAULT_ENVIRONMENT_NAME, "WSO2RSS1", "user1", SYSTEM_TYPE);
    }

    @Test(groups = "wso2.ss", description = "drop attached user ",dependsOnMethods={"createDB","createPrivilegeTemplate","createDbUser","attachUserToDB"}, priority=2)
    public void dropDatabaseUser() throws AxisFault{
        client.dropDatabaseUser(DEFAULT_ENVIRONMENT_NAME, "WSO2RSS1", "user2", SYSTEM_TYPE);
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
        DatabaseUserInfo dBInfo[] = client.getUsersAttachedToDatabase(
                DEFAULT_ENVIRONMENT_NAME, "WSO2RSS1", dbName, SYSTEM_TYPE);
        if (dBInfo != null) {
            if (dBInfo.length > 0) {
                UserDatabaseEntryInfo entry = new UserDatabaseEntryInfo();
                entry.setRssInstanceName("WSO2RSS1");
                entry.setDatabaseName(dbName);
                log.info("\n db = " + dbName + "\n");
                entry.setUsername(dBInfo[0].getUsername());
                entry.setType(SYSTEM_TYPE);;
                client.createCarbonDataSource(DEFAULT_ENVIRONMENT_NAME,
                        "ds_" + System.currentTimeMillis(), entry);
                assertTrue(true);
            }
        }
    }
    
    

    @AfterClass(alwaysRun = true)
    public void cleanUp() throws Exception {
        DatabaseInfo[] databaseMetaDatas = client.getDatabaseList(DEFAULT_ENVIRONMENT_NAME);
        if (databaseMetaDatas.length > 0) {
            for (DatabaseInfo databaseMetaData : databaseMetaDatas) {
                if (databaseMetaData.getName().equals("db1")) {
                    client.dropDatabase(DEFAULT_ENVIRONMENT_NAME,databaseMetaData.getRssInstanceName(), "db1", SYSTEM_TYPE);
                }
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

}
