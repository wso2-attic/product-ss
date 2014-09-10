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
package org.wso2.carbon.ss.integration.test.rssmanager;

import static org.testng.Assert.assertTrue;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.wso2.carbon.rssmanager.core.dto.xsd.DatabaseInfo;
import org.wso2.carbon.rssmanager.core.dto.xsd.DatabasePrivilegeTemplateInfo;
import org.wso2.carbon.rssmanager.core.dto.xsd.DatabaseUserInfo;
import org.wso2.carbon.rssmanager.core.dto.xsd.MySQLPrivilegeSetInfo;
import org.wso2.carbon.ss.SSIntegrationTest;
import org.wso2.ss.integration.common.clients.RSSManagerClient;

public class RSSMgtTestCase extends SSIntegrationTest{

    private static final Log log = LogFactory.getLog(RSSMgtTestCase.class);
    private final String serviceName = "CassandraKeyspaceAdmin";
    private String serviceEndPoint;
    private RSSManagerClient client;

    @BeforeClass(alwaysRun = true)
    public void initializeTest() throws Exception {
        super.init();
        serviceEndPoint = getServiceUrlHttp(serviceName);
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
    	database.setType("SYSTEM");
        client.createDatabase("DEFAULT", database); 

    }
    
    
    @Test(groups = "wso2.ss", description = " get database list ",dependsOnMethods={"createDB"}, priority=1)
    public void getDatabasesList() throws AxisFault {
        assertTrue(client.getDatabaseList("DEFAULT").length == 3);
    }

    @Test(groups = "wso2.ss", description = "create database user", dataProvider = "users", priority=1)
    public void createDbUser(String userName) throws AxisFault {
    	DatabaseUserInfo databaseUser = new DatabaseUserInfo();
        databaseUser.setUsername(userName);
        databaseUser.setPassword("user");
        databaseUser.setType("SYSTEM");
        client.createDatabaseUser("DEFAULT", databaseUser);
        assertTrue(true);
    }

    
    @Test(groups = "wso2.ss", description = "create databae priviledge template", dataProvider = "templates", priority=1)
    public void createPrivilegeTemplate(String tempName) throws AxisFault{
    	DatabasePrivilegeTemplateInfo template = new DatabasePrivilegeTemplateInfo();
    	template.setName(tempName);
    	MySQLPrivilegeSetInfo privileges = new MySQLPrivilegeSetInfo();
    	privileges.setAlterPriv("Y");
    	template.setPrivileges(privileges);
    	client.createDatabasePrivilegesTemplate("DEFAULT", template);
    }
    
   @Test(groups = "wso2.ss", description = "assign user to database",dependsOnMethods={"createDB","createDbUser","createPrivilegeTemplate"}, dataProvider = "attachUsers",priority=2)
    public void attachUserToDB(String user,String db, String temp) throws AxisFault{
    	client.attachUserToDatabase("DEFAULT", "WSO2RSS1", db, user, temp, "SYSTEM");
    }
    
    @Test(groups = "wso2.ss", description = "detach user from database",dependsOnMethods={"attachUserToDB"}, priority=2)
    public void detachUserFromDB() throws AxisFault{
    	client.detachUserFromDatabase("DEFAULT", "WSO2RSS1", "db1", "user1", "SYSTEM");
    	client.attachUserToDatabase("DEFAULT", "WSO2RSS1", "db1", "user1", "temp1", "SYSTEM");
    }
    
    @Test(groups = "wso2.ss", description = "delete databae priviledge template",dependsOnMethods={"createPrivilegeTemplate"}, priority=2)
    public void deletePrivilegeTemplate() throws AxisFault{
    	
    	client.dropDatabasePrivilegesTemplate("DEFAULT", "temp2");
    }
    
    @Test(groups = "wso2.ss", description = "edit databae priviledge template", dependsOnMethods={"createPrivilegeTemplate"}, priority=2)
    public void editPrivilegeTemplate() throws AxisFault{
    	DatabasePrivilegeTemplateInfo template = new DatabasePrivilegeTemplateInfo();
    	template.setName("temp3");
    	MySQLPrivilegeSetInfo privileges = new MySQLPrivilegeSetInfo();
    	privileges.setAlterPriv("N");
    	template.setPrivileges(privileges);
	
    	client.editDatabasePrivilegesTemplate("DEFAULT",template );
    	DatabasePrivilegeTemplateInfo tempInfo = client.getDatabasePrivilegesTemplate("DEFAULT", "temp3");
    	Assert.assertEquals(tempInfo.getPrivileges().getAlterPriv(), "N");
    }
    
    @Test(groups = "wso2.ss", description = "drop attached user ",dependsOnMethods={"createDB","createPrivilegeTemplate","createDbUser","attachUserToDB"}, priority=2)
    public void dropAttachedUser() throws AxisFault{        
       
    	try{
    		client.dropDatabaseUser("DEFAULT", "WSO2RSS1", "user2", "SYSTEM");
    	}catch(Exception ex){
    		return;
    	}
        Assert.fail(" Can't drop already attached user");
    }
    

    @AfterClass(alwaysRun = true)
    public void cleanUp() throws Exception {
    	DatabaseInfo[] databaseMetaDatas = client.getDatabaseList("DEFAULT");
        if (databaseMetaDatas.length > 0) {
            for (DatabaseInfo databaseMetaData : databaseMetaDatas) {
                if (databaseMetaData.getName().equals("db1")) {
                    client.dropDatabase("DEFAULT",databaseMetaData.getRssInstanceName(), "db1", "SYSTEM");
                }
            }
        }else{
        	Assert.fail(" No DB created ");
        }
        DatabaseUserInfo[] databaseUsers = client.getDatabaseUsers("DEFAULT");
        if (databaseUsers.length > 0) {
            for (DatabaseUserInfo databaseUserMetaData : databaseUsers) {
                if (databaseUserMetaData.getUsername().equals("user1")) {
                    client.dropDatabaseUser("DEFAULT",databaseUserMetaData.getRssInstanceName(), "user1", "SYSTEM");
                }
            }
        }else{
        	Assert.fail(" No User created ");
        }

    }

}
