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
import org.wso2.ss.integration.common.clients.cassandra.cluster.operation.ClusterColumnFamilyOperationsAdminClient;

public class ClusterColumnFamilyOperationsTestCase extends SSIntegrationTest {

	private ClusterColumnFamilyOperationsAdminClient client;

	private static final String SYSTEM_KEYSPACE = "system_auth";
	private static final String SYSTEM_COLUMN_FAMILY = "users";
	private static final String HOST = "localhost";
	private static final String HOST_ADDRESS = "127.0.0.1";
	private static final String SNAPSHOT_TAG = "testSnapshot";

	@BeforeClass(alwaysRun = true)
	public void initializeTest() throws Exception {
		super.init();
		client = new ClusterColumnFamilyOperationsAdminClient(ssContext.getContextUrls().getBackEndUrl(), sessionCookie);
	}

	@Test(groups = "wso2.ss", description = "Flush column families")
	public void testFlushColumnFamilies() throws AxisFault {
		client.flushColumnFamilies(HOST_ADDRESS, SYSTEM_KEYSPACE, new String[] {SYSTEM_COLUMN_FAMILY});
	}


	@Test(groups = "wso2.ss", description = "Repair column families")
	public void testrRepairColumnFamilies() throws AxisFault {
			client.repairColumnFamilies(HOST_ADDRESS, SYSTEM_KEYSPACE, new String[] {SYSTEM_COLUMN_FAMILY});
	}

	@Test(groups = "wso2.ss", description = "Cleanup column families")
	public void testCleanUpColumnFamilies() throws AxisFault {
			client.cleanUpColumnFamilies(HOST_ADDRESS, SYSTEM_KEYSPACE, new String[] {SYSTEM_COLUMN_FAMILY});
	}

	@Test(groups = "wso2.ss", description = "Compact column families")
	public void testCompactColumnFamilies() throws AxisFault {
			client.compactColumnFamilies(HOST_ADDRESS, SYSTEM_KEYSPACE, new String[] { SYSTEM_COLUMN_FAMILY });
	}

	@Test(groups = "wso2.ss", description = "Scrub column family")
	public void testScrubColumnFamilies() throws AxisFault {
			client.scrubColumnFamilies(HOST_ADDRESS, SYSTEM_KEYSPACE, new String[] {SYSTEM_COLUMN_FAMILY});
	}

	@Test(groups = "wso2.ss", description = "UpgradeSSTables column family")
	public void testUpgradeSSTablesColumnFamilies() throws AxisFault {
			client.upgradeSSTablesColumnFamilies(HOST_ADDRESS, SYSTEM_KEYSPACE, new String[] {SYSTEM_COLUMN_FAMILY});
	}

	@Test(groups = "wso2.ss", description = "Rebuild column families")
	public void testRebuildCF() throws AxisFault {
			client.rebuildCF(HOST_ADDRESS, SYSTEM_KEYSPACE, SYSTEM_COLUMN_FAMILY);
	}

	@Test(groups = "wso2.ss", description = "Refresh column families")
	public void testRefreshCF() throws AxisFault {
			client.refreshCF(HOST_ADDRESS, SYSTEM_KEYSPACE, SYSTEM_COLUMN_FAMILY);
	}

	@Test(groups = "wso2.ss", description = "set compaction thresholds")
	public void testSetCompactionThresholds()
			throws AxisFault {
			client.setCompactionThresholds(HOST_ADDRESS, SYSTEM_KEYSPACE, SYSTEM_COLUMN_FAMILY, 4, 32);
	}

	@Test(groups = "wso2.ss", description = "Take column family snapshot")
	public void takeCFSnapshot() throws AxisFault {
			client.takeCFSnapshot(HOST_ADDRESS, SNAPSHOT_TAG, SYSTEM_KEYSPACE, SYSTEM_COLUMN_FAMILY);
	}

}
