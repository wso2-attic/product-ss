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
package org.wso2.carbon.ss.integration.test.cassandra;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.cassandra.mgt.stub.ks.CassandraServerManagementExceptionException;
import org.wso2.carbon.cassandra.mgt.stub.ks.xsd.AuthorizedRolesInformation;
import org.wso2.carbon.cassandra.mgt.stub.ks.xsd.KeyspaceInformation;
import org.wso2.carbon.integration.common.admin.client.AuthenticatorClient;
import org.wso2.carbon.integration.common.admin.client.UserManagementClient;
import org.wso2.carbon.rssmanager.core.dto.xsd.DatabaseInfo;
import org.wso2.carbon.ss.SSIntegrationTest;
import org.wso2.carbon.ss.integration.test.rssmanager.RSSMgtTestCase;
import org.wso2.carbon.user.mgt.stub.UserAdminUserAdminException;
import org.wso2.ss.integration.common.clients.CassandraKeyspaceAdminClient;
import org.wso2.ss.integration.common.clients.RSSManagerClient;
import org.wso2.ss.integration.common.utils.CassandraUtils;

import javax.xml.xpath.XPathExpressionException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class CassandraRBACUserKeyspacePermissionTest extends SSIntegrationTest {

	private static final Log log = LogFactory.getLog(RSSMgtTestCase.class);

	private UserManagementClient userManagementClient;
	private final String CASSANDRA_USER_WITH_ADD_PERMISSION = "cassandratestuser1";
	private final String CASSANDRA_USER_WITH_ADD_EDIT_PERMISSION = "cassandratestuser2";
	private final String CASSANDRA_USER_WITH_ALL_PERMISSIONS = "cassandratestuser3";
	private final String CASSANDRA_USER_PASSWORD = "password";
	private final String CASSANDRA_ROLE_NAME_WITH_ADD_KEYSPACE_PRIVILEGE = "cassandratestrole1";
	private final String CASSANDRA_ROLE_NAME_WITH_ADD_EDIT_KEYSPACE_PRIVILEGES = "cassandratestrole2";
	private final String CASSANDRA_ROLE_NAME_WITH_ALL_PRIVILEGES = "cassandratestrole3";
	private String newUsrSessionCookie;
	private AuthenticatorClient authenticatorClient;
	private final String ENVIRONMENT_NAME = "DEFAULT";
	private final String DEFAULT_CLUSTER_NAME = "Test Cluster";
	private final String KEYSPACE_NAME = "testKeyspaceA";
	private final int REPLICATION_FACTOR = 1;
	private CassandraKeyspaceAdminClient client;

	@BeforeClass(alwaysRun = true)
	public void initializeTest() throws Exception {
		super.init();
		userManagementClient = new UserManagementClient(ssContext.getContextUrls().getBackEndUrl(), sessionCookie);
		authenticatorClient = new AuthenticatorClient(ssContext.getContextUrls().getBackEndUrl());
		client = new CassandraKeyspaceAdminClient(ssContext.getContextUrls().getBackEndUrl(), sessionCookie);
		initializeUsersAndRoles();
		setPermissionsForRoles();
	}

	private void setPermissionsForRoles() throws RemoteException, CassandraServerManagementExceptionException {
		AuthorizedRolesInformation[] authorizedRolesInformations = new AuthorizedRolesInformation[6];
		AuthorizedRolesInformation authorizeRoleInfo;
		authorizeRoleInfo = new AuthorizedRolesInformation();
		authorizeRoleInfo.setPermission("add");
		authorizeRoleInfo.setResource("/permission/admin/cassandra");
		authorizeRoleInfo.setAuthorizedRoles(new String[] { CASSANDRA_ROLE_NAME_WITH_ALL_PRIVILEGES,
				CASSANDRA_ROLE_NAME_WITH_ADD_EDIT_KEYSPACE_PRIVILEGES,
				CASSANDRA_ROLE_NAME_WITH_ADD_KEYSPACE_PRIVILEGE });
		authorizedRolesInformations[0] = authorizeRoleInfo;
		authorizeRoleInfo = new AuthorizedRolesInformation();
		authorizeRoleInfo.setPermission("edit");
		authorizeRoleInfo.setResource("/permission/admin/cassandra");
		authorizeRoleInfo.setAuthorizedRoles(new String[] { CASSANDRA_ROLE_NAME_WITH_ALL_PRIVILEGES,
				CASSANDRA_ROLE_NAME_WITH_ADD_EDIT_KEYSPACE_PRIVILEGES,
				CASSANDRA_ROLE_NAME_WITH_ADD_KEYSPACE_PRIVILEGE });
		authorizedRolesInformations[1] = authorizeRoleInfo;
		authorizeRoleInfo = new AuthorizedRolesInformation();
		authorizeRoleInfo.setPermission("delete");
		authorizeRoleInfo.setResource("/permission/admin/cassandra");
		authorizeRoleInfo.setAuthorizedRoles(new String[] { CASSANDRA_ROLE_NAME_WITH_ALL_PRIVILEGES,
				CASSANDRA_ROLE_NAME_WITH_ADD_EDIT_KEYSPACE_PRIVILEGES,
				CASSANDRA_ROLE_NAME_WITH_ADD_KEYSPACE_PRIVILEGE });
		authorizedRolesInformations[2] = authorizeRoleInfo;
		authorizeRoleInfo = new AuthorizedRolesInformation();
		authorizeRoleInfo.setPermission("browse");
		authorizeRoleInfo.setResource("/permission/admin/cassandra");
		authorizeRoleInfo.setAuthorizedRoles(new String[] { CASSANDRA_ROLE_NAME_WITH_ALL_PRIVILEGES,
				CASSANDRA_ROLE_NAME_WITH_ADD_EDIT_KEYSPACE_PRIVILEGES,
				CASSANDRA_ROLE_NAME_WITH_ADD_KEYSPACE_PRIVILEGE });
		authorizedRolesInformations[3] = authorizeRoleInfo;
		authorizeRoleInfo = new AuthorizedRolesInformation();
		authorizeRoleInfo.setPermission("consume");
		authorizeRoleInfo.setResource("/permission/admin/cassandra");
		authorizeRoleInfo.setAuthorizedRoles(new String[] { CASSANDRA_ROLE_NAME_WITH_ALL_PRIVILEGES,
				CASSANDRA_ROLE_NAME_WITH_ADD_EDIT_KEYSPACE_PRIVILEGES,
				CASSANDRA_ROLE_NAME_WITH_ADD_KEYSPACE_PRIVILEGE });
		authorizedRolesInformations[4] = authorizeRoleInfo;
		authorizeRoleInfo = new AuthorizedRolesInformation();
		authorizeRoleInfo.setPermission("authorize");
		authorizeRoleInfo.setResource("/permission/admin/cassandra");
		authorizeRoleInfo.setAuthorizedRoles(new String[] { CASSANDRA_ROLE_NAME_WITH_ALL_PRIVILEGES,
				CASSANDRA_ROLE_NAME_WITH_ADD_EDIT_KEYSPACE_PRIVILEGES,
				CASSANDRA_ROLE_NAME_WITH_ADD_KEYSPACE_PRIVILEGE });
		authorizedRolesInformations[5] = authorizeRoleInfo;
		client.authorizeRoles(authorizedRolesInformations);
	}

	private void initializeUsersAndRoles() throws RemoteException, UserAdminUserAdminException {
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
		permissions.add("/permission/applications/Cassandra/All Environments/DEFAULT/Keyspace/Add");
		userManagementClient.addRole(CASSANDRA_ROLE_NAME_WITH_ADD_KEYSPACE_PRIVILEGE, null, permissions.toArray(
				new String[permissions.size()]));
		userManagementClient.addUser(CASSANDRA_USER_WITH_ADD_PERMISSION, CASSANDRA_USER_PASSWORD,
				new String[] { CASSANDRA_ROLE_NAME_WITH_ADD_KEYSPACE_PRIVILEGE }, null);
		permissions.add("/permission/applications/Cassandra/All Environments/DEFAULT/Keyspace/Edit");
		userManagementClient.addRole(CASSANDRA_ROLE_NAME_WITH_ADD_EDIT_KEYSPACE_PRIVILEGES, null, permissions.toArray(new
				String[permissions.size()]));
		userManagementClient.addUser(CASSANDRA_USER_WITH_ADD_EDIT_PERMISSION, CASSANDRA_USER_PASSWORD, new String[]
				{ CASSANDRA_ROLE_NAME_WITH_ADD_EDIT_KEYSPACE_PRIVILEGES }, null);
		permissions.add("/permission/applications/Cassandra/All Environments/DEFAULT/Keyspace/Delete");
		userManagementClient.addRole(CASSANDRA_ROLE_NAME_WITH_ALL_PRIVILEGES, null, permissions.toArray(
				new String[permissions.size()]));
		userManagementClient.addUser(CASSANDRA_USER_WITH_ALL_PERMISSIONS, CASSANDRA_USER_PASSWORD,
				new String[] { CASSANDRA_ROLE_NAME_WITH_ALL_PRIVILEGES }, null);
	}

	@Test(groups = "wso2.ss", description = "Add keyspace by authorized user")
	public void addKeyspaceBuAuthorizedUser()
			throws Exception {
		newUsrSessionCookie = authenticatorClient.login(CASSANDRA_USER_WITH_ADD_PERMISSION, CASSANDRA_USER_PASSWORD,
				"localhost");
		client = new CassandraKeyspaceAdminClient(ssContext.getContextUrls().getBackEndUrl(), newUsrSessionCookie);
		boolean isKeyspaceContains = false;
		KeyspaceInformation keyspaceInformation = new KeyspaceInformation();
		keyspaceInformation.setName(KEYSPACE_NAME);
		keyspaceInformation.setReplicationFactor(REPLICATION_FACTOR);
		keyspaceInformation.setStrategyClass(CassandraUtils.SIMPLE_CLASS);
		keyspaceInformation.setEnvironmentName(ENVIRONMENT_NAME);
		client.addKeyspace(keyspaceInformation);
		for (KeyspaceInformation keyspace : client.listKeyspacesOfCurrentUSer(ENVIRONMENT_NAME)) {
			if (KEYSPACE_NAME.equals(keyspace.getName())) {
				isKeyspaceContains = true;
			}
		}
		assertTrue(isKeyspaceContains);
		keyspaceInformation = client.getKeyspaceOfCurrentUser(ENVIRONMENT_NAME, DEFAULT_CLUSTER_NAME, KEYSPACE_NAME);
		assertNotNull(keyspaceInformation);
		assertEquals(keyspaceInformation.getName(), KEYSPACE_NAME);
		assertEquals(keyspaceInformation.getReplicationFactor(), REPLICATION_FACTOR);
		assertEquals(keyspaceInformation.getStrategyClass(), CassandraUtils.SIMPLE_CLASS);
	}

	@Test(dependsOnMethods = "addKeyspaceBuAuthorizedUser", description = "update keyspace by unauthorized user",
			expectedExceptions = Exception.class)
	public void updateKeyspaceByUnAuthorizedUser()
			throws Exception {
		newUsrSessionCookie = authenticatorClient.login(CASSANDRA_USER_WITH_ADD_PERMISSION, CASSANDRA_USER_PASSWORD,
				"localhost");
		client = new CassandraKeyspaceAdminClient(ssContext.getContextUrls().getBackEndUrl(), newUsrSessionCookie);
		boolean isKeyspaceContains = false;
		KeyspaceInformation keyspaceInformation = new KeyspaceInformation();
		keyspaceInformation.setName(KEYSPACE_NAME);
		keyspaceInformation.setReplicationFactor(REPLICATION_FACTOR);
		keyspaceInformation.setStrategyClass(CassandraUtils.OLD_NETWORK_CLASS);
		keyspaceInformation.setEnvironmentName(ENVIRONMENT_NAME);
		client.updateKeyspace(keyspaceInformation);
	}

	@Test(dependsOnMethods = { "addKeyspaceBuAuthorizedUser",
			"updateKeyspaceByUnAuthorizedUser" }, description = "update keyspace by super tenant")
	public void updateKeyspaceByAuthorizedUser()
			throws Exception {
		newUsrSessionCookie = authenticatorClient.login(CASSANDRA_USER_WITH_ADD_EDIT_PERMISSION, CASSANDRA_USER_PASSWORD,
				"localhost");
		client = new CassandraKeyspaceAdminClient(ssContext.getContextUrls().getBackEndUrl(), newUsrSessionCookie);
		boolean isKeyspaceContains = false;
		KeyspaceInformation keyspaceInformation = new KeyspaceInformation();
		keyspaceInformation.setName(KEYSPACE_NAME);
		keyspaceInformation.setReplicationFactor(REPLICATION_FACTOR);
		keyspaceInformation.setStrategyClass(CassandraUtils.OLD_NETWORK_CLASS);
		keyspaceInformation.setEnvironmentName(ENVIRONMENT_NAME);
		client.updateKeyspace(keyspaceInformation);
		for (KeyspaceInformation keyspace : client.listKeyspacesOfCurrentUSer(ENVIRONMENT_NAME)) {
			if (KEYSPACE_NAME.equals(keyspace.getName())) {
				isKeyspaceContains = true;
			}
		}
		assertTrue(isKeyspaceContains);
		keyspaceInformation = client.getKeyspaceOfCurrentUser(ENVIRONMENT_NAME, DEFAULT_CLUSTER_NAME, KEYSPACE_NAME);
		assertNotNull(keyspaceInformation);
		assertEquals(keyspaceInformation.getName(), KEYSPACE_NAME);
		assertEquals(keyspaceInformation.getReplicationFactor(), REPLICATION_FACTOR);
		assertEquals(keyspaceInformation.getStrategyClass(), CassandraUtils.OLD_NETWORK_CLASS);
	}

	@Test(dependsOnMethods = { "updateKeyspaceByAuthorizedUser" }, description = "delete keyspace by unauthorized user",
			expectedExceptions = Exception.class)
	public void deleteKeyspaceByUnAuthorizedUser()
			throws Exception {
		newUsrSessionCookie = authenticatorClient.login(CASSANDRA_USER_WITH_ADD_EDIT_PERMISSION, CASSANDRA_USER_PASSWORD,
				"localhost");
		client = new CassandraKeyspaceAdminClient(ssContext.getContextUrls().getBackEndUrl(), newUsrSessionCookie);
		client.deleteKeyspace(ENVIRONMENT_NAME, DEFAULT_CLUSTER_NAME, KEYSPACE_NAME);
	}

	@Test(dependsOnMethods = { "updateKeyspaceByAuthorizedUser" }, description = "delete keyspace by unauthorized user")
	public void deleteKeyspaceByAuthorizedUser()
			throws Exception {
		newUsrSessionCookie = authenticatorClient.login(CASSANDRA_USER_WITH_ALL_PERMISSIONS, CASSANDRA_USER_PASSWORD,
				"localhost");
		client = new CassandraKeyspaceAdminClient(ssContext.getContextUrls().getBackEndUrl(), newUsrSessionCookie);
		assertTrue(client.deleteKeyspace(ENVIRONMENT_NAME, DEFAULT_CLUSTER_NAME, KEYSPACE_NAME));
	}

	@AfterClass(alwaysRun = true)
	public void cleanUp() throws Exception {
		userManagementClient.deleteUser(CASSANDRA_USER_WITH_ALL_PERMISSIONS);
		userManagementClient.deleteUser(CASSANDRA_USER_WITH_ADD_EDIT_PERMISSION);
		userManagementClient.deleteUser(CASSANDRA_USER_WITH_ADD_PERMISSION);
		userManagementClient.deleteRole(CASSANDRA_ROLE_NAME_WITH_ALL_PRIVILEGES);
		userManagementClient.deleteRole(CASSANDRA_ROLE_NAME_WITH_ADD_EDIT_KEYSPACE_PRIVILEGES);
		userManagementClient.deleteRole(CASSANDRA_ROLE_NAME_WITH_ADD_KEYSPACE_PRIVILEGE);
	}
}
