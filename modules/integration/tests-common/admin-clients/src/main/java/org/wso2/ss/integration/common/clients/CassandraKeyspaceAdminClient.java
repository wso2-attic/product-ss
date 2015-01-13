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
import org.wso2.carbon.cassandra.mgt.stub.ks.CassandraKeyspaceAdminStub;
import org.wso2.carbon.cassandra.mgt.stub.ks.CassandraServerManagementExceptionException;
import org.wso2.carbon.cassandra.mgt.stub.ks.xsd.AuthorizedRolesInformation;
import org.wso2.carbon.cassandra.mgt.stub.ks.xsd.ColumnFamilyInformation;
import org.wso2.carbon.cassandra.mgt.stub.ks.xsd.KeyspaceInformation;
import org.wso2.carbon.cassandra.mgt.stub.ks.xsd.TokenRangeInformation;
import org.wso2.carbon.integration.common.admin.client.utils.AuthenticateStubUtil;

import java.rmi.RemoteException;

/**
 * The WS dataaccess to access the Cassandra Admin Service
 */

public class CassandraKeyspaceAdminClient {

    private static final Log log = LogFactory.getLog(CassandraKeyspaceAdminClient.class);

    private CassandraKeyspaceAdminStub cassandraAdminStub;

    private String serviceName = "CassandraKeyspaceAdmin";
    /*public CassandraKeyspaceAdminClient(ConfigurationContext ctx, String serverURL, String cookie)
            throws AxisFault {
        init(ctx, serverURL, cookie);
    }*/

    public CassandraKeyspaceAdminClient(String backEndUrl, String sessionCookie) throws AxisFault {
        String endPoint = backEndUrl + serviceName;
        cassandraAdminStub = new CassandraKeyspaceAdminStub(endPoint);
        AuthenticateStubUtil.authenticateStub(sessionCookie, cassandraAdminStub);
    }

    private void init(ConfigurationContext ctx,
                      String serverURL,
                      String cookie) throws AxisFault {
        String serviceURL = serverURL + serviceName;
        cassandraAdminStub = new CassandraKeyspaceAdminStub(ctx, serviceURL);
        ServiceClient client = cassandraAdminStub._getServiceClient();
        Options options = client.getOptions();
        options.setManageSession(true);
        options.setProperty(org.apache.axis2.transport.http.HTTPConstants.COOKIE_STRING, cookie);
        options.setProperty(Constants.Configuration.ENABLE_MTOM, Constants.VALUE_TRUE);
        options.setTimeOutInMilliSeconds(10000);
    }

   /* public CassandraKeyspaceAdminClient(String backEndUrl, String userName, String password)
            throws AxisFault {
        String endPoint = backEndUrl + serviceName;
        cassandraAdminStub = new CassandraKeyspaceAdminStub(endPoint);
        AuthenticateStubUtil.authenticateStub(userName, password, cassandraAdminStub);
    }*/

    /**
     * Returns Cluster Name
     *
     * @return cluster name
     * @throws AxisFault
     */

    public String getClusterName(String envName, String clusterName) throws AxisFault {
        try {
            return cassandraAdminStub.getClusterName(envName, clusterName);
        } catch (Exception e) {
            throw new AxisFault("Error retrieving Cluster Name !", e);
        }
    }

    /**
     * Gets all keyspaces in a cluster
     *
     * @param clusterName The name of the cluster
     * @param username    The name of the current user
     * @param password    The password of the current user
     * @return A <code>String</code> array representing the names of keyspaces
     * @throws AxisFault For errors during locating   kepspaces
     */

    public KeyspaceInformation[] lisKeyspaces(String envName, String clusterName, String username, String password)
            throws AxisFault {
        try {
            return cassandraAdminStub.listKeyspaces(envName, clusterName, username, password);
        } catch (Exception e) {
            throw new AxisFault("Error retrieving keyspace names !", e);
        }
    }

    /**
     * Get all the keyspaces belong to the currently singed up user
     *
     * @return A <code>String</code> array representing the names of keyspaces
     * @throws AxisFault For errors during locating  kepspaces
     */
    public KeyspaceInformation[] listKeyspacesOfCurrentUSer(String envName) throws AxisFault {
        try {
            return cassandraAdminStub.listKeyspacesOfCurrentUser(envName);
        } catch (Exception e) {
            throw new AxisFault("Error retrieving keyspace names !", e);
        }
    }

