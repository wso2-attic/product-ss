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
import org.wso2.carbon.cassandra.cluster.proxy.stub.data.xsd.ProxyDescribeRingProperties;
import org.wso2.carbon.ss.SSIntegrationTest;
import org.wso2.ss.integration.common.clients.cassandra.cluster.stats.ClusterKeyspaceStatsAdminClient;

import static org.testng.Assert.assertNotNull;

public class ClusterKeyspaceStatsTestCase extends SSIntegrationTest {

	private ClusterKeyspaceStatsAdminClient client;
	private static final String SYSTEM_KEYSPACE = "system";
	private static final String HOST = "localhost";
	private static final String HOST_ADDRESS = "127.0.0.1";

	@BeforeClass(alwaysRun = true)
	public void initializeTest() throws Exception {
		super.init();
		client = new ClusterKeyspaceStatsAdminClient(ssContext.getContextUrls().getBackEndUrl(), sessionCookie);
	}

	@Test(groups = "wso2.ss", description = "Get column families for keyspace")
	public void testGetColumnFamiliesForKeyspace() throws AxisFault {
			String[] columnFamilies = client.getColumnFamiliesForKeyspace(HOST_ADDRESS, SYSTEM_KEYSPACE);
			assertNotNull(columnFamilies);
	}
}
