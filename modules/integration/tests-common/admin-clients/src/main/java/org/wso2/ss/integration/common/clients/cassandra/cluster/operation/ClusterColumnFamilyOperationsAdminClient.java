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
package org.wso2.ss.integration.common.clients.cassandra.cluster.operation;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.cassandra.cluster.proxy.stub.operation.ClusterOperationProxyAdminStub;
import org.wso2.carbon.integration.common.admin.client.utils.AuthenticateStubUtil;

/**
 * Class for performing the column family operations
 */
public class ClusterColumnFamilyOperationsAdminClient {

	private static final Log log = LogFactory.getLog(ClusterColumnFamilyOperationsAdminClient.class);

	private ClusterOperationProxyAdminStub cassandraClusterToolsAdminStub;
	private String serviceName = "ClusterOperationProxyAdmin";

	public ClusterColumnFamilyOperationsAdminClient(String backEndUrl, String sessionCookie) throws AxisFault {
		String endPoint = backEndUrl + serviceName;
		cassandraClusterToolsAdminStub = new ClusterOperationProxyAdminStub(endPoint);
		AuthenticateStubUtil.authenticateStub(sessionCookie, cassandraClusterToolsAdminStub);
	}

	/**
	 * Flush column family
	 *
	 * @param keyspace       Name of the keyspace which is the target column family located
	 * @param columnFamilies Name of the column families
	 * @return Return true if the operation is success and else false
	 */
	public boolean flushColumnFamilies(String host, String keyspace, String[] columnFamilies) throws AxisFault {
		validateKeyspace(keyspace);
		validateColumnFamily(columnFamilies);
		try {
			return cassandraClusterToolsAdminStub.flushColumnFamilies(host, keyspace, columnFamilies);
		} catch (Exception e) {
			throw new AxisFault("Error while flushing column family", e);
		}
	}

	/**
	 * Repair column family
	 *
	 * @param keyspace       Name of the keyspace which is the target column family located
	 * @param columnFamilies Name of the column families
	 * @return Return true if the operation is success and else false
	 */
	public boolean repairColumnFamilies(String host, String keyspace, String[] columnFamilies) throws AxisFault {
		validateKeyspace(keyspace);
		validateColumnFamily(columnFamilies);
		try {
			return cassandraClusterToolsAdminStub.repairColumnFamilies(host, keyspace, columnFamilies);
		} catch (Exception e) {
			throw new AxisFault("Error while repairing column family", e);
		}
	}

	/**
	 * Cleanup column family
	 *
	 * @param keyspace       Name of the keyspace which is the target column family located
	 * @param columnFamilies Name of the column families
	 * @return Return true if the operation is success and else false
	 */
	public boolean cleanUpColumnFamilies(String host, String keyspace, String[] columnFamilies) throws AxisFault {
		validateKeyspace(keyspace);
		validateColumnFamily(columnFamilies);
		try {
			return cassandraClusterToolsAdminStub.cleanUpColumnFamilies(host, keyspace, columnFamilies);
		} catch (Exception e) {
			throw new AxisFault("Error while cleanUp column family", e);
		}
	}

	/**
	 * Compact column family
	 *
	 * @param keyspace       Name of the keyspace which is the target column family located
	 * @param columnFamilies Name of the column families
	 * @return Return true if the operation is success and else false
	 */
	public boolean compactColumnFamilies(String host, String keyspace, String[] columnFamilies) throws AxisFault {
		validateKeyspace(keyspace);
		validateColumnFamily(columnFamilies);
		try {
			return cassandraClusterToolsAdminStub.compactColumnFamilies(host, keyspace, columnFamilies);
		} catch (Exception e) {
			throw new AxisFault("Error while compacting column family", e);
		}
	}

	/**
	 * Scrub column family
	 *
	 * @param keyspace       Name of the keyspace which is the target column family located
	 * @param columnFamilies Name of the column families
	 * @return Return true if the operation is success and else false
	 */
	public boolean scrubColumnFamilies(String host, String keyspace, String[] columnFamilies) throws AxisFault {
		validateKeyspace(keyspace);
		validateColumnFamily(columnFamilies);
		try {
			return cassandraClusterToolsAdminStub.scrubColumnFamilies(host, keyspace, columnFamilies);
		} catch (Exception e) {
			throw new AxisFault("Error while scrub column family", e);
		}
	}

	/**
	 * UpgradeSSTables column family
	 *
	 * @param keyspace       Name of the keyspace which is the target column family located
	 * @param columnFamilies Name of the column families
	 * @return Return true if the operation is success and else false
	 */
	public boolean upgradeSSTablesColumnFamilies(String host, String keyspace, String[] columnFamilies) throws AxisFault {
		validateKeyspace(keyspace);
		validateColumnFamily(columnFamilies);
		try {
			return cassandraClusterToolsAdminStub.upgradeSSTablesColumnFamilies(host, keyspace, columnFamilies);
		} catch (Exception e) {
			throw new AxisFault("Error while upgradeSSTables column family", e);
		}
	}

	/**
	 * validate Keyspace Name
	 *
	 * @param keyspaceName Name of the keyspace
	 * @throws AxisFault
	 */
	private void validateKeyspace(String keyspaceName) throws AxisFault {
		if (keyspaceName == null || "".equals(keyspaceName.trim())) {
			throw new AxisFault("The keyspace name is empty or null");
		}
	}

	/**
	 * validate column family Name
	 *
	 * @param columnFamilies Name of the column families
	 */
	private void validateColumnFamily(String[] columnFamilies) throws AxisFault {
		if (columnFamilies.length == 0) {
			throw new AxisFault("No column families specified");
		}
	}

	public void rebuildCF(String host, String keyspace, String columnFamily) throws AxisFault {
		validateKeyspace(keyspace);
		try {
			cassandraClusterToolsAdminStub.rebuildColumnFamily(host, keyspace, columnFamily);
		} catch (Exception e) {
			throw new AxisFault("Error while rebuild column family", e);
		}
	}

	public void rebuildCFWithIndex(String host, String keyspace, String columnFamily, String[] index) throws AxisFault {
		validateKeyspace(keyspace);
		try {
			cassandraClusterToolsAdminStub.rebuildColumnFamilyWithIndex(host, keyspace, columnFamily, index);
		} catch (Exception e) {
			throw new AxisFault("Error while rebuild with index column family", e);
		}
	}

	public void refreshCF(String host, String keyspace, String columnFamily) throws AxisFault {
		validateKeyspace(keyspace);
		try {
			cassandraClusterToolsAdminStub.refresh(host, keyspace, columnFamily);
		} catch (Exception e) {
			throw new AxisFault("Error while refresh column family", e);
		}
	}

	public void setCompactionThresholds(String host, String keyspace, String columnFamily, int minT, int maxT)
			throws AxisFault {
		validateKeyspace(keyspace);
		try {
			cassandraClusterToolsAdminStub.setCompactionThresholds(host, keyspace, columnFamily, minT, maxT);
		} catch (Exception e) {
			throw new AxisFault("Error while setting compaction thresholds for column family", e);
		}
	}

	public void takeCFSnapshot(String host, String keyspace, String columnFamily, String tag) throws AxisFault {
		validateKeyspace(keyspace);
		try {
			cassandraClusterToolsAdminStub.takeSnapshotOfColumnFamily(host, tag, keyspace, columnFamily);
		} catch (Exception e) {
			throw new AxisFault("Error while taking column family snapshot", e);
		}
	}
}
