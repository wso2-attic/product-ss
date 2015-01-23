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
import org.wso2.carbon.cassandra.cluster.proxy.stub.data.xsd.ProxyKeyspaceInitialInfo;
import org.wso2.carbon.cassandra.cluster.proxy.stub.data.xsd.ProxyNodeInitialInfo;
import org.wso2.carbon.ss.SSIntegrationTest;
import org.wso2.ss.integration.common.clients.cassandra.cluster.operation.ClusterNodeOperationsAdminClient;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class ClusterNodeOperationsTestCase extends SSIntegrationTest {

	private ClusterNodeOperationsAdminClient client;
	private static final String HOST = "localhost";
	private static final String HOST_ADDRESS = "127.0.0.1";
	private static final String SNAPSHOT_TAG = "nodeSnapshot";

	@BeforeClass(alwaysRun = true)
	public void initializeTest() throws Exception {
		super.init();
		client = new ClusterNodeOperationsAdminClient(ssContext.getContextUrls().getBackEndUrl(), sessionCookie);
	}

	@Test(groups = "wso2.ss", description = "Perform garbage collector")
	public void testPerformGC() throws AxisFault {
		client.performGC(HOST_ADDRESS);
	}

	@Test(groups = "wso2.ss", description = "Test take and clear node snapshot")
	public void testTakeAndClearNodeSnapShot() throws AxisFault {
		client.takeNodeSnapShot(HOST_ADDRESS, SNAPSHOT_TAG);
		boolean isSnapshotExist = false;
		for(String s : client.getSnapshotTags(HOST_ADDRESS)) {
			if(SNAPSHOT_TAG.equalsIgnoreCase(s)) {
				isSnapshotExist = true;
			}
		}
		assertTrue(isSnapshotExist);
		client.clearNodeSnapShot(HOST_ADDRESS, SNAPSHOT_TAG);
		isSnapshotExist = false;
		for(String s : client.getSnapshotTags(HOST_ADDRESS)) {
			if(SNAPSHOT_TAG.equalsIgnoreCase(s)) {
				isSnapshotExist = true;
			}
		}
		assertFalse(isSnapshotExist);
	}

	@Test(groups = "wso2.ss", description = "Test start and stop RPC")
	public void testStartAndStopRPCServer() throws AxisFault, InterruptedException {
		client.stopRPCServer(HOST_ADDRESS);
		Thread.sleep(5000);
		assertFalse(client.getRPCServerStatus(HOST_ADDRESS));
		client.startRPCServer(HOST_ADDRESS);
		Thread.sleep(5000);
		assertTrue(client.getRPCServerStatus(HOST_ADDRESS));

	}

	@Test(groups = "wso2.ss", description = "Test start and stop gossip")
	public void testStartAndStopGossipServer() throws AxisFault, InterruptedException {
		client.startGossipServer(HOST_ADDRESS);
		Thread.sleep(5000);
		assertFalse(client.getGossipServerStatus(HOST_ADDRESS));
		client.startGossipServer(HOST_ADDRESS);
		Thread.sleep(5000);
		assertTrue(client.getGossipServerStatus(HOST_ADDRESS));
	}

	@Test(groups = "wso2.ss", description = "Test incremental backup")
	public void testSetIncrementalBackup() throws AxisFault, InterruptedException {
		client.setIncrementalBackUpStatus(HOST_ADDRESS, true);
		Thread.sleep(5000);
		assertFalse(client.getIncrementalBackUpStatus(HOST_ADDRESS));
		client.setIncrementalBackUpStatus(HOST_ADDRESS, false);
		Thread.sleep(5000);
		assertTrue(client.getGossipServerStatus(HOST_ADDRESS));
	}

	@Test(groups = "wso2.ss", description = "Invalidate key cache")
	public void testInvalidateKeyCache() throws AxisFault {
		client.invalidateKeyCache(HOST_ADDRESS);
	}

	@Test(groups = "wso2.ss", description = "Invalidate key cache")
	public void testInvalidateRowCache() throws AxisFault {
		client.invalidateRowCache(HOST_ADDRESS);
	}

	@Test(groups = "wso2.ss", description = "Set Row cache capacity")
	public void testSetRowCacheCapacity() throws AxisFault {
		client.setRowCacheCapacity(HOST_ADDRESS, 4);
	}

	@Test(groups = "wso2.ss", description = "Set Key cache capacity")
	public void testSetKeyCacheCapacity() throws AxisFault {
		client.setKeyCacheCapacity(HOST_ADDRESS, 8);
	}

	@Test(groups = "wso2.ss", description = "Set compaction throughput")
	public void testSetCompactionThroughput() throws AxisFault {
		client.setCompactionThroughput(HOST_ADDRESS, 5);
	}

	@Test(groups = "wso2.ss", description = "Set stream throughput")
	public void testSetStreamThroughput() throws AxisFault {
		client.setStreamThroughput(HOST_ADDRESS, 10);
	}

	@Test(groups = "wso2.ss", description = "Test get keyspace initial info")
	public void testGetNodeInitialInfo() throws AxisFault {
		ProxyNodeInitialInfo proxyNodeInitialInfo = client.getNodeInitialInfo(HOST_ADDRESS);
		assertNotNull(proxyNodeInitialInfo);
	}

	@Test(groups = "wso2.ss", description = "Test get keyspace initial info")
	public void testGetKeyspaceInitialInfo() throws AxisFault {
		ProxyKeyspaceInitialInfo keyspaceInitialInfo = client.getKeyspaceInitialInfo(HOST_ADDRESS);
		assertNotNull(keyspaceInitialInfo);
	}
}
