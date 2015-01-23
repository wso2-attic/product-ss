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
import org.wso2.carbon.cassandra.cluster.proxy.stub.data.xsd.ProxyKeyspaceInitialInfo;
import org.wso2.carbon.cassandra.cluster.proxy.stub.data.xsd.ProxyNodeInitialInfo;
import org.wso2.carbon.cassandra.cluster.proxy.stub.operation.ClusterOperationProxyAdminStub;
import org.wso2.carbon.integration.common.admin.client.utils.AuthenticateStubUtil;

/**
 * Class for performing node level operations
 */
public class ClusterNodeOperationsAdminClient {

	private static final Log log = LogFactory.getLog(ClusterNodeOperationsAdminClient.class);
	private ClusterOperationProxyAdminStub cassandraClusterToolsAdminStub;

	private String serviceName = "ClusterOperationProxyAdmin";

	public ClusterNodeOperationsAdminClient(String backEndUrl, String sessionCookie) throws AxisFault {
		String endPoint = backEndUrl + serviceName;
		cassandraClusterToolsAdminStub = new ClusterOperationProxyAdminStub(endPoint);
		AuthenticateStubUtil.authenticateStub(sessionCookie, cassandraClusterToolsAdminStub);
	}

	/**
	 * Drain the node
	 *
	 * @return Return true if the operation successfully perform and else false
	 */
	public boolean drainNode(String host) throws AxisFault {
		try {
			return cassandraClusterToolsAdminStub.drainNode(host);
		} catch (Exception e) {
			throw new AxisFault("Error while draining the node", e);
		}
	}

	/**
	 * Decommission the node
	 *
	 * @return Return true if the operation successfully perform and else false
	 */
	public boolean decommissionNode(String host) throws AxisFault {

		try {
			return cassandraClusterToolsAdminStub.decommissionNode(host);
		} catch (Exception e) {
			throw new AxisFault("Error while decommission the node", e);
		}
	}

	/**
	 * Perform garbage collector
	 */
	public void performGC(String host) throws AxisFault {
		try {
			cassandraClusterToolsAdminStub.performGC(host);
		} catch (Exception e) {
			throw new AxisFault("Error while perform GC", e);
		}
	}

	/**
	 * Move node to new token
	 *
	 * @param newToken Name of the token
	 * @return Return true if the operation successfully perform and else false
	 */
	public void moveNode(String host, String newToken) throws AxisFault {
		try {
			cassandraClusterToolsAdminStub.moveNode(host, newToken);
		} catch (Exception e) {
			throw new AxisFault("Error while moving node", e);
		}
	}

	/**
	 * Take a backup of entire node
	 *
	 * @param tag Name of the backuo
	 * @return Return true if the operation successfully perform and else false
	 */
	public void takeNodeSnapShot(String host, String tag) throws AxisFault {
		try {
			cassandraClusterToolsAdminStub.takeSnapshotOfNode(host, tag);
		} catch (Exception e) {
			throw new AxisFault("Error while taking snapshot of node", e);
		}
	}

	/**
	 * Clear a snapshot of node
	 *
	 * @param tag Name of the snapshot which need to be clear
	 * @return Return true if the operation successfully perform and else false
	 * @throws AxisFault for unable to perform operation due to exception
	 */
	public void clearNodeSnapShot(String host, String tag) throws AxisFault {
		try {
			cassandraClusterToolsAdminStub.clearSnapshotOfNode(host, tag);
		} catch (Exception e) {
			throw new AxisFault("Error while clearing snapshot of node", e);
		}
	}

	/**
	 * Start RPC server
	 */
	public void startRPCServer(String host) throws AxisFault {
		try {
			cassandraClusterToolsAdminStub.startRPCServer(host);
		} catch (Exception e) {
			throw new AxisFault("Error while starting RPC server", e);
		}
	}

	/**
	 * Stop RPC server
	 */
	public void stopRPCServer(String host) throws AxisFault {
		try {
			cassandraClusterToolsAdminStub.stopRPCServer(host);
		} catch (Exception e) {
			throw new AxisFault("Error while stopping RPC server", e);
		}
	}

	/**
	 * Start Gossip server
	 */
	public void startGossipServer(String host) throws AxisFault {
		try {
			cassandraClusterToolsAdminStub.startGossipServer(host);
		} catch (Exception e) {
			throw new AxisFault("Error while starting Gossip server", e);
		}
	}

	/**
	 * Stop Gossip server
	 */
	public void stopGossipServer(String host) throws AxisFault {
		try {
			cassandraClusterToolsAdminStub.stopGossipServer(host);
		} catch (Exception e) {
			throw new AxisFault("Error while stopping Gossip server", e);
		}
	}

	/**
	 * Enable or Disable incremental backup of cassandra node
	 *
	 * @param status boolean value to set backup status
	 */
	public void setIncrementalBackUpStatus(String host, boolean status) throws AxisFault {
		try {
			cassandraClusterToolsAdminStub.setIncrementalBackUpStatus(host, status);
		} catch (Exception e) {
			throw new AxisFault("Error setting incremental backup status", e);
		}
	}

	/**
	 * Join to cassandra ring
	 */
	public boolean joinRing(String host) throws AxisFault {
		try {
			return cassandraClusterToolsAdminStub.joinCluster(host);
		} catch (Exception e) {
			throw new AxisFault("Error joining to the cluster", e);
		}
	}