    /**
     * Get all the CFs belong to the currently singed up user for a given keyspace
     *
     * @param keyspaceName the name of the keyspace
     * @return A <code>String</code> array representing the names of CFs
     * @throws AxisFault For errors during locating CFs
     */
    public String[] listColumnFamiliesOfCurrentUser(String envName, String clusterName, String keyspaceName)
            throws AxisFault {
        validateKeyspace(keyspaceName);
        try {
            return cassandraAdminStub.listColumnFamiliesOfCurrentUser(envName, clusterName, keyspaceName);
        } catch (Exception e) {
            throw new AxisFault("Error retrieving CF names !", e);
        }
    }

    /**
     * Get the CF information for a given cf which belongs to the currently singed up user
     *
     * @param keyspaceName the name of the keyspace
     * @param cfName       the name of the CF
     * @return An instance of <code>ColumnFamilyInformation </code>
     * @throws AxisFault For errors during locating the CF
     */
    public ColumnFamilyInformation getColumnFamilyInformationOfCurrentUser(String envName, String clusterName,
                                                                           String keyspaceName,
                                                                           String cfName)
            throws AxisFault {
        validateKeyspace(keyspaceName);
        validateCF(cfName);
        try {
            return cassandraAdminStub.getColumnFamilyOfCurrentUser(envName, clusterName, keyspaceName, cfName);
        } catch (Exception e) {
            throw new AxisFault("Error retrieving the CF for the name " + cfName, e);
        }
    }

    /**
     * Set permissions for a resource
     *
     * @param infoList AuthorizedRolesInformation List
     * @return true if the operation was successful.
     * @throws AxisFault For errors during sharing the resource
     */
    public boolean authorizeRolesForResource(AuthorizedRolesInformation[] infoList) throws AxisFault {
        try {
            return cassandraAdminStub.authorizeRolesForResource(infoList);
        } catch (Throwable e) {
            throw new AxisFault("Error sharing a resource !", e);
        }
    }

    /**
     * Clear permissions for a resource
     *
     * @param infoList AuthorizedRolesInformation List
     * @return true if the operation was successful.
     * @throws AxisFault For errors during sharing the resource
     */
    public boolean clearResourcePermissions(AuthorizedRolesInformation[] infoList) throws AxisFault {
        try {
            return cassandraAdminStub.clearResourcePermissions(infoList);
        } catch (Exception e) {
            throw new AxisFault("Error clear authorization for a resource !", e);
        }
    }

    /**
     * Get all the meta-data belong to the currently singed up user for a given keyspace
     *
     * @param keyspaceName the name of the keyspace
     * @return A <code>KeyspaceInformation</code> representing the meta-data of a keyspace
     * @throws AxisFault For errors during locating keyspace
     */
    public KeyspaceInformation getKeyspaceOfCurrentUser(String envName, String clusterName, String keyspaceName)
            throws AxisFault {
        validateKeyspace(keyspaceName);
        try {
            return cassandraAdminStub.getKeyspaceOfCurrentUser(envName, clusterName, keyspaceName);
        } catch (Exception e) {
            throw new AxisFault("Error retrieving keyspace !", e);
        }
    }

    /**
     * Create a new keyspace
     *
     * @param keyspaceInformation keyspace information
     * @throws AxisFault For errors during adding a keyspace
     */
    public void addKeyspace(KeyspaceInformation keyspaceInformation)
            throws AxisFault {
        validateKeyspaceInformation(keyspaceInformation);
        try {
            cassandraAdminStub.addKeyspace(keyspaceInformation);
        } catch (Exception e) {
            throw new AxisFault("Error adding the keyspace !", e);
        }
    }

    /**
     * Updates an existing keyspace
     *
     * @param keyspaceInformation keyspace information
     * @throws AxisFault For errors during adding a keyspace
     */
    public void updateKeyspace(KeyspaceInformation keyspaceInformation)
            throws AxisFault {
        validateKeyspaceInformation(keyspaceInformation);
        try {
            cassandraAdminStub.updatedKeyspace(keyspaceInformation);
        } catch (Exception e) {
            throw new AxisFault("Error updating the keyspace !", e);
        }
    }

    /**
     * Get all users
     *
     * @return A <code>String</code> array representing the names of all the users
     * @throws AxisFault For errors during looking up users
     */
    public String[] getAllRoles() throws AxisFault {
        try {
            return cassandraAdminStub.getAllRoles();
        } catch (Exception e) {
            throw new AxisFault("Error retrieving user names !", e);
        }
    }

