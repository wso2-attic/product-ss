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
package org.wso2.carbon.ss.integration.test.cassandra.cluster.stats;

import org.apache.axis2.AxisFault;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.cassandra.cluster.proxy.stub.data.xsd.ProxyClusterNetstat;
import org.wso2.carbon.cassandra.cluster.proxy.stub.data.xsd.ProxyClusterRingInformation;
import org.wso2.carbon.cassandra.cluster.proxy.stub.data.xsd.ProxyCompactionStats;
import org.wso2.carbon.cassandra.cluster.proxy.stub.data.xsd.ProxyKeyspaceInfo;
import org.wso2.carbon.cassandra.cluster.proxy.stub.data.xsd.ProxyNodeInformation;
import org.wso2.carbon.cassandra.cluster.proxy.stub.data.xsd.ProxyThreadPoolInfo;
import org.wso2.carbon.ss.SSIntegrationTest;
import org.wso2.ss.integration.common.clients.cassandra.cluster.stats.ClusterNodeStatsAdminClient;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class ClusterNodeStatsTestCase extends SSIntegrationTest {

	private ClusterNodeStatsAdminClient client;
	private static final String HOST = "localhost";
	private static final String HOST_ADDRESS = "127.0.0.1";

	@BeforeClass(alwaysRun = true)
	public void initializeTest() throws Exception {
		super.init();
		client = new ClusterNodeStatsAdminClient(ssContext.getContextUrls().getBackEndUrl(), sessionCookie);
	}

	@Test(groups = "wso2.ss", description = "Get cluster ring stats")
	public void testGetRingStats() throws AxisFault {
		ProxyClusterRingInformation[] proxyClusterRingInformations = client.getRing(HOST_ADDRESS, null);
		assertNotNull(proxyClusterRingInformations);
		assertEquals(1, proxyClusterRingInformations.length);
		assertEquals(HOST_ADDRESS, proxyClusterRingInformations[0].getAddress());
	}

	@Test(groups = "wso2.ss", description = "Get cluster node stats")
	public void testGetNodeInfoStats() throws AxisFault {
		ProxyNodeInformation nodeInfo = client.getNodeInfo(HOST_ADDRESS);
		assertNotNull(nodeInfo);
		assertEquals(true, nodeInfo.getGossipState());
	}

	@Test(groups = "wso2.ss", description = "Get cluster column family stats")
	public void testGetColumnFamiyStats() throws AxisFault {
		ProxyKeyspaceInfo[] proxyKeyspaceInfo = client.getCfstats(HOST_ADDRESS);
		assertNotNull(proxyKeyspaceInfo);
	}

	@Test(groups = "wso2.ss", description = "Get version")
	public void testGetVersion() throws AxisFault {
		assertNotNull(client.getVersion(HOST_ADDRESS));
	}

	@Test(groups = "wso2.ss", description = "Get cluster thread pool stats")
	public void testGetThreadPoolStats() throws AxisFault {
		ProxyThreadPoolInfo proxyThreadPoolInfo = client.getTpstats(HOST_ADDRESS);
		assertNotNull(proxyThreadPoolInfo);
	}

	@Test(groups = "wso2.ss", description = "Get cluster proxy compaction stats")
	public void testGetCompactionStats() throws AxisFault {
		ProxyCompactionStats proxyCompactionStats = client.getCompactionStats(HOST_ADDRESS);
		assertNotNull(proxyCompactionStats);
	}

	@Test(groups = "wso2.ss", description = "Get gossip info")
	public void testGetGossipInfo() throws AxisFault {
		assertNotNull(client.getGossipInfo(HOST_ADDRESS));
	}

	@Test(groups = "wso2.ss", description = "Get network stats info")
	public void testGetNetstat() throws AxisFault {
		ProxyClusterNetstat proxyClusterNetstat = client.getNetstat(HOST_ADDRESS, HOST_ADDRESS);
		assertNotNull(proxyClusterNetstat);
	}

	@Test(groups = "wso2.ss", description = "Get token removal status")
	public void testTOkenRemovalStatus() throws AxisFault {
		client.getTokenRemovalStatus(HOST_ADDRESS);
	}

	@Test(groups = "wso2.ss", description = "Get range key sample info")
	public void testRangeKeySample() throws AxisFault {
		client.getRangekeysample(HOST_ADDRESS);
	}

	@Test(groups = "wso2.ss", description = "Get keyspaces")
	public void testGetKeyspace() throws AxisFault {
		String[] keyspaces = client.getKeyspaces(HOST_ADDRESS);
		assertNotNull(keyspaces);
	}
}