	/**
	 * Get Gossip server status
	 *
	 * @return boolean
	 */
	public boolean getGossipServerStatus(String host) throws AxisFault {
		try {
			return cassandraClusterToolsAdminStub.isGossipServerEnable(host);
		} catch (Exception e) {
			throw new AxisFault("Error getting gossip server status", e);
		}
	}

	/**
	 * Get RPC server status
	 *
	 * @return boolean
	 */
	public boolean getRPCServerStatus(String host) throws AxisFault {
		try {
			return cassandraClusterToolsAdminStub.isRPCRunning(host);
		} catch (Exception e) {
			throw new AxisFault("Error getting RPC server status", e);
		}
	}

	/**
	 * Get Incremental Backup status
	 *
	 * @return boolean
	 */
	public boolean getIncrementalBackUpStatus(String host) throws AxisFault {
		try {
			return cassandraClusterToolsAdminStub.getIncrementalBackUpStatus(host);
		} catch (Exception e) {
			throw new AxisFault("Error getting RPC server status", e);
		}
	}

	/**
	 * Check whether node is join the ring
	 *
	 * @return boolean
	 */
	public boolean isJoinedRing(String host) throws AxisFault {
		try {
			return cassandraClusterToolsAdminStub.isJoined(host);
		} catch (Exception e) {
			throw new AxisFault("Error getting node join status", e);
		}
	}

	public boolean invalidateKeyCache(String host) throws AxisFault {
		try {
			return cassandraClusterToolsAdminStub.invalidateKeyCache(host);
		} catch (Exception e) {
			throw new AxisFault("Error while invalidate key cache", e);
		}
	}

	public boolean invalidateRowCache(String host) throws AxisFault {
		try {
			return cassandraClusterToolsAdminStub.invalidateKeyCache(host);
		} catch (Exception e) {
			throw new AxisFault("Error while invalidate row cache", e);
		}
	}

	public void setRowCacheCapacity(String host, int capacity) throws AxisFault {
		try {
			cassandraClusterToolsAdminStub.setRowCacheCapacity(host, capacity);
		} catch (Exception e) {
			throw new AxisFault("Error while setting row cache", e);
		}
	}

	public void setKeyCacheCapacity(String host, int capacity) throws AxisFault {
		try {
			cassandraClusterToolsAdminStub.setRowCacheCapacity(host, capacity);
		} catch (Exception e) {
			throw new AxisFault("Error while setting key cache", e);
		}
	}

	public void setStreamThroughput(String host, int capacity) throws AxisFault {
		try {
			cassandraClusterToolsAdminStub.setStreamThroughputMbPerSec(host, capacity);
		} catch (Exception e) {
			throw new AxisFault("Error while setting  stream throughput", e);
		}
	}

	public void setCompactionThroughput(String host, int capacity) throws AxisFault {
		try {
			cassandraClusterToolsAdminStub.setCompactionThroughputMbPerSec(host, capacity);
		} catch (Exception e) {
			throw new AxisFault("Error while setting  compaction throughput", e);
		}
	}

	public void rebuild(String host, String datacenter) throws AxisFault {
		try {
			cassandraClusterToolsAdminStub.rebuild(host, datacenter);
		} catch (Exception e) {
			throw new AxisFault("Error while setting  compaction throughput", e);
		}
	}

	public void stopCompaction(String host, String type) throws AxisFault {
		try {
			cassandraClusterToolsAdminStub.stopCompaction(host, type);
		} catch (Exception e) {
			throw new AxisFault("Error while setting  compaction throughput", e);
		}
	}

	public void removeToken(String host, String token) throws AxisFault {
		try {
			cassandraClusterToolsAdminStub.removeToken(host, token);
		} catch (Exception e) {
			throw new AxisFault("Error while setting  compaction throughput", e);
		}
	}

	public void forceRemoveToken(String host) throws AxisFault {
		try {
			cassandraClusterToolsAdminStub.forceRemoveCompletion(host);
		} catch (Exception e) {
			throw new AxisFault("Error while setting  compaction throughput", e);
		}
	}

	public String getTokenRemovalStatus(String host) throws AxisFault {
		try {
			return cassandraClusterToolsAdminStub.getRemovalStatus(host);
		} catch (Exception e) {
			throw new AxisFault("Error while setting  compaction throughput", e);
		}
	}

	public String[] getSnapshotTags(String host) throws AxisFault {
		try {
			return cassandraClusterToolsAdminStub.getSnapshotTags(host);
		} catch (Exception e) {
			throw new AxisFault("Error while setting  compaction throughput", e);
		}
	}

	public ProxyNodeInitialInfo getNodeInitialInfo(String host) throws AxisFault {
		try {
			return cassandraClusterToolsAdminStub.getNodeInitialInfo(host);
		} catch (Exception e) {
			throw new AxisFault("Error while getting node initial info", e);
		}
	}

	public ProxyKeyspaceInitialInfo getKeyspaceInitialInfo(String host) throws AxisFault {
		try {
			return cassandraClusterToolsAdminStub.getKeyspaceInitialInfo(host);
		} catch (Exception e) {
			throw new AxisFault("Error while getting keyspace initial info", e);
		}
	}
}
