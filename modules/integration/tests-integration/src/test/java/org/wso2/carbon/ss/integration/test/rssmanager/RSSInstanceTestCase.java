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
import org.testng.annotations.Test;
import org.wso2.carbon.rssmanager.core.dto.xsd.DatabaseUserInfo;
import org.wso2.carbon.rssmanager.core.dto.xsd.RSSInstanceInfo;
import org.wso2.carbon.rssmanager.core.dto.xsd.SSHInformationConfigInfo;
import org.wso2.carbon.rssmanager.core.dto.xsd.SnapshotConfigInfo;
import org.wso2.carbon.ss.SSIntegrationTest;
import org.wso2.ss.integration.common.clients.RSSManagerClient;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class RSSInstanceTestCase extends SSIntegrationTest {

	private static final Log log = LogFactory.getLog(RSSInstanceTestCase.class);
	private RSSManagerClient client;
	private final String DEFAULT_ENVIRONMENT_NAME = "DEFAULT";
	private final String USER_DEFINED_TYPE = "USER_DEFINED";
	private final String RSS_INSTANCE_NAME_1 = "TESTRSS1";
	private final String RSS_INSTANCE_NAME_2 = "TESTRSS2";
	private final String DATABASE_USER_NAME = "testuser";

	@BeforeClass(alwaysRun = true)
	public void initializeTest() throws Exception {
		super.init();
		client = new RSSManagerClient(ssContext.getContextUrls().getBackEndUrl(), sessionCookie);
	}

	@Test(groups = "wso2.ss", description = "Two database users with same name does not allow to create on different RSS instances created with USER_DEFINED instance type")
	public void createUsersWithSameUsernameInTwoUserDefinedRSSInstances() throws AxisFault {
		RSSInstanceInfo rssInstanceInfo = new RSSInstanceInfo();
		rssInstanceInfo.setDbmsType("H2");
		rssInstanceInfo.setRssInstanceName(RSS_INSTANCE_NAME_1);
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
		rssInstanceInfo = client.getRSSInstance(DEFAULT_ENVIRONMENT_NAME, rssInstanceInfo.getRssInstanceName(),
				USER_DEFINED_TYPE);
		assertNotNull(rssInstanceInfo);
		assertEquals(rssInstanceInfo.getRssInstanceName(), RSS_INSTANCE_NAME_1);
		assertEquals(rssInstanceInfo.getDbmsType(), "H2");
		rssInstanceInfo = new RSSInstanceInfo();
		rssInstanceInfo.setDbmsType("H2");
		rssInstanceInfo.setRssInstanceName(RSS_INSTANCE_NAME_2);
		rssInstanceInfo.setEnvironmentName(DEFAULT_ENVIRONMENT_NAME);
		rssInstanceInfo.setServerCategory("LOCAL");
		rssInstanceInfo.setInstanceType(USER_DEFINED_TYPE);
		rssInstanceInfo.setServerURL("jdbc:h2:repository/database");
		rssInstanceInfo.setUsername("root");
		rssInstanceInfo.setPassword("root");
		rssInstanceInfo.setDriverClass("org.h2.Driver");
		snapshotConfigInfo = new SnapshotConfigInfo();
		snapshotConfigInfo.setTargetDirectory(System.getProperty("user.dir"));
		rssInstanceInfo.setSnapshotConfig(snapshotConfigInfo);
		rssInstanceInfo.setSshInformationConfig(new SSHInformationConfigInfo());
		client.createRSSInstance(DEFAULT_ENVIRONMENT_NAME, rssInstanceInfo);
		rssInstanceInfo = client.getRSSInstance(DEFAULT_ENVIRONMENT_NAME, rssInstanceInfo.getRssInstanceName(),USER_DEFINED_TYPE);
		assertNotNull(rssInstanceInfo);
		assertEquals(rssInstanceInfo.getRssInstanceName(), RSS_INSTANCE_NAME_2);
		assertEquals(rssInstanceInfo.getDbmsType(), "H2");
		DatabaseUserInfo databaseUser = new DatabaseUserInfo();
		databaseUser.setUsername(DATABASE_USER_NAME);
		databaseUser.setPassword("user");
		databaseUser.setType(USER_DEFINED_TYPE);
		databaseUser.setRssInstanceName(RSS_INSTANCE_NAME_1);
		client.createDatabaseUser(DEFAULT_ENVIRONMENT_NAME, databaseUser);
		databaseUser = client.getDatabaseUser(DEFAULT_ENVIRONMENT_NAME, RSS_INSTANCE_NAME_1, DATABASE_USER_NAME, USER_DEFINED_TYPE);
		assertNotNull(databaseUser);
		assertEquals(DATABASE_USER_NAME, databaseUser.getName());
		databaseUser = new DatabaseUserInfo();
		databaseUser.setUsername(DATABASE_USER_NAME);
		databaseUser.setPassword("user");
		databaseUser.setType(USER_DEFINED_TYPE);
		databaseUser.setRssInstanceName(RSS_INSTANCE_NAME_2);
		client.createDatabaseUser(DEFAULT_ENVIRONMENT_NAME, databaseUser);
		databaseUser = client.getDatabaseUser(DEFAULT_ENVIRONMENT_NAME, RSS_INSTANCE_NAME_2, DATABASE_USER_NAME,
				USER_DEFINED_TYPE);
		assertNotNull(databaseUser);
		assertEquals(DATABASE_USER_NAME, databaseUser.getName());
	}

	@AfterClass(alwaysRun = true)
	public void cleanUp() throws Exception {
		client.dropDatabaseUser(DEFAULT_ENVIRONMENT_NAME, RSS_INSTANCE_NAME_1, DATABASE_USER_NAME, USER_DEFINED_TYPE);
		client.dropDatabaseUser(DEFAULT_ENVIRONMENT_NAME, RSS_INSTANCE_NAME_2, DATABASE_USER_NAME, USER_DEFINED_TYPE);
		client.dropRSSInstance(DEFAULT_ENVIRONMENT_NAME, RSS_INSTANCE_NAME_1, USER_DEFINED_TYPE);
		client.dropRSSInstance(DEFAULT_ENVIRONMENT_NAME, RSS_INSTANCE_NAME_2, USER_DEFINED_TYPE);
	}
}
