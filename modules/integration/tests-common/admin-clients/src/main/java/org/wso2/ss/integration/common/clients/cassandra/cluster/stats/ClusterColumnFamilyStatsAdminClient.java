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
package org.wso2.ss.integration.common.clients.cassandra.cluster.stats;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.cassandra.cluster.proxy.stub.data.xsd.ProxyColumnFamilyHistograms;
import org.wso2.carbon.cassandra.cluster.proxy.stub.stats.ClusterStatsProxyAdminStub;
import org.wso2.carbon.integration.common.admin.client.utils.AuthenticateStubUtil;

public class ClusterColumnFamilyStatsAdminClient {

	private static final Log log = LogFactory.getLog(ClusterColumnFamilyStatsAdminClient.class);

	private ClusterStatsProxyAdminStub clusterStatsProxyAdminStub;

	private String serviceName = "ClusterStatsProxyAdmin";

	public ClusterColumnFamilyStatsAdminClient(String backEndUrl, String sessionCookie) throws AxisFault {
		String endPoint = backEndUrl + serviceName;
		clusterStatsProxyAdminStub = new ClusterStatsProxyAdminStub(endPoint);
		AuthenticateStubUtil.authenticateStub(sessionCookie, clusterStatsProxyAdminStub);
	}

	public int[] getCompactionThresholds(String host, String keyspace, String columnFamily)
			throws AxisFault {
		try {
			return clusterStatsProxyAdminStub.getCompactionThresholds(host, keyspace, columnFamily);
		} catch (Exception e) {
			throw new AxisFault("Error while getting cluster ring information", e);
		}
	}

	public ProxyColumnFamilyHistograms[] getColumnFamilyHistograms(String host, String keyspace, String columnFamily)
			throws AxisFault {
		try {
			return clusterStatsProxyAdminStub.getColumnFamilyHistograms(host, keyspace, columnFamily);
		} catch (Exception e) {
			throw new AxisFault("Error while getting column family histograms", e);
		}
	}

	public String[] getEndpoints(String host, String keyspace, String columnFamily, String key)
			throws AxisFault {
		try {
			return clusterStatsProxyAdminStub.getEndpoints(host, keyspace, columnFamily, key);
		} catch (Exception e) {
			throw new AxisFault("Error while getting endpoints information", e);
		}
	}

	public String[] getSSTables(String host, String keyspace, String columnFamily, String key)
			throws AxisFault {
		try {
			return clusterStatsProxyAdminStub.getSSTables(host, keyspace, columnFamily, key);
		} catch (Exception e) {
			throw new AxisFault("Error while getting SS Tables", e);
		}
	}
}