    /**
     * Remove a keyspace
     *
     * @param keyspaceName the name of the keyspace
     * @return true for success
     * @throws AxisFault For errors during removing a keyspace
     */
    public boolean deleteKeyspace(String envName, String clusterName, String keyspaceName) throws AxisFault {
        validateKeyspace(keyspaceName);
        try {
            return cassandraAdminStub.deleteKeyspace(envName, clusterName, keyspaceName);
        } catch (Exception e) {
            throw new AxisFault("Error removing the keyspace !", e);
        }

    }

    /**
     * Deletes a CF
     *
     * @param keyspaceName     the name of the keyspace
     * @param columnFamilyName CF's name
     * @return true for success
     * @throws AxisFault for errors during removing a CF
     */
    public boolean deleteColumnFamily(String envName, String clusterName, String keyspaceName, String columnFamilyName)
            throws AxisFault {
        validateKeyspace(keyspaceName);
        validateCF(columnFamilyName);
        try {
            return cassandraAdminStub.deleteColumnFamily(envName, clusterName, keyspaceName, columnFamilyName);
        } catch (Exception e) {
            throw new AxisFault("Error removing the CF !", e);
        }
    }

    /**
     * Add a CF under a given keyspace
     *
     * @param columnFamilyInformation information about a CF
     * @throws AxisFault for errors during adding a CF
     */
    public void addColumnFamily(String envName, String clusterName, ColumnFamilyInformation columnFamilyInformation)
            throws AxisFault {
        validateColumnFamilyInformation(columnFamilyInformation);
        try {
            cassandraAdminStub.addColumnFamily(envName, clusterName, columnFamilyInformation);
        } catch (Exception e) {
            throw new AxisFault("Error adding the CF !", e);
        }
    }

    /**
     * Update an existing CF under a given keyspace
     *
     * @param columnFamilyInformation CF information
     * @throws AxisFault for errors during adding a CF
     */
    public void updateColumnFamily(String envName, String clusterName, ColumnFamilyInformation columnFamilyInformation)
            throws AxisFault {
        validateColumnFamilyInformation(columnFamilyInformation);
        try {
            cassandraAdminStub.updateColumnFamily(envName, clusterName, columnFamilyInformation);
        } catch (Exception e) {
            throw new AxisFault("Error updating the CF !", e);
        }
    }

    /**
     * Get the token range of a given key space
     *
     * @param keyspace keyspace name
     * @return Token range as a list
     * @throws AxisFault for errors during calling backend service
     */
    public TokenRangeInformation[] getTokenRange(String envName, String clusterName, String keyspace)
            throws AxisFault {
        validateKeyspace(keyspace);
        try {
            return cassandraAdminStub.getTokenRange(envName, clusterName, keyspace);
        } catch (Exception e) {
            log.error("Error getting the token range of the keyspace : " + keyspace, e);
        }
        return null;
    }

    /**
     * Get permission information of all roles for a given resource (Root/Keyspace/Column Family)
     *
     * @param path
     * @return
     */
    public AuthorizedRolesInformation[] getResourcePermissionsOfRoles(String path) {
        try {
            return cassandraAdminStub.getResourcePermissionsOfRoles(path);
        } catch (Exception e) {
            log.error("Error retrieving role list" + path, e);
        }
        return new AuthorizedRolesInformation[0];
    }

    /**
     * validate Keyspace Name
     *
     * @param keyspaceName
     * @throws AxisFault
     */
    private void validateKeyspace(String keyspaceName) throws AxisFault {
        if (keyspaceName == null || "".equals(keyspaceName.trim())) {
            throw new AxisFault("The keyspace name is empty or null");
        }
    }

    private void validateKeyspaceInformation(KeyspaceInformation keyspaceInformation)
            throws AxisFault {
        if (keyspaceInformation == null) {
            throw new AxisFault("The keyspace information is empty or null");
        }
        validateKeyspace(keyspaceInformation.getName());
    }

    private void validateColumnFamilyInformation(ColumnFamilyInformation columnFamilyInformation)
            throws AxisFault {
        if (columnFamilyInformation == null) {
            throw new AxisFault("The column family information is empty or null");
        }
        validateKeyspace(columnFamilyInformation.getKeyspace());
        validateCF(columnFamilyInformation.getName());
    }

    private void validateCF(String columnFamilyName) throws AxisFault {
        if (columnFamilyName == null || "".equals(columnFamilyName.trim())) {
            throw new AxisFault("The column family name name is empty or null");
        }
    }

    public void authorizeRoles(AuthorizedRolesInformation[] authorizedRolesInformations)
            throws RemoteException, CassandraServerManagementExceptionException {
        cassandraAdminStub.authorizeRolesForResource(authorizedRolesInformations);
    }
}
