/*
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
package org.wso2.carbon.ss.rss.test;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.api.clients.utils.AuthenticateStub;
import org.wso2.carbon.ndatasource.ui.stub.NDataSourceAdminDataSourceException;
import org.wso2.carbon.ndatasource.ui.stub.NDataSourceAdminStub;
import org.wso2.carbon.ndatasource.ui.stub.core.services.xsd.WSDataSourceInfo;
import org.wso2.carbon.rssmanager.ui.stub.RSSAdminRSSManagerExceptionException;
import org.wso2.carbon.rssmanager.ui.stub.RSSAdminStub;
import org.wso2.carbon.rssmanager.ui.stub.types.*;
import org.wso2.carbon.rssmanager.ui.stub.types.config.environment.RSSEnvironmentContext;
import org.wso2.carbon.ss.RSSTestUtil;

import java.rmi.RemoteException;

import static org.testng.Assert.*;

public class RSSAdminServiceTestCase extends RSSBaseTestCase {

    private RSSAdminStub rssAdminStub;
    private NDataSourceAdminStub nDataSourceAdminStub;
    private RSSEnvironmentContext ctx;

    @BeforeClass(alwaysRun = true)
    public void initSetup() throws Exception {
        super.initEnvironment();

        String rssManagerAdminServiceEpr = this.getEnvironment().getBackEndUrl() +
                RSSTestConstants.EnvironmentConfigurations.RSS_ADMIN_SERVICE;
        this.rssAdminStub = new RSSAdminStub(rssManagerAdminServiceEpr);
        AuthenticateStub.authenticateStub(this.getEnvironment().getSessionCookie(), rssAdminStub);

        String ndataSourceAdminServiceEpr = this.getEnvironment().getBackEndUrl() +
                RSSTestConstants.EnvironmentConfigurations.NDATASOURCE_ADMIN_SERVICE;
        this.nDataSourceAdminStub = new NDataSourceAdminStub(ndataSourceAdminServiceEpr);
        AuthenticateStub.authenticateStub(this.getEnvironment().getSessionCookie(),
                nDataSourceAdminStub);
        ctx = new RSSEnvironmentContext();
        ctx.setEnvironmentName(RSSTestConstants.EnvironmentConfigurations.RSS_TEST_ENVIRONMENT);
        ctx.setRssInstanceName(RSSTestConstants.EnvironmentConfigurations.RSS_MANAGEMENT_MODE_SYSTEM);
    }

    @Test(description = "Create database")
    public void createDatabase() throws Exception {
        try {
            boolean status = this.getRSSAdminStub().isDatabaseExist(ctx,
                    RSSTestConstants.SampleConfigurations.SAMPLE_DATABASE_NAME);
            assertFalse(status, "Database with the name of sample database already exists");

            Database database = new Database();
            database.setName(RSSTestConstants.SampleConfigurations.SAMPLE_DATABASE_NAME);
            database.setRssInstanceName(
                    RSSTestConstants.EnvironmentConfigurations.RSS_MANAGEMENT_MODE_SYSTEM);
            rssAdminStub.createDatabase(ctx,database);

            status = this.getRSSAdminStub().isDatabaseExist(ctx,
                    RSSTestConstants.SampleConfigurations.SAMPLE_DATABASE_NAME);
            assertTrue(status, "Sample database hasn't been created properly");
        } catch (RemoteException e) {
            throw new Exception("Error occurred while creating sample database", e);
        } catch (RSSAdminRSSManagerExceptionException e) {
            throw new Exception("Error occurred while creating sample database", e);
        }
    }

    @Test(dependsOnMethods = {"createDatabase"}, description = "Drop database")
    public void dropDatabase() throws Exception {
        try {
            boolean status = this.getRSSAdminStub().isDatabaseExist(ctx,
                    RSSTestConstants.SampleConfigurations.SAMPLE_DATABASE_NAME);
            assertTrue(status, "Database does not exist");

            this.getRSSAdminStub().dropDatabase(ctx,
                    RSSTestConstants.SampleConfigurations.SAMPLE_DATABASE_NAME);
            status = this.getRSSAdminStub().isDatabaseExist(ctx,
                    RSSTestConstants.SampleConfigurations.SAMPLE_DATABASE_NAME);
            assertFalse(status, "Database hasn't been deleted properly");
        } catch (RemoteException e) {
            throw new Exception("Error occurred while dropping sample database", e);
        } catch (RSSAdminRSSManagerExceptionException e) {
            throw new Exception("Error occurred while dropping sample database", e);
        }
    }

    @Test(description = "Get server list")
    public void getRSSInstanceList() throws Exception {
        try {
            RSSInstance[] instances = this.getRSSAdminStub().getRSSInstances(ctx);
            assertTrue(instances.length > 0, "No RSS Instances found.");
        } catch (RemoteException e) {
            throw new Exception("Error occurred while retrieving the list of RSS instances", e);
        } catch (RSSAdminRSSManagerExceptionException e) {
            throw new Exception("Error occurred while retrieving the list of RSS instances", e);
        }
    }

    @Test(dependsOnMethods = "createDatabase", description = "Get database list")
    public void getDatabasesList() throws Exception {
        try {
            assertTrue(this.getRSSAdminStub().getDatabases(ctx).length > 0, "No databases found");
            assertTrue(true);
        } catch (RemoteException e) {
            throw new Exception("Error occurred while retrieving the list of databases", e);
        } catch (RSSAdminRSSManagerExceptionException e) {
            throw new Exception("Error occurred while retrieving the list of databases", e);
        }
    }

    @Test(dependsOnMethods = "createDatabase", description = "Create database user")
    public void createDatabaseUser() throws Exception {
        try {
            boolean status = this.getRSSAdminStub().isDatabaseUserExist(ctx,
                    RSSTestConstants.SampleConfigurations.SAMPLE_DATABASE_USERNAME);
            assertFalse(status, "Sample database user already exists");
            DatabaseUser databaseUser = new DatabaseUser();
            databaseUser.setName(
                    RSSTestConstants.SampleConfigurations.SAMPLE_DATABASE_USERNAME);
            databaseUser.setPassword(
                    RSSTestConstants.SampleConfigurations.SAMPLE_DATABASE_USER_PASSWORD);
            databaseUser.setRssInstanceName(
                    RSSTestConstants.EnvironmentConfigurations.RSS_MANAGEMENT_MODE_SYSTEM);
            this.getRSSAdminStub().createDatabaseUser(ctx, databaseUser);

            status = this.getRSSAdminStub().isDatabaseUserExist(ctx,
                    RSSTestConstants.SampleConfigurations.SAMPLE_DATABASE_USERNAME);
            assertTrue(status, "Sample database user hasn't been created properly");
        } catch (RemoteException e) {
            throw new Exception("Error occurred while creating sample database user", e);
        } catch (RSSAdminRSSManagerExceptionException e) {
            throw new Exception("Error occurred while creating sample database user", e);
        }
    }

    @Test(description = "Create privilege template")
    public void createPrivilegeTemplate() throws Exception {
        try {
            boolean status =
                    this.getRSSAdminStub().isDatabasePrivilegesTemplateExist(ctx,
                            RSSTestConstants.SampleConfigurations.SAMPLE_PRIVILEGE_TEMPLATE_NAME);
            assertFalse(status, "Sample database privilege template already exists");

            DatabasePrivilegeTemplate template = RSSTestUtil.createSamplePrivilegeTemplate();
            this.getRSSAdminStub().createDatabasePrivilegesTemplate(ctx, template);

            status = this.getRSSAdminStub().isDatabasePrivilegesTemplateExist( ctx,
                            RSSTestConstants.SampleConfigurations.SAMPLE_PRIVILEGE_TEMPLATE_NAME);
            assertTrue(status, "Sample database privilege template hasn't been created properly");

            DatabasePrivilegeTemplate createdTemplate =
                    this.getRSSAdminStub().getDatabasePrivilegesTemplate(ctx,
                            RSSTestConstants.SampleConfigurations.SAMPLE_PRIVILEGE_TEMPLATE_NAME);

            this.checkPrivileges(template, createdTemplate);
        } catch (RemoteException e) {
            throw new Exception("Error occurred while creating sample database privilege " +
                    "template", e);
        } catch (RSSAdminRSSManagerExceptionException e) {
            throw new Exception("Error occurred while creating sample database privilege " +
                    "template", e);
        }
    }

    @Test(dependsOnMethods = {"createDatabase", "createDatabaseUser", "createPrivilegeTemplate"},
            description = "Attach database user")
    public void attachDatabaseUser() throws Exception {
        DatabaseUser[] before = this.getRSSAdminStub().getAvailableUsersToAttachToDatabase(ctx,
                RSSTestConstants.SampleConfigurations.SAMPLE_DATABASE_NAME);

        boolean status = this.isDatabaseUserAlreadyAttached(ctx,
                        RSSTestConstants.SampleConfigurations.SAMPLE_DATABASE_NAME,
                        RSSTestConstants.SampleConfigurations.SAMPLE_DATABASE_USERNAME);
        assertFalse(status, "Database user not available to attach to database");
        UserDatabaseEntry userDatabaseEntry = new UserDatabaseEntry();
        userDatabaseEntry.setDatabaseName(RSSTestConstants.SampleConfigurations.SAMPLE_DATABASE_NAME);
        userDatabaseEntry.setUsername(RSSTestConstants.SampleConfigurations.SAMPLE_DATABASE_USERNAME);
        userDatabaseEntry.setPrivileges(RSSTestUtil.createSamplePrivilegeTemplate().getPrivileges());
        this.getRSSAdminStub().attachUserToDatabase(ctx, userDatabaseEntry,
                RSSTestConstants.SampleConfigurations.SAMPLE_PRIVILEGE_TEMPLATE_NAME);
        DatabaseUser[] after = this.getRSSAdminStub().getAvailableUsersToAttachToDatabase(
                ctx, RSSTestConstants.SampleConfigurations.SAMPLE_DATABASE_NAME);
        if (after == null) {
            after = new DatabaseUser[0];
        }
        assertTrue((before.length - 1) == after.length, "Number of available users are " +
                "still the same even after attaching.");

        status = this.isDatabaseUserAlreadyAttached(ctx,
                        RSSTestConstants.SampleConfigurations.SAMPLE_DATABASE_NAME,
                        RSSTestConstants.SampleConfigurations.SAMPLE_DATABASE_USERNAME);
        assertFalse(status, "Database user still available for attach after attached to " +
                "the database");
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test(dependsOnMethods = {"attachDatabaseUser", "createDatabaseUser"},
            description = "Edit database user")
    public void editDatabaseUser() throws RSSAdminRSSManagerExceptionException, RemoteException {
        DatabaseUser user = new DatabaseUser();
        user.setName(RSSTestConstants.SampleConfigurations.SAMPLE_DATABASE_USERNAME);
        user.setPassword(RSSTestConstants.SampleConfigurations.SAMPLE_DATABASE_USER_PASSWORD);
        user.setRssInstanceName(
                RSSTestConstants.EnvironmentConfigurations.RSS_MANAGEMENT_MODE_SYSTEM);

        DatabasePrivilegeSet privileges = RSSTestUtil.createSamplePrivilegeSet();
        this.getRSSAdminStub().editDatabaseUserPrivileges(ctx, privileges, user,
                RSSTestConstants.SampleConfigurations.SAMPLE_DATABASE_NAME);
        privileges = this.getRSSAdminStub().getUserDatabasePermissions(ctx,
                        RSSTestConstants.SampleConfigurations.SAMPLE_DATABASE_NAME,
                        RSSTestConstants.SampleConfigurations.SAMPLE_DATABASE_USERNAME);

        DatabaseUser databaseUserMetaData =
                this.getRSSAdminStub().getDatabaseUser(
                        ctx,
                        RSSTestConstants.SampleConfigurations.SAMPLE_DATABASE_USERNAME);
        assertEquals(privileges.getSelectPriv(), "Y");
        assertEquals(privileges.getInsertPriv(), "Y");
        assertEquals(privileges.getIndexPriv(), "N");
        assertEquals(privileges.getAlterPriv(), "N");

    }

    @Test(dependsOnMethods = {"createDatabase", "createDatabaseUser"},
            description = "Drop database user")
    public void dropDatabaseUser() throws Exception {
        try {
            boolean status = this.getRSSAdminStub().isDatabaseUserExist(
                    ctx,
                    RSSTestConstants.SampleConfigurations.SAMPLE_DATABASE_USERNAME);
            assertTrue(status, "Database user already exists");

            this.getRSSAdminStub().dropDatabaseUser(
                    ctx,
                    RSSTestConstants.SampleConfigurations.SAMPLE_DATABASE_USERNAME);

            status = this.getRSSAdminStub().isDatabaseUserExist(
                    ctx,
                    RSSTestConstants.SampleConfigurations.SAMPLE_DATABASE_USERNAME);
            assertFalse(status, "Database hasn't been dropped properly");
            assertTrue(true);
        } catch (RemoteException e) {
            throw new Exception("Error occurred while dropping sample database user", e);
        } catch (RSSAdminRSSManagerExceptionException e) {
            throw new Exception("Error occurred while dropping sample database user", e);
        }
    }

    @Test(dependsOnMethods = {"createPrivilegeTemplate"}, description = "Edit privilege template")
    public void editPrivilegeTemplate() throws Exception {
        try {
            DatabasePrivilegeTemplate before =
                    this.getRSSAdminStub().getDatabasePrivilegesTemplate(ctx,
                            RSSTestConstants.SampleConfigurations.SAMPLE_PRIVILEGE_TEMPLATE_NAME);

            DatabasePrivilegeTemplate modified = RSSTestUtil.createSamplePrivilegeTemplate();
            modified.getPrivileges().setSelectPriv("N");
            modified.getPrivileges().setInsertPriv("N");

            this.getRSSAdminStub().editDatabasePrivilegesTemplate(ctx,modified);

            boolean status =
                        this.getRSSAdminStub().isDatabasePrivilegesTemplateExist(ctx,
                                RSSTestConstants.SampleConfigurations.SAMPLE_PRIVILEGE_TEMPLATE_NAME);
            assertTrue(status, "Modified sample database privilege template does not exist");

            DatabasePrivilegeTemplate after =
                    this.getRSSAdminStub().getDatabasePrivilegesTemplate(ctx,
                            RSSTestConstants.SampleConfigurations.SAMPLE_PRIVILEGE_TEMPLATE_NAME);
            assertEquals(after.getPrivileges().getSelectPriv(),
                    before.getPrivileges().getSelectPriv());
            assertEquals(after.getPrivileges().getInsertPriv(),
                    before.getPrivileges().getInsertPriv());
        } catch (RemoteException e) {
            throw new Exception("Error occurred while editing sample database privilege " +
                    "template", e);
        } catch (RSSAdminRSSManagerExceptionException e) {
            throw new Exception("Error occurred while editing sample database privilege " +
                    "template", e);

        }
    }

    @Test(dependsOnMethods = {"createPrivilegeTemplate"},
            description = "Drop privilege template")
    public void dropDatabasePrivilegeTemplate() throws Exception {
        try {
            this.getRSSAdminStub().dropDatabasePrivilegesTemplate(ctx,
                    RSSTestConstants.SampleConfigurations.SAMPLE_PRIVILEGE_TEMPLATE_NAME);
            assertTrue(true);
        } catch (RemoteException e) {
            throw new Exception("Error occurred while dropping sample database privilege " +
                    "template", e);
        } catch (RSSAdminRSSManagerExceptionException e) {
            throw new Exception("Error occurred while dropping sample database privilege " +
                    "template", e);
        }
    }

    @Test(dependsOnMethods = {"createDatabase", "createDatabaseUser", "createDataSource"},
            description = "Drop attach database user")
    public void dropAttachedDatabaseUser()
            throws RSSAdminRSSManagerExceptionException, RemoteException {
        DatabaseUser[] attachedDatabaseUsers = this.getRSSAdminStub().getUsersAttachedToDatabase(ctx,
                RSSTestConstants.SampleConfigurations.SAMPLE_DATABASE_NAME);
        int availableUserCount =
                this.getRSSAdminStub().getAvailableUsersToAttachToDatabase(
                        ctx,
                        RSSTestConstants.SampleConfigurations.SAMPLE_DATABASE_NAME).length;
        boolean contains = false;
        for (DatabaseUser temp : attachedDatabaseUsers) {
            if (RSSTestConstants.SampleConfigurations.SAMPLE_DATABASE_USERNAME.equals(temp)) {
                contains = true;
            }
        }
        assertTrue(contains, "Database user not attached to the database");

        UserDatabaseEntry userDatabaseEntry = new UserDatabaseEntry();
        userDatabaseEntry.setDatabaseName(RSSTestConstants.SampleConfigurations.SAMPLE_DATABASE_NAME);
        userDatabaseEntry.setUsername(RSSTestConstants.SampleConfigurations.SAMPLE_DATABASE_USERNAME);
        this.getRSSAdminStub().detachUserFromDatabase(ctx, userDatabaseEntry);
        assertTrue((availableUserCount + 1) ==
                this.getRSSAdminStub().getAvailableUsersToAttachToDatabase(
                        ctx,
                        RSSTestConstants.SampleConfigurations.SAMPLE_DATABASE_NAME).length);
        contains = false;
        for (DatabaseUser temp : this.getRSSAdminStub().getUsersAttachedToDatabase(ctx,
                RSSTestConstants.SampleConfigurations.SAMPLE_DATABASE_NAME)) {
            if (RSSTestConstants.SampleConfigurations.SAMPLE_DATABASE_USERNAME.equals(temp)) {
                contains = true;
            }
        }
        assertFalse(contains, "Database user still attached to the database");
        contains = false;
        for (DatabaseUser temp :
                this.getRSSAdminStub().getAvailableUsersToAttachToDatabase(ctx,
                        RSSTestConstants.SampleConfigurations.SAMPLE_DATABASE_NAME)) {
            if (RSSTestConstants.SampleConfigurations.SAMPLE_DATABASE_USERNAME.equals(temp)) {
                contains = true;
            }
        }
        assertTrue(contains, "Database user not available to attach after de-attached");
    }

    @Test(dependsOnMethods = {"createDatabase", "createDatabaseUser", "attachDatabaseUser"},
            description = "Create data source")
    public void createDataSource() throws Exception {
        try {
            UserDatabaseEntry entry = new UserDatabaseEntry();
            entry.setRssInstanceName(
                    RSSTestConstants.EnvironmentConfigurations.RSS_MANAGEMENT_MODE_SYSTEM);
            entry.setDatabaseName(RSSTestConstants.SampleConfigurations.SAMPLE_DATABASE_NAME);
            entry.setUsername(RSSTestConstants.SampleConfigurations.SAMPLE_DATABASE_USERNAME);
            this.getRSSAdminStub().createCarbonDataSource(ctx, entry);

            WSDataSourceInfo[] allDataSources = this.getNDataSourceAdminStub().getAllDataSources();
            boolean isCreated = false;
            for (WSDataSourceInfo wsDataSourceInfo : allDataSources) {
                if (entry.getDatabaseName().equals(wsDataSourceInfo.getDsMetaInfo().getName())) {
                    isCreated = true;
                }
            }
            assertTrue(isCreated, "Data source has not been created");
        } catch (RemoteException e) {
            throw new Exception("Error occurred while creating a sample datasource", e);
        } catch (RSSAdminRSSManagerExceptionException e) {
            throw new Exception("Error occurred while creating a sample datasource", e);
        } catch (NDataSourceAdminDataSourceException e) {
            throw new Exception("Error occurred while creating a sample datasource", e);
        }
    }
    /*
    @Test(dependsOnMethods = {"createDatabase", "createDatabaseUser"},
            description = "Create RSS instance")
    public void createRSSInstance() throws RSSAdminRSSManagerExceptionException, RemoteException {
        this.getRSSAdminStub().createRSSInstance(ctx, RSSTestUtil.createRSSInstance());

        RSSInstanceMetaData retrievedRSSInstance = this.getRSSAdminStub().getRSSInstance(ctx);
        assertTrue((retrievedRSSInstance != null &&
                RSSTestConstants.SampleConfigurations.SAMPLE_RSS_INSTANCE_NAME.equals(
                        retrievedRSSInstance.getName())),
                "Sample RSS instance hasn't been created");
    }

    @Test(dependsOnMethods = "createRSSInstance", description = "Edit rss instance")
    public void editRSSInstance() throws RSSAdminRSSManagerExceptionException, RemoteException {
        RSSInstance before = RSSTestUtil.createRSSInstance();
        this.getRSSAdminStub().editRSSInstance(ctx, RSSTestUtil.modifyRSSInstanceConfiguration(before));

        RSSInstanceMetaData after = this.getRSSAdminStub().getRSSInstance(ctx, before.getName());

        assertFalse(before.getServerURL().equals(after.getServerUrl()),
                "Sample RSS instance URL hasn't been edited");
        assertFalse(before.getInstanceType().equals(after.getInstanceType()),
                "Sample RSS instance type hasn't been edited");
        assertFalse(before.getServerCategory().equals(after.getServerCategory()),
                "Sample RSS instance server category hasn't been edited");
    }

    */

    @AfterClass(alwaysRun = true)
    public void cleanUp() throws Exception {
        boolean status =
                this.getRSSAdminStub().isDatabaseUserExist(
                        ctx,
                        RSSTestConstants.SampleConfigurations.SAMPLE_DATABASE_USERNAME);
        if (status) {
            this.getRSSAdminStub().dropDatabaseUser(
                    ctx,
                    RSSTestConstants.SampleConfigurations.SAMPLE_DATABASE_USERNAME);
        }

        status =
                this.getRSSAdminStub().isDatabaseExist(
                        ctx,
                        RSSTestConstants.SampleConfigurations.SAMPLE_DATABASE_NAME);
        if (status) {
            this.getRSSAdminStub().dropDatabase(
                    ctx,
                    RSSTestConstants.SampleConfigurations.SAMPLE_DATABASE_NAME);
        }

        status =
                this.getRSSAdminStub().isDatabasePrivilegesTemplateExist(ctx,
                        RSSTestConstants.SampleConfigurations.SAMPLE_PRIVILEGE_TEMPLATE_NAME);
        if (status) {
            this.getRSSAdminStub().dropDatabasePrivilegesTemplate(ctx,
                    RSSTestConstants.SampleConfigurations.SAMPLE_PRIVILEGE_TEMPLATE_NAME);
        }

        RSSInstance rssInstance =
                this.getRSSAdminStub().getRSSInstance(ctx);
        if (rssInstance != null) {
            this.getRSSAdminStub().dropRSSInstance(ctx,
                    RSSTestConstants.SampleConfigurations.SAMPLE_RSS_INSTANCE_NAME);
        }

        WSDataSourceInfo[] wsDataSourceInfos = nDataSourceAdminStub.getAllDataSources();
        if (wsDataSourceInfos != null) {
            for (WSDataSourceInfo wsDataSourceInfo : wsDataSourceInfos) {
                if (RSSTestConstants.SampleConfigurations.SAMPLE_DATABASE_NAME.equals(
                        wsDataSourceInfo.getDsMetaInfo().getName())) {
                    nDataSourceAdminStub.deleteDataSource(
                            RSSTestConstants.SampleConfigurations.SAMPLE_DATABASE_NAME);
                }
            }
        }

    }

    private void checkPrivileges(DatabasePrivilegeTemplate src, DatabasePrivilegeTemplate target) {
        DatabasePrivilegeSet srcPrivileges = src.getPrivileges();
        DatabasePrivilegeSet targetPrivileges = target.getPrivileges();
        assertEquals(targetPrivileges.getSelectPriv(), srcPrivileges.getSelectPriv());
        assertEquals(targetPrivileges.getInsertPriv(), srcPrivileges.getInsertPriv());
        assertEquals(targetPrivileges.getUpdatePriv(), srcPrivileges.getUpdatePriv());
        assertEquals(targetPrivileges.getDeletePriv(), srcPrivileges.getDeletePriv());
        assertEquals(targetPrivileges.getCreatePriv(), srcPrivileges.getCreatePriv());
        assertEquals(targetPrivileges.getDropPriv(), srcPrivileges.getDropPriv());
        assertEquals(targetPrivileges.getGrantPriv(), srcPrivileges.getGrantPriv());
        assertEquals(targetPrivileges.getReferencesPriv(), srcPrivileges.getReferencesPriv());
        assertEquals(targetPrivileges.getIndexPriv(), srcPrivileges.getIndexPriv());
        assertEquals(targetPrivileges.getAlterPriv(), srcPrivileges.getAlterPriv());
        assertEquals(targetPrivileges.getCreateTmpTablePriv(), srcPrivileges.getCreateTmpTablePriv());
        assertEquals(targetPrivileges.getLockTablesPriv(), srcPrivileges.getLockTablesPriv());
        assertEquals(targetPrivileges.getCreateViewPriv(), srcPrivileges.getCreateViewPriv());
        assertEquals(targetPrivileges.getShowViewPriv(), srcPrivileges.getShowViewPriv());
        assertEquals(targetPrivileges.getCreateRoutinePriv(), srcPrivileges.getCreateRoutinePriv());
        assertEquals(targetPrivileges.getExecutePriv(), srcPrivileges.getExecutePriv());
        assertEquals(targetPrivileges.getEventPriv(), srcPrivileges.getEventPriv());
        assertEquals(targetPrivileges.getTriggerPriv(), srcPrivileges.getTriggerPriv());
    }

    private boolean isDatabaseUserAlreadyAttached(RSSEnvironmentContext ctx, String databaseName,
                                                  String username) throws Exception {
        try {
            DatabaseUser[] availableUsers = this.getRSSAdminStub().getAvailableUsersToAttachToDatabase(ctx, databaseName);

            for (DatabaseUser user : availableUsers) {
                if (username.equals(user.getName())) {
                    return true;
                }
            }
            return false;
        } catch (RemoteException e) {
            throw new Exception("Error occurred while checking whether the sample user is " +
                    "already attached", e);
        } catch (RSSAdminRSSManagerExceptionException e) {
            throw new Exception("Error occurred while checking whether the sample user is " +
                    "already attached", e);
        }
    }

    private RSSAdminStub getRSSAdminStub() {
        return rssAdminStub;
    }

    private NDataSourceAdminStub getNDataSourceAdminStub() {
        return nDataSourceAdminStub;
    }

}
