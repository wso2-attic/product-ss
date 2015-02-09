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
package org.wso2.carbon.ss.integration.test.cassandra.cluster.explorer;

import org.apache.axis2.AxisFault;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.ss.SSIntegrationTest;
import org.wso2.ss.integration.common.clients.cassandra.cluster.explorer.CassandraExplorerAdminClient;

import static org.testng.Assert.assertTrue;

public class CassandraClusterExplorerTestCase extends SSIntegrationTest {

	private CassandraExplorerAdminClient client;
	private static String CLUSTER_URL = "localhost:9160";
	private static String USERNAME = "admin";
	private static String PASSWORD = "admin";
	private static String CLUSTER_NAME = "Test Cluster";
	private static final String SYSTEM_KEYSPACE = "system";
	private static final String SYSTEM_COLUMN_FAMILY = "schema_keyspaces";

	@BeforeClass(alwaysRun = true)
	public void initializeTest() throws Exception {
		super.init();
		client = new CassandraExplorerAdminClient(ssContext.getContextUrls().getBackEndUrl(), sessionCookie);
		connectToCluster();
	}

	@Test(groups = "wso2.ss", description = "Test get keyspaces")
	public void testGetKeyspaces() throws AxisFault {
		boolean isExist = false;
		for(String s : client.getKeyspaces()) {
			if(SYSTEM_KEYSPACE.equalsIgnoreCase(s)) {
				isExist = true;
			}
		}
		assertTrue(isExist);
	}

	@Test(groups = "wso2.ss", description = "Test get ccolumn families of keyspace")
	public void testGetColumnFamiliesOfKeyspace() throws AxisFault {
		boolean isExist = false;
		for(String s : client.getColumnFamilies(SYSTEM_KEYSPACE)) {
			if(SYSTEM_COLUMN_FAMILY.equalsIgnoreCase(s)) {
				isExist = true;
			}
		}
		assertTrue(isExist);
	}

	private void connectToCluster() throws AxisFault {
		client.connectToCassandraCluster(CLUSTER_NAME, CLUSTER_URL, USERNAME, PASSWORD);
	}

}
