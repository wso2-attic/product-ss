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
package org.wso2.carbon.ss.integration.test.cassandra;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.ss.SSIntegrationTest;
import org.wso2.ss.integration.common.clients.CassandraKeyspaceAdminClient;

import java.rmi.RemoteException;

import static org.testng.Assert.assertTrue;

public class CassandraKeyspaceTestCase extends SSIntegrationTest{

    private static final Log log = LogFactory.getLog(CassandraKeyspaceTestCase.class);
    private final String serviceName = "CassandraKeyspaceAdmin";
    private String serviceEndPoint;
    private CassandraKeyspaceAdminClient client;

    @BeforeClass(alwaysRun = true)
    public void initializeTest() throws Exception {
        super.init();
        serviceEndPoint = getServiceUrlHttp(serviceName);
        client = new CassandraKeyspaceAdminClient(ssContext.getContextUrls().getBackEndUrl(),sessionCookie);
    }

    @Test(groups = "wso2.ss", description = "get keyspaces for user")
    public void testRequest() throws RemoteException {
        String[] keyspaces=client.listKeyspacesOfCurrentUSer();
        assertTrue(keyspaces.length>0);
    }

    @AfterClass(alwaysRun = true)
    public void cleanUp(){

    }
}
