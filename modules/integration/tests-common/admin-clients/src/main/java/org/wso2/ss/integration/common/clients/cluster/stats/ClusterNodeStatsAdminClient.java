package org.wso2.ss.integration.common.clients.cluster.stats;/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/

import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.cassandra.cluster.proxy.stub.data.xsd.ProxyClusterNetstat;
import org.wso2.carbon.cassandra.cluster.proxy.stub.data.xsd.ProxyClusterRingInformation;
import org.wso2.carbon.cassandra.cluster.proxy.stub.data.xsd.ProxyCompactionStats;
import org.wso2.carbon.cassandra.cluster.proxy.stub.data.xsd.ProxyKeyspaceInfo;
import org.wso2.carbon.cassandra.cluster.proxy.stub.data.xsd.ProxyNodeInformation;
import org.wso2.carbon.cassandra.cluster.proxy.stub.data.xsd.ProxyThreadPoolInfo;
import org.wso2.carbon.cassandra.cluster.proxy.stub.stats.ClusterStatsProxyAdminStub;

public class ClusterNodeStatsAdminClient {
    private static final Log log = LogFactory.getLog(ClusterNodeStatsAdminClient.class);

    private ClusterStatsProxyAdminStub clusterStatsProxyAdminStub;

    public ClusterNodeStatsAdminClient(ConfigurationContext ctx, String serverURL,
                                                    String cookie)
            throws AxisFault {
        init(ctx, serverURL, cookie);
    }

    private void init(ConfigurationContext ctx,
                      String serverURL,
                      String cookie) throws AxisFault {
        String serviceURL = serverURL + "ClusterStatsProxyAdmin";
        clusterStatsProxyAdminStub = new ClusterStatsProxyAdminStub(ctx, serviceURL);
        ServiceClient client = clusterStatsProxyAdminStub._getServiceClient();
        Options options = client.getOptions();
        options.setManageSession(true);
        options.setProperty(org.apache.axis2.transport.http.HTTPConstants.COOKIE_STRING, cookie);
        options.setProperty(Constants.Configuration.ENABLE_MTOM, Constants.VALUE_TRUE);
        options.setTimeOutInMilliSeconds(10000);
    }

    public ProxyClusterRingInformation[] getRing(
            String host, String keyspace)
            throws AxisFault {
        try{
        return clusterStatsProxyAdminStub.getRing(host,keyspace);
        }catch (Exception e)
        {
            throw new AxisFault("Error while getting node ring info",e);
        }
    }
    
    public ProxyNodeInformation getNodeInfo(String host) throws AxisFault
    {
        try{
            return clusterStatsProxyAdminStub.getInfo(host);
        }catch (Exception e)
        {
            throw new AxisFault("Error while getting node info",e);
        }  
    }

    public ProxyKeyspaceInfo[] getCfstats(String host) throws AxisFault {
        try{
            return clusterStatsProxyAdminStub.getCfstats(host);
        }catch (Exception e)
        {
            throw new AxisFault("Error while getting column family info",e);
        }
    }

    public String getVersion(String host) throws AxisFault {
        try{
            return clusterStatsProxyAdminStub.getVersion(host);
        }catch (Exception e)
        {
            throw new AxisFault("Error while getting version",e);
        }  
    }
    public ProxyThreadPoolInfo getTpstats(String host)
            throws AxisFault {
        try{
            return clusterStatsProxyAdminStub.getTpstats(host);
        }catch (Exception e){
            throw new AxisFault("Error while getting thread pool information",e);
        }
    }

    public ProxyCompactionStats getCompactionStats(String host)
            throws AxisFault {
        try{
            return clusterStatsProxyAdminStub.getCompactionStats(host);
        }catch (Exception e){
            throw new AxisFault("Error while getting compaction stats",e);
        }
    }

    public String getGossipInfo(String host)
            throws AxisFault {
        try{
            return clusterStatsProxyAdminStub.getGossipInfo(host);
        }catch (Exception e){
            throw new AxisFault("Error while getting gossip information",e);
        }
    }

    public ProxyClusterNetstat getNetstat(String connectedHost,String host)
            throws AxisFault {
        try{
            return clusterStatsProxyAdminStub.getNetstat(connectedHost, host);
        }catch (Exception e){
            throw new AxisFault("Error while getting network information",e);
        }
    }

    public String getTokenRemovalStatus(String host)
            throws AxisFault {
        try{
            return clusterStatsProxyAdminStub.getTokenRemovalStatus(host);
        }catch (Exception e){
            throw new AxisFault("Error while getting token removal status",e);
        }
    }
    
    public String[] getRangekeysample(String host)
            throws AxisFault {
        try{
            return clusterStatsProxyAdminStub.getRangekeysample(host);
        }catch (Exception e){
            throw new AxisFault("Error while getting range key sample",e);
        }
    }
    
    public String[] getKeyspaces(String host)
            throws AxisFault {
        try{
            return clusterStatsProxyAdminStub.getKeyspaces(host);
        }catch (Exception e){
            throw new AxisFault("Error while getting getKeyspaces",e);
        }
    }
}
