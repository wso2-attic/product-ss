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
import org.wso2.carbon.cassandra.mgt.stub.ks.CassandraServerManagementExceptionException;
import org.wso2.carbon.cassandra.mgt.stub.ks.xsd.AuthorizedRolesInformation;
import org.wso2.carbon.cassandra.mgt.stub.ks.xsd.ColumnFamilyInformation;
import org.wso2.carbon.cassandra.mgt.stub.ks.xsd.KeyspaceInformation;
import org.wso2.carbon.integration.common.admin.client.AuthenticatorClient;
import org.wso2.carbon.integration.common.admin.client.UserManagementClient;
import org.wso2.carbon.ss.SSIntegrationTest;
import org.wso2.carbon.ss.integration.test.rssmanager.RSSMgtTestCase;
import org.wso2.carbon.user.mgt.stub.UserAdminUserAdminException;
import org.wso2.ss.integration.common.clients.CassandraKeyspaceAdminClient;
import org.wso2.ss.integration.common.utils.CassandraClientHelper;
import org.wso2.ss.integration.common.utils.CassandraUtils;

import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class CassandraRBACUserColumnFamilyPermissionTest extends SSIntegrationTest {

	private static final Log log = LogFactory.getLog(RSSMgtTestCase.class);

	private UserManagementClient userManagementClient;
	private final String CASSANDRA_USER_WITH_ADD_PERMISSION = "cassandratestuser1";
	private final String CASSANDRA_USER_WITH_ADD_EDIT_PERMISSION = "cassandratestuser2";
	private final String CASSANDRA_USER_WITH_ALL_PERMISSIONS = "cassandratestuser3";
	private final String CASSANDRA_USER_PASSWORD = "password";
	private final String CASSANDRA_ROLE_NAME_WITH_ADD_COLUMN_FAMILY_PRIVILEGE = "cassandratestrole1";
	private final String CASSANDRA_ROLE_NAME_WITH_ADD_EDIT_COLUMN_FAMILY_PRIVILEGES = "cassandratestrole2";
	private final String CASSANDRA_ROLE_NAME_WITH_ALL_PRIVILEGES = "cassandratestrole3";
	private String newUsrSessionCookie;
	private AuthenticatorClient authenticatorClient;
	private final String ENVIRONMENT_NAME = "DEFAULT";
	private final String DEFAULT_CLUSTER_NAME = "Test Cluster";
	private final String KEYSPACE_NAME = "testKeyspaceA";
	private final int REPLICATION_FACTOR = 1;
	private final String COLUMN_FAMILY_NAME = "TestColumnFamilyA";
	private final double KEY_CACHE_SIZE = 0.5;
	private final double ROW_CACHE_SIZE = 0.5;
	public static final String RESOURCE_PATH_PREFIX = File.separator + "permission" + File.separator +
	                                                  "admin" + File.separator + "cassandra";
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
		permissions.add("/permission/applications/Cassandra/All Environments/DEFAULT/Keyspace/Edit");
		permissions.add("/permission/applications/Cassandra/All Environments/DEFAULT/Keyspace/Delete");
		permissions.add("/permission/applications/Cassandra/All Environments/DEFAULT/Column Family/Add");
		userManagementClient.addRole(CASSANDRA_ROLE_NAME_WITH_ADD_COLUMN_FAMILY_PRIVILEGE, null, permissions.toArray(
				new String[permissions.size()]));
		userManagementClient.addUser(CASSANDRA_USER_WITH_ADD_PERMISSION, CASSANDRA_USER_PASSWORD,
				new String[] { CASSANDRA_ROLE_NAME_WITH_ADD_COLUMN_FAMILY_PRIVILEGE }, null);
		permissions.add("/permission/applications/Cassandra/All Environments/DEFAULT/Column Family/Edit");
		userManagementClient.addRole(CASSANDRA_ROLE_NAME_WITH_ADD_EDIT_COLUMN_FAMILY_PRIVILEGES, null, permissions.toArray(
				new
						String[permissions.size()]));
		userManagementClient.addUser(CASSANDRA_USER_WITH_ADD_EDIT_PERMISSION, CASSANDRA_USER_PASSWORD, new String[]
				{ CASSANDRA_ROLE_NAME_WITH_ADD_EDIT_COLUMN_FAMILY_PRIVILEGES }, null);
		permissions.add("/permission/applications/Cassandra/All Environments/DEFAULT/Column Family/Delete");
		userManagementClient.addRole(CASSANDRA_ROLE_NAME_WITH_ALL_PRIVILEGES, null, permissions.toArray(
				new String[permissions.size()]));
		userManagementClient.addUser(CASSANDRA_USER_WITH_ALL_PERMISSIONS, CASSANDRA_USER_PASSWORD,
				new String[] { CASSANDRA_ROLE_NAME_WITH_ALL_PRIVILEGES }, null);
	}

	private void setPermissionsForRoles() throws RemoteException, CassandraServerManagementExceptionException {
		AuthorizedRolesInformation[] authorizedRolesInformations = new AuthorizedRolesInformation[6];
		AuthorizedRolesInformation authorizeRoleInfo;
		authorizeRoleInfo = new AuthorizedRolesInformation();
		authorizeRoleInfo.setPermission("add");
		authorizeRoleInfo.setResource("/permission/admin/cassandra");
		authorizeRoleInfo.setAuthorizedRoles(new String[] { CASSANDRA_ROLE_NAME_WITH_ALL_PRIVILEGES,
				CASSANDRA_ROLE_NAME_WITH_ADD_COLUMN_FAMILY_PRIVILEGE,
				CASSANDRA_ROLE_NAME_WITH_ADD_EDIT_COLUMN_FAMILY_PRIVILEGES });
		authorizedRolesInformations[0] = authorizeRoleInfo;
		authorizeRoleInfo = new AuthorizedRolesInformation();
		authorizeRoleInfo.setPermission("edit");
		authorizeRoleInfo.setResource("/permission/admin/cassandra");
		authorizeRoleInfo.setAuthorizedRoles(new String[] { CASSANDRA_ROLE_NAME_WITH_ALL_PRIVILEGES,
				CASSANDRA_ROLE_NAME_WITH_ADD_COLUMN_FAMILY_PRIVILEGE,
				CASSANDRA_ROLE_NAME_WITH_ADD_EDIT_COLUMN_FAMILY_PRIVILEGES });
		authorizedRolesInformations[1] = authorizeRoleInfo;
		authorizeRoleInfo = new AuthorizedRolesInformation();
		authorizeRoleInfo.setPermission("delete");
		authorizeRoleInfo.setResource("/permission/admin/cassandra");
		authorizeRoleInfo.setAuthorizedRoles(new String[] { CASSANDRA_ROLE_NAME_WITH_ALL_PRIVILEGES,
				CASSANDRA_ROLE_NAME_WITH_ADD_COLUMN_FAMILY_PRIVILEGE,
				CASSANDRA_ROLE_NAME_WITH_ADD_EDIT_COLUMN_FAMILY_PRIVILEGES });
		authorizedRolesInformations[2] = authorizeRoleInfo;
		authorizeRoleInfo = new AuthorizedRolesInformation();
		authorizeRoleInfo.setPermission("browse");
		authorizeRoleInfo.setResource("/permission/admin/cassandra");
		authorizeRoleInfo.setAuthorizedRoles(new String[] { CASSANDRA_ROLE_NAME_WITH_ALL_PRIVILEGES,
				CASSANDRA_ROLE_NAME_WITH_ADD_COLUMN_FAMILY_PRIVILEGE,
				CASSANDRA_ROLE_NAME_WITH_ADD_EDIT_COLUMN_FAMILY_PRIVILEGES });
		authorizedRolesInformations[3] = authorizeRoleInfo;
		authorizeRoleInfo = new AuthorizedRolesInformation();
		authorizeRoleInfo.setPermission("consume");
		authorizeRoleInfo.setResource("/permission/admin/cassandra");
		authorizeRoleInfo.setAuthorizedRoles(new String[] { CASSANDRA_ROLE_NAME_WITH_ALL_PRIVILEGES,
				CASSANDRA_ROLE_NAME_WITH_ADD_COLUMN_FAMILY_PRIVILEGE,
				CASSANDRA_ROLE_NAME_WITH_ADD_EDIT_COLUMN_FAMILY_PRIVILEGES });
		authorizedRolesInformations[4] = authorizeRoleInfo;
		authorizeRoleInfo = new AuthorizedRolesInformation();
		authorizeRoleInfo.setPermission("authorize");
		authorizeRoleInfo.setResource("/permission/admin/cassandra");
		authorizeRoleInfo.setAuthorizedRoles(new String[] { CASSANDRA_ROLE_NAME_WITH_ALL_PRIVILEGES,
				CASSANDRA_ROLE_NAME_WITH_ADD_COLUMN_FAMILY_PRIVILEGE,
				CASSANDRA_ROLE_NAME_WITH_ADD_EDIT_COLUMN_FAMILY_PRIVILEGES });
		authorizedRolesInformations[5] = authorizeRoleInfo;
		client.authorizeRoles(authorizedRolesInformations);
	}

	@Test(groups = "wso2.ss", description = "Add keyspace")
	public void addKeyspace()
			throws Exception {
		client = new CassandraKeyspaceAdminClient(ssContext.getContextUrls().getBackEndUrl(), sessionCookie);
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

	@Test(dependsOnMethods = { "addKeyspace" }, description = "Add column family by authorize user")
	public void addColumnFamilyByAuthorizeduser()
			throws Exception {
		newUsrSessionCookie = authenticatorClient.login(CASSANDRA_USER_WITH_ADD_PERMISSION, CASSANDRA_USER_PASSWORD,
				"localhost");
		client = new CassandraKeyspaceAdminClient(ssContext.getContextUrls().getBackEndUrl(), newUsrSessionCookie);
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
		client.addColumnFamily(ENVIRONMENT_NAME, DEFAULT_CLUSTER_NAME, columnFamilyInformation);
		for (String columnFamily : client.listColumnFamiliesOfCurrentUser(ENVIRONMENT_NAME, null, KEYSPACE_NAME)) {
			if (COLUMN_FAMILY_NAME.equals(columnFamily)) {
				isCFContains = true;
			}
		}
		assertTrue(isCFContains);
		columnFamilyInformation = client.getColumnFamilyInformationOfCurrentUser(ENVIRONMENT_NAME, DEFAULT_CLUSTER_NAME,
				KEYSPACE_NAME, COLUMN_FAMILY_NAME);
		assertEquals(columnFamilyInformation.getName(), COLUMN_FAMILY_NAME);
		assertEquals(columnFamilyInformation.getKeyspace(), KEYSPACE_NAME);
		assertEquals(columnFamilyInformation.getGcGraceSeconds(), CassandraUtils.DEFAULT_GCGRACE);
		assertEquals(columnFamilyInformation.getMaxCompactionThreshold(), CassandraUtils.DEFAULT_MAX_COMPACTION_THRESHOLD);
		assertEquals(columnFamilyInformation.getMinCompactionThreshold(), CassandraUtils.DEFAULT_MIN_COMPACTION_THRESHOLD);
		assertEquals(columnFamilyInformation.getType(), CassandraUtils.COLUMN_TYPE_STANDARD);
	}

	@Test(dependsOnMethods = {
			"addKeyspace" }, description = "Update column family by unauthorize user", expectedExceptions = Exception.class)
	public void updateColumnFamilyByUnAuthorizeUser()
			throws Exception {
		newUsrSessionCookie = authenticatorClient.login(CASSANDRA_USER_WITH_ADD_PERMISSION, CASSANDRA_USER_PASSWORD,
				"localhost");
		client = new CassandraKeyspaceAdminClient(ssContext.getContextUrls().getBackEndUrl(), newUsrSessionCookie);
		boolean isCFContains = false;
		KeyspaceInformation keyspaceInformation = client.getKeyspaceOfCurrentUser(ENVIRONMENT_NAME, DEFAULT_CLUSTER_NAME,
				KEYSPACE_NAME);
		ColumnFamilyInformation columnFamilyInformation = CassandraClientHelper.getColumnFamilyInformationOfCurrentUser(
				keyspaceInformation, COLUMN_FAMILY_NAME);
		columnFamilyInformation.setDefaultValidationClass(CassandraUtils.ASCIITYPE);
		client.updateColumnFamily(ENVIRONMENT_NAME, DEFAULT_CLUSTER_NAME, columnFamilyInformation);
	}

	@Test(dependsOnMethods = { "addKeyspace", "updateColumnFamilyByUnAuthorizeUser" }, description = "Update column family "
	                                                                                                 + "by authorize user")
	public void updateColumnFamilyByAuthorizeUser()
			throws Exception {
		newUsrSessionCookie = authenticatorClient.login(CASSANDRA_USER_WITH_ADD_EDIT_PERMISSION, CASSANDRA_USER_PASSWORD,
				"localhost");
		client = new CassandraKeyspaceAdminClient(ssContext.getContextUrls().getBackEndUrl(), newUsrSessionCookie);
		boolean isCFContains = false;
		KeyspaceInformation keyspaceInformation = client.getKeyspaceOfCurrentUser(ENVIRONMENT_NAME, DEFAULT_CLUSTER_NAME,
				KEYSPACE_NAME);
		ColumnFamilyInformation columnFamilyInformation = CassandraClientHelper.getColumnFamilyInformationOfCurrentUser(
				keyspaceInformation, COLUMN_FAMILY_NAME);
		columnFamilyInformation.setDefaultValidationClass(CassandraUtils.ASCIITYPE);
		client.updateColumnFamily(ENVIRONMENT_NAME, DEFAULT_CLUSTER_NAME, columnFamilyInformation);
		for (String columnFamily : client.listColumnFamiliesOfCurrentUser(ENVIRONMENT_NAME, DEFAULT_CLUSTER_NAME,
				KEYSPACE_NAME)) {
			if (COLUMN_FAMILY_NAME.equals(columnFamily)) {
				isCFContains = true;
			}
		}
		assertTrue(isCFContains);
		columnFamilyInformation = client.getColumnFamilyInformationOfCurrentUser(ENVIRONMENT_NAME, DEFAULT_CLUSTER_NAME,
				KEYSPACE_NAME, COLUMN_FAMILY_NAME);
		assertEquals(columnFamilyInformation.getName(), COLUMN_FAMILY_NAME);
		assertEquals(columnFamilyInformation.getKeyspace(), KEYSPACE_NAME);
		assertEquals(columnFamilyInformation.getGcGraceSeconds(), CassandraUtils.DEFAULT_GCGRACE);
		assertEquals(columnFamilyInformation.getMaxCompactionThreshold(), CassandraUtils.DEFAULT_MAX_COMPACTION_THRESHOLD);
		assertEquals(columnFamilyInformation.getMinCompactionThreshold(), CassandraUtils.DEFAULT_MIN_COMPACTION_THRESHOLD);
		assertEquals(columnFamilyInformation.getType(), CassandraUtils.COLUMN_TYPE_STANDARD);
	}

	@Test(dependsOnMethods = { "addKeyspace",
			"updateColumnFamilyByAuthorizeUser" }, description = "delete column family by unauthorize user",
			expectedExceptions = Exception.class)
	public void deleteColumnFamilyByUnAuthorizeUser()
			throws Exception {
		newUsrSessionCookie = authenticatorClient.login(CASSANDRA_USER_WITH_ADD_EDIT_PERMISSION, CASSANDRA_USER_PASSWORD,
				"localhost");
		client = new CassandraKeyspaceAdminClient(ssContext.getContextUrls().getBackEndUrl(), newUsrSessionCookie);
		client.deleteColumnFamily(ENVIRONMENT_NAME, DEFAULT_CLUSTER_NAME, KEYSPACE_NAME, COLUMN_FAMILY_NAME);
	}

	@Test(dependsOnMethods = { "addKeyspace",
			"deleteColumnFamilyByUnAuthorizeUser" }, description = "delete column family by authorize user")
	public void deleteColumnFamilyByAuthorizeUser()
			throws Exception {
		newUsrSessionCookie = authenticatorClient.login(CASSANDRA_USER_WITH_ALL_PERMISSIONS, CASSANDRA_USER_PASSWORD,
				"localhost");
		client = new CassandraKeyspaceAdminClient(ssContext.getContextUrls().getBackEndUrl(), newUsrSessionCookie);
		assertTrue(client.deleteColumnFamily(ENVIRONMENT_NAME, DEFAULT_CLUSTER_NAME, KEYSPACE_NAME, COLUMN_FAMILY_NAME));
	}

	@AfterClass(alwaysRun = true)
	public void cleanUp() throws Exception {
		client = new CassandraKeyspaceAdminClient(ssContext.getContextUrls().getBackEndUrl(), newUsrSessionCookie);
		client.deleteKeyspace(ENVIRONMENT_NAME, DEFAULT_CLUSTER_NAME, KEYSPACE_NAME);
		userManagementClient.deleteUser(CASSANDRA_USER_WITH_ALL_PERMISSIONS);
		userManagementClient.deleteUser(CASSANDRA_USER_WITH_ADD_EDIT_PERMISSION);
		userManagementClient.deleteUser(CASSANDRA_USER_WITH_ADD_PERMISSION);
		userManagementClient.deleteRole(CASSANDRA_ROLE_NAME_WITH_ALL_PRIVILEGES);
		userManagementClient.deleteRole(CASSANDRA_ROLE_NAME_WITH_ADD_EDIT_COLUMN_FAMILY_PRIVILEGES);
		userManagementClient.deleteRole(CASSANDRA_ROLE_NAME_WITH_ADD_COLUMN_FAMILY_PRIVILEGE);
	}
}
