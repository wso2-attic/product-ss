package org.wso2.carbon.ss.integration.test.cassandra;


public class ClusterMgtTestCase {
    /*private EnvironmentVariables ssServer;
    private UserInfo userInfo;
    private EnvironmentBuilder builder;
    private final String keyspaceName = "TestKeyspace";
    private String backendUrl;
    private String sessionCookie;
    private String serviceeUrl;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws LoginAuthenticationExceptionException, RemoteException {
        userInfo = UserListCsvReader.getUserInfo(0);
        builder = new EnvironmentBuilder().ss(0);
        ssServer = builder.build().getSs();
        backendUrl = ssServer.getBackEndUrl();
        sessionCookie = ssServer.getSessionCookie();
        serviceeUrl = ssServer.getServiceUrl();
    }

    @Test
    public void testCreateCluster()
            throws Exception {
        String endPoint = backendUrl + "CassandraClusterAdmin";
        CassandraClusterAdminStub cassandraClusterAdminStub = new CassandraClusterAdminStub(endPoint);
        AuthenticateStub.authenticateStub(sessionCookie, cassandraClusterAdminStub);

        NodeInformation[] nodeInformations = cassandraClusterAdminStub.getNodes();
        for (NodeInformation nodeInformation : nodeInformations) {
            String token = nodeInformation.getToken();
            if (token != null) {
                assertTrue(token.length() > 0);
            }
        }
    }*/
}
