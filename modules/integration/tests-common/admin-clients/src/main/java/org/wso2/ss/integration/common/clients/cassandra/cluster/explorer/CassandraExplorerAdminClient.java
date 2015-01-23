/*
*  Licensed to the Apache Software Foundation (ASF) under one
*  or more contributor license agreements.  See the NOTICE file
*  distributed with this work for additional information
*  regarding copyright ownership.  The ASF licenses this file
*  to you under the Apache License, Version 2.0 (the
*  "License"); you may not use this file except in compliance
*  with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*   * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*/
package org.wso2.ss.integration.common.clients.cassandra.cluster.explorer;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.cassandra.explorer.stub.CassandraExplorerAdminStub;
import org.wso2.carbon.cassandra.explorer.stub.data.xsd.Column;
import org.wso2.carbon.cassandra.explorer.stub.data.xsd.Row;
import org.wso2.carbon.integration.common.admin.client.utils.AuthenticateStubUtil;

public class CassandraExplorerAdminClient {

	private CassandraExplorerAdminStub explorerAdminStub;
	private static final Log log = LogFactory.getLog(CassandraExplorerAdminClient.class);
	private static String SSERVICE_ENDPOINT = "CassandraExplorerAdmin";

	public CassandraExplorerAdminClient(String backEndUrl, String sessionCookie) throws AxisFault {
		String endPoint = backEndUrl + SSERVICE_ENDPOINT;
		explorerAdminStub = new CassandraExplorerAdminStub(endPoint);
		AuthenticateStubUtil.authenticateStub(sessionCookie, explorerAdminStub);
	}

	public Column[] getPaginateSliceforColumns(String keyspace, String columnFamily, String rowName,
	                                           int startingNo, int limit)
			throws AxisFault {
		try {
			return explorerAdminStub.getColumnPaginateSlice(keyspace, columnFamily, rowName, startingNo, limit);
		} catch (Exception e) {
			throw new AxisFault("Unable to retrieve data. " + e.getMessage(), e);
		}
	}

	public int getNoOfColumns(String keyspace, String columnFamily, String rowName)
			throws AxisFault {
		try {
			return explorerAdminStub.getNoOfColumns(keyspace, columnFamily, rowName);
		} catch (Exception e) {
			throw new AxisFault("Unable to retrieve data. " + e.getMessage(), e);
		}
	}

	public Column[] searchColumns(String keyspace, String columnFamily, String rowName,
	                              String searchKey, int startingNo, int limit)
			throws AxisFault {
		try {
			return explorerAdminStub.searchColumns(keyspace, columnFamily, rowName, searchKey,
					startingNo, limit);
		} catch (Exception e) {
			throw new AxisFault("Unable to retrieve data. " + e.getMessage(), e);
		}
	}

	public int getNoOfFilteredResultsoforColumns(String keyspace, String columnFamily,
	                                             String rowName, String searchKey)
			throws AxisFault {
		try {
			return explorerAdminStub.getNoOfColumnSearchResults(keyspace, columnFamily, rowName, searchKey);
		} catch (Exception e) {
			throw new AxisFault("Unable to retrieve data. " + e.getMessage(), e);
		}
	}

	public Row[] getPaginateSliceforRows(String keyspace, String columnFamily, int startingNo, int limit)
			throws AxisFault {
		try {
			return explorerAdminStub.getRowPaginateSlice(keyspace, columnFamily, startingNo, limit);
		} catch (Exception e) {
			throw new AxisFault("Unable to retrieve data. " + e.getMessage(), e);
		}
	}

	public Row[] searchRows(String keyspace, String columnFamily, String searchKey, int startingNo, int limit)
			throws AxisFault {
		try {
			return explorerAdminStub.searchRows(keyspace, columnFamily, searchKey,
					startingNo, limit);
		} catch (Exception e) {
			throw new AxisFault("Unable to retrieve data. " + e.getMessage(), e);
		}
	}

	public int getNoOfFilteredResultsoforRows(String keyspace, String columnFamily, String searchKey)
			throws AxisFault {
		try {
			return explorerAdminStub.getNoOfRowSearchResults(keyspace, columnFamily, searchKey);
		} catch (Exception e) {
			throw new AxisFault("Unable to retrieve data. " + e.getMessage(), e);
		}
	}

	public boolean connectToCassandraCluster(String clusterName, String connectionUrl,
	                                         String userName, String password)
			throws AxisFault {
		try {
			return explorerAdminStub.connectToCassandraCluster(clusterName, connectionUrl, userName,
					password);
		} catch (Exception e) {
			throw new AxisFault("Unable to connect to cluster. " + e.getMessage(), e);
		}
	}

	public String[] getKeyspaces() throws AxisFault {
		try {
			return explorerAdminStub.getKeyspaces();
		} catch (Exception e) {
			throw new AxisFault("Unable to retrieve keyspaces. " + e.getMessage(), e);
		}
	}

	public String[] getColumnFamilies(String keyspace) throws AxisFault {
		try {
			return explorerAdminStub.getColumnFamilies(keyspace);
		} catch (Exception e) {
			throw new AxisFault("Unable to retrieve column families. " + e.getMessage(), e);
		}
	}

	public int getNoOfRows(String keyspace, String columnFamily) throws AxisFault {
		try {
			return explorerAdminStub.getNoOfRows(keyspace, columnFamily);
		} catch (Exception e) {
			throw new AxisFault("Unable to retrieve data. " + e.getMessage(), e);
		}
	}

	public void setMaxRowCount(int maxRowCount)
			throws AxisFault {
		try {
			explorerAdminStub.setMaxRowCount(maxRowCount);
		} catch (Exception e) {
			throw new AxisFault("Unable to retrieve data. " + e.getMessage(), e);
		}
	}

}
