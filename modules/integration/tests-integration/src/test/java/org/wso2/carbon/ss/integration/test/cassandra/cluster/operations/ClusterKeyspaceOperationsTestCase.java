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
package org.wso2.carbon.ss.integration.test.cassandra.cluster.operations;

import org.apache.axis2.AxisFault;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.ss.SSIntegrationTest;
import org.wso2.ss.integration.common.clients.cassandra.cluster.operation.ClusterKeyspaceOperationsAdminClient;

public class ClusterKeyspaceOperationsTestCase extends SSIntegrationTest {

	private ClusterKeyspaceOperationsAdminClient client;

	private static final String SYSTEM_KEYSPACE = "system_auth";
	private static final String HOST = "localhost";
	private static final String HOST_ADDRESS = "127.0.0.1";
	private static final String SNAPSHOT_TAG = "keyspaceSnapshot";

	@BeforeClass(alwaysRun = true)
	public void initializeTest() throws Exception {
		super.init();
		client = new ClusterKeyspaceOperationsAdminClient(ssContext.getContextUrls().getBackEndUrl(), sessionCookie);
	}

	@Test(groups = "wso2.ss", description = "Flush keyspace")
	public void testFlushKeyspace() throws AxisFault {
		client.flushKeyspace(HOST_ADDRESS, SYSTEM_KEYSPACE);
	}

	@Test(groups = "wso2.ss", description = "Compact keyspace")
	public void testCompactKeyspace() throws AxisFault {
		client.compactKeyspace(HOST_ADDRESS, SYSTEM_KEYSPACE);
	}

	@Test(groups = "wso2.ss", description = "Cleanup keyspace")
	public void testCleanUpKeyspace() throws AxisFault {
		client.cleanUpKeyspace(HOST_ADDRESS, SYSTEM_KEYSPACE);
	}

	@Test(groups = "wso2.ss", description = "Repair keyspace")
	public void testRepairKeyspace() throws AxisFault {
		client.repairKeyspace(HOST_ADDRESS, SYSTEM_KEYSPACE);
	}

	@Test(groups = "wso2.ss", description = "Take snapshot of keyspace")
	public void testTakeSnapshotKeyspace() throws AxisFault {
		client.takeSnapshotKeyspace(HOST_ADDRESS, SNAPSHOT_TAG, SYSTEM_KEYSPACE);
	}

	@Test(groups = "wso2.ss", description = "Take snapshot of keyspace", dependsOnGroups = {"testTakeSnapshotKeyspace"})
	public void testClearSnapshotKeyspace() throws AxisFault {
		client.clearSnapshotKeyspace(HOST_ADDRESS, SNAPSHOT_TAG, SYSTEM_KEYSPACE);
	}

}
