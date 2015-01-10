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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.integration.common.admin.client.AuthenticatorClient;
import org.wso2.carbon.integration.common.admin.client.UserManagementClient;
import org.wso2.carbon.rssmanager.core.dto.xsd.DatabaseInfo;
import org.wso2.carbon.rssmanager.core.dto.xsd.DatabasePrivilegeTemplateInfo;
import org.wso2.carbon.rssmanager.core.dto.xsd.DatabaseUserInfo;
import org.wso2.carbon.rssmanager.core.dto.xsd.MySQLPrivilegeSetInfo;
import org.wso2.carbon.ss.SSIntegrationTest;
import org.wso2.carbon.user.mgt.stub.UserAdminUserAdminException;
import org.wso2.ss.integration.common.clients.RSSManagerClient;

import javax.xml.xpath.XPathExpressionException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class RSSRBACAttachDatabaseUserPermissionTest extends SSIntegrationTest {

	private static final Log log = LogFactory.getLog(RSSMgtTestCase.class);

	private RSSManagerClient rssManagerClient;
	private final String DEFAULT_ENVIRONMENT_NAME = "DEFAULT";
	private final String SYSTEM_TYPE = "SYSTEM";
	private UserManagementClient userManagementClient;
	private final String RSS_USER_WITH_ADD_PERMISSION = "rsstestuser1";
	private final String RSS_USER_WITH_ADD_EDIT_PERMISSION = "rsstestuser2";
	private final String RSS_USER_WITH_ALL_ATTACH_DATABASE_USER_PERMISSIONS = "rsstestuser3";
	private final String RSS_USER_PASSWORD = "password";
	private final String RSS_ROLE_NAME_WITH_ADD_ATTACH_USER_PRIVILEGE = "rsstestrole1";
	private final String RSS_ROLE_NAME_WITH_ADD_EDIT_ATTACH_USER_PRIVILEGES = "rsstestrole2";
	private final String RSS_ROLE_NAME_WITH_ALL_ATTACH_USER_PRIVILEGES = "rsstestrole3";
	private String newUsrSessionCookie;
	private AuthenticatorClient authenticatorClient;
	private String databaseName = "testdbA";
	private String databaseUserName = "testUserA";
	private String privilegeTemplateName = "testTemplateA";

	@BeforeClass(alwaysRun = true)
	public void initializeTest() throws Exception {
		super.init();
		userManagementClient = new UserManagementClient(ssContext.getContextUrls().getBackEndUrl(), sessionCookie);
		authenticatorClient = new AuthenticatorClient(ssContext.getContextUrls().getBackEndUrl());
		initializeUsersAndRoles();
	}

	private void initializeUsersAndRoles() throws RemoteException, UserAdminUserAdminException, XPathExpressionException,
			LoginAuthenticationExceptionException {
		List<String> permissions = new ArrayList<String>();
		permissions.add("/permission/admin");
		permissions.add("/permission/admin/configure/datasources");
		permissions.add("/permission/admin/configure/security");
		permissions.add("/permission/admin/configure/security/usermgt");
		permissions.add("/permission/admin/configure/security/usermgt/passwords");
		permissions.add("/permission/admin/configure/security/usermgt/profiles");
		permissions.add("/permission/admin/configure/security/usermgt/users");
		permissions.add("/permission/admin/configure/theme");
		permissions.add("/permission/admin/login");
		permissions.add("/permission/admin/manage");
		permissions.add("/permission/admin/manage/add");
		permissions.add("/permission/admin/manage/extensions");
		permissions.add("/permission/admin/manage/extensions/add");
		permissions.add("/permission/admin/manage/extensions/list");
		permissions.add("/permission/admin/manage/modify");
		permissions.add("/permission/admin/manage/resources");
		permissions.add("/permission/admin/manage/resources/browse");
		permissions.add("/permission/admin/manage/resources/notifications");
		permissions.add("/permission/admin/manage/search");
		permissions.add("/permission/admin/manage/search/advanced-search");
		permissions.add("/permission/admin/manage/search/resources");
		permissions.add("/permission/admin/monitor");
		permissions.add("/permission/admin/monitor/logging");
		permissions.add("/permission/admin/monitor/tenantUsage/customUsage");
		permissions.add("/permission/applications/RSSManager/All Environments/DEFAULT/System Instance/Database/Add");
		permissions.add("/permission/applications/RSSManager/All Environments/DEFAULT/System Instance/Database/Delete");
		permissions.add("/permission/applications/RSSManager/All Environments/DEFAULT/System Instance/Database User/Add");
		permissions.add("/permission/applications/RSSManager/All Environments/DEFAULT/System Instance/Database "
		                + "User/Delete");
		permissions.add(
				"/permission/applications/RSSManager/All Environments/DEFAULT/System Instance/Attach Database User/Add");
		userManagementClient.addRole(RSS_ROLE_NAME_WITH_ADD_ATTACH_USER_PRIVILEGE, null, permissions.toArray(
				new String[permissions.size()]));
		userManagementClient.addUser(RSS_USER_WITH_ADD_PERMISSION, RSS_USER_PASSWORD, new String[] {
				RSS_ROLE_NAME_WITH_ADD_ATTACH_USER_PRIVILEGE }, null);
		permissions.add(
				"/permission/applications/RSSManager/All Environments/DEFAULT/System Instance/Attach Database User/Edit");
		userManagementClient.addRole(RSS_ROLE_NAME_WITH_ADD_EDIT_ATTACH_USER_PRIVILEGES, null, permissions.toArray(new
				String[permissions.size()]));
		userManagementClient.addUser(RSS_USER_WITH_ADD_EDIT_PERMISSION, RSS_USER_PASSWORD, new String[]
				{ RSS_ROLE_NAME_WITH_ADD_EDIT_ATTACH_USER_PRIVILEGES }, null);
		permissions.add(
				"/permission/applications/RSSManager/All Environments/DEFAULT/System Instance/Attach Database User/Delete");
		userManagementClient.addRole(RSS_ROLE_NAME_WITH_ALL_ATTACH_USER_PRIVILEGES, null, permissions.toArray(
				new String[permissions.size()]));
		userManagementClient.addUser(RSS_USER_WITH_ALL_ATTACH_DATABASE_USER_PERMISSIONS, RSS_USER_PASSWORD, new String[] {
				RSS_ROLE_NAME_WITH_ALL_ATTACH_USER_PRIVILEGES }, null);
		createPrivilegeTemplate();
	}

	@Test(groups = "wso2.ss", description = "create database with privileged user")
	public void createDB()
			throws RemoteException, LoginAuthenticationExceptionException, XPathExpressionException {
		newUsrSessionCookie = authenticatorClient.login(RSS_USER_WITH_ADD_PERMISSION, RSS_USER_PASSWORD, "localhost");
		rssManagerClient = new RSSManagerClient(ssContext.getContextUrls().getBackEndUrl(), newUsrSessionCookie);
		DatabaseInfo database = new DatabaseInfo();
		database.setName(databaseName);
		database.setType(SYSTEM_TYPE);
		rssManagerClient.createDatabase(DEFAULT_ENVIRONMENT_NAME, database);
		database = rssManagerClient.getDatabase(DEFAULT_ENVIRONMENT_NAME, SYSTEM_TYPE, databaseName, SYSTEM_TYPE);
		assertNotNull(database);
		assertEquals(databaseName, database.getName());
	}

	@Test(groups = "wso2.ss", description = "create database user")
	public void createDbUser() throws RemoteException, LoginAuthenticationExceptionException, XPathExpressionException {
		newUsrSessionCookie = authenticatorClient.login(RSS_USER_WITH_ALL_ATTACH_DATABASE_USER_PERMISSIONS,
				RSS_USER_PASSWORD, "localhost");
		rssManagerClient = new RSSManagerClient(ssContext.getContextUrls().getBackEndUrl(), newUsrSessionCookie);
		DatabaseUserInfo databaseUser = new DatabaseUserInfo();
		databaseUser.setUsername(databaseUserName);
		databaseUser.setPassword("user");
		databaseUser.setType(SYSTEM_TYPE);
		rssManagerClient.createDatabaseUser(DEFAULT_ENVIRONMENT_NAME, databaseUser);
		databaseUser = rssManagerClient.getDatabaseUser(DEFAULT_ENVIRONMENT_NAME, SYSTEM_TYPE, databaseUserName,
				SYSTEM_TYPE);
		assertNotNull(databaseUser);
		assertEquals(databaseUserName, databaseUser.getName());
	}

	@Test(groups = "wso2.ss", description = "assign user to database by privilege user", dependsOnMethods = { "createDB",
			"createDbUser" })
	public void attachUserToDB()
			throws RemoteException, LoginAuthenticationExceptionException, XPathExpressionException {
		newUsrSessionCookie = authenticatorClient.login(RSS_USER_WITH_ADD_PERMISSION, RSS_USER_PASSWORD, "localhost");
		rssManagerClient = new RSSManagerClient(ssContext.getContextUrls().getBackEndUrl(), newUsrSessionCookie);
		rssManagerClient.attachUserToDatabase(DEFAULT_ENVIRONMENT_NAME, "WSO2RSS1", databaseName, databaseUserName,
				privilegeTemplateName,
				SYSTEM_TYPE);
		DatabaseUserInfo[] databaseUserInfos = rssManagerClient.getUsersAttachedToDatabase(DEFAULT_ENVIRONMENT_NAME,
				"WSO2RSS1", databaseName, SYSTEM_TYPE);
		boolean userExist = false;
		for (DatabaseUserInfo databaseUserInfo : databaseUserInfos) {
			if (databaseUserName.equalsIgnoreCase(databaseUserInfo.getName())) {
				userExist = true;
			}
		}
		assertTrue(userExist);
	}

	@Test(groups = "wso2.ss", expectedExceptions = Exception.class, description = "detach user from database",
			dependsOnMethods = {
					"attachUserToDB" }, priority = 2)
	public void detachUserFromDBByUnprivilegeUser()
			throws RemoteException, LoginAuthenticationExceptionException, XPathExpressionException {
		newUsrSessionCookie = authenticatorClient.login(RSS_USER_WITH_ADD_PERMISSION, RSS_USER_PASSWORD, "localhost");
		rssManagerClient = new RSSManagerClient(ssContext.getContextUrls().getBackEndUrl(), newUsrSessionCookie);
		rssManagerClient.detachUserFromDatabase(DEFAULT_ENVIRONMENT_NAME, "WSO2RSS1", databaseName, databaseUserName,
				SYSTEM_TYPE);
	}

	@Test(groups = "wso2.ss", description = "detach user from database", dependsOnMethods = {
			"attachUserToDB", "detachUserFromDBByUnprivilegeUser" }, priority = 2)
	public void detachUserFromDBByPrivilegeUser()
			throws RemoteException, LoginAuthenticationExceptionException, XPathExpressionException {
		newUsrSessionCookie = authenticatorClient.login(RSS_USER_WITH_ALL_ATTACH_DATABASE_USER_PERMISSIONS,
				RSS_USER_PASSWORD, "localhost");
		rssManagerClient = new RSSManagerClient(ssContext.getContextUrls().getBackEndUrl(), newUsrSessionCookie);
		rssManagerClient.detachUserFromDatabase(DEFAULT_ENVIRONMENT_NAME, "WSO2RSS1", databaseName, databaseUserName,
				SYSTEM_TYPE);
		DatabaseUserInfo[] databaseUserInfos = rssManagerClient.getUsersAttachedToDatabase(DEFAULT_ENVIRONMENT_NAME,
				"WSO2RSS1",
				databaseName, SYSTEM_TYPE);
		boolean userExist = false;
		if (databaseUserInfos != null) {
			for (DatabaseUserInfo databaseUserInfo : databaseUserInfos) {
				if (databaseUserName.equalsIgnoreCase(databaseUserInfo.getName())) {
					userExist = true;
				}
			}
			assertFalse(userExist);
		}
	}

	public void createPrivilegeTemplate()
			throws RemoteException, LoginAuthenticationExceptionException, XPathExpressionException {
		newUsrSessionCookie = authenticatorClient.login(RSS_USER_WITH_ALL_ATTACH_DATABASE_USER_PERMISSIONS,
				RSS_USER_PASSWORD, "localhost");
		rssManagerClient = new RSSManagerClient(ssContext.getContextUrls().getBackEndUrl(), newUsrSessionCookie);
		DatabasePrivilegeTemplateInfo template = new DatabasePrivilegeTemplateInfo();
		template.setName(privilegeTemplateName);
		MySQLPrivilegeSetInfo privileges = new MySQLPrivilegeSetInfo();
		privileges.setAlterPriv("Y");
		template.setPrivileges(privileges);
		rssManagerClient.createDatabasePrivilegesTemplate(DEFAULT_ENVIRONMENT_NAME, template);
		template = rssManagerClient.getDatabasePrivilegesTemplate(DEFAULT_ENVIRONMENT_NAME, privilegeTemplateName);
		assertNotNull(template);
		assertEquals(privilegeTemplateName, template.getName());
	}

	@AfterClass(alwaysRun = true)
	public void cleanUp() throws Exception {
		newUsrSessionCookie = authenticatorClient.login(RSS_USER_WITH_ALL_ATTACH_DATABASE_USER_PERMISSIONS,
				RSS_USER_PASSWORD, "localhost");
		rssManagerClient = new RSSManagerClient(ssContext.getContextUrls().getBackEndUrl(), newUsrSessionCookie);
		rssManagerClient.dropDatabaseUser(DEFAULT_ENVIRONMENT_NAME, "WSO2RSS1", databaseUserName, SYSTEM_TYPE);
		rssManagerClient.dropDatabase(DEFAULT_ENVIRONMENT_NAME, "WSO2RSS1", databaseName, SYSTEM_TYPE);
		rssManagerClient.dropDatabasePrivilegesTemplate(DEFAULT_ENVIRONMENT_NAME, privilegeTemplateName);
		userManagementClient.deleteUser(RSS_USER_WITH_ALL_ATTACH_DATABASE_USER_PERMISSIONS);
		userManagementClient.deleteUser(RSS_USER_WITH_ADD_PERMISSION);
		userManagementClient.deleteUser(RSS_USER_WITH_ADD_EDIT_PERMISSION);
		userManagementClient.deleteRole(RSS_ROLE_NAME_WITH_ADD_ATTACH_USER_PRIVILEGE);
		userManagementClient.deleteRole(RSS_ROLE_NAME_WITH_ADD_EDIT_ATTACH_USER_PRIVILEGES);
		userManagementClient.deleteRole(RSS_ROLE_NAME_WITH_ALL_ATTACH_USER_PRIVILEGES);
	}
}
