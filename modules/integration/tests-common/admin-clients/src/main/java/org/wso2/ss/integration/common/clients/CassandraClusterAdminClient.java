/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.ss.integration.common.clients;

import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.cassandra.mgt.stub.cluster.CassandraClusterAdminStub;
import org.wso2.carbon.cassandra.mgt.stub.cluster.xsd.ColumnFamilyStats;
import org.wso2.carbon.cassandra.mgt.stub.cluster.xsd.NodeInformation;

/**
 * A client to access CassandraClusterAdmin service
 */
public class CassandraClusterAdminClient {

    private static final Log log = LogFactory.getLog(CassandraClusterAdminClient.class);

    private CassandraClusterAdminStub cassandraAdminStub;

    public CassandraClusterAdminClient(ConfigurationContext ctx, String serverURL, String cookie)
            throws AxisFault {
        init(ctx, serverURL, cookie);
    }

    private void init(ConfigurationContext ctx,
                      String serverURL,
                      String cookie) throws AxisFault {
        String serviceURL = serverURL + "CassandraClusterAdmin";
        cassandraAdminStub = new CassandraClusterAdminStub(ctx, serviceURL);
        ServiceClient client = cassandraAdminStub._getServiceClient();
        Options options = client.getOptions();
        options.setManageSession(true);
        options.setProperty(org.apache.axis2.transport.http.HTTPConstants.COOKIE_STRING, cookie);
        options.setProperty(Constants.Configuration.ENABLE_MTOM, Constants.VALUE_TRUE);
        options.setTimeOutInMilliSeconds(10000);
    }

    /**
     * Retrieve the nodes of the Cassandra Cluster
     *
     * @return An array of <code>NodeInformation </node>
     * @throws AxisFault for errors during getting node informations
     */
    public NodeInformation[] listNodes() throws AxisFault {
        try {
            return cassandraAdminStub.getNodes();
        } catch (Exception e) {
            throw new AxisFault("Error retrieving nodes names !", e);
        }
    }

    /**
     * Returns a <code>ColumnFamilyStats</code> representing the stats for the column family with the given name
     *
     * @param keyspace the name of the keyspace
     * @param cf       name of the column family
     * @return <code>ColumnFamilyStats</code> instance
     */
    public ColumnFamilyStats getColumnFamilyStats(String keyspace, String cf) throws AxisFault {
        validateKeyspace(keyspace);
        validateCF(cf);
        try {
            return cassandraAdminStub.getColumnFamilyStats(keyspace, cf);
        } catch (Exception e) {
            log.error("Error retrieving nodes names !", e);
        }
        return null;
    }

    private void validateKeyspace(String keyspaceName) throws AxisFault {
        if (keyspaceName == null || "".equals(keyspaceName.trim())) {
            throw new AxisFault("The keyspace name is empty or null");
        }
    }

    private void validateCF(String columnFamilyName) throws AxisFault {
        if (columnFamilyName == null || "".equals(columnFamilyName.trim())) {
            throw new AxisFault("The column family name name is empty or null");
        }
    }
}
