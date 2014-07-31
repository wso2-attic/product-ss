/*
 *  Copyright (c) 2005-2011, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package org.wso2.carbon.ss;

import org.wso2.carbon.rssmanager.core.dto.xsd.*;
import org.wso2.carbon.ss.integration.test.rssmanager.RSSTestConstants;
import org.wso2.carbon.utils.ServerConstants;

import java.io.File;

public class RSSTestUtil {

    public static String getRSSSrcConfigPath() {
        String carbonHome = System.getProperty(ServerConstants.CARBON_HOME);
        return carbonHome + File.separator +
                RSSTestConstants.EnvironmentConfigurations.RSS_CONFIG_PATH;
    }

    public static String getRSSDestConfigPath() {
        return "";
    }

    public static DatabasePrivilegeSetInfo createSamplePrivilegeSet() {
        DatabasePrivilegeSetInfo privileges = new MySQLPrivilegeSetInfo();
        privileges.setSelectPriv("Y");
        privileges.setInsertPriv("Y");
        privileges.setUpdatePriv("Y");
        privileges.setDeletePriv("Y");
        privileges.setCreatePriv("Y");
        privileges.setDropPriv("Y");
        privileges.setIndexPriv("Y");
        privileges.setAlterPriv("Y");

        return privileges;
    }

    public static DatabasePrivilegeTemplateInfo createSamplePrivilegeTemplate() {
        DatabasePrivilegeSetInfo privileges = createSamplePrivilegeSet();
        DatabasePrivilegeTemplateInfo template = new DatabasePrivilegeTemplateInfo();
        template.setName(RSSTestConstants.SampleConfigurations.SAMPLE_PRIVILEGE_TEMPLATE_NAME);
        template.setPrivileges(privileges);
        return template;
    }
    /*
    public static RSSInstance createRSSInstance() {
        RSSInstance rssInstance = new RSSInstance();
        rssInstance.setName(RSSTestConstants.SampleConfigurations.SAMPLE_RSS_INSTANCE_NAME);
        rssInstance.setServerURL(RSSTestConstants.SampleConfigurations.SAMPLE_RSS_INSTANCE_URL);
        rssInstance.setAdminUsername(
                RSSTestConstants.SampleConfigurations.SAMPLE_RSS_INSTANCE_ADMIN_USERNAME);
        rssInstance.setAdminPassword(
                RSSTestConstants.SampleConfigurations.SAMPLE_RSS_INSTANCE_ADMIN_PASSWORD);
        rssInstance.setDbmsType(RSSTestConstants.EnvironmentConfigurations.DATABASE_TYPE_MYSQL);
        rssInstance.setInstanceType(
                RSSTestConstants.EnvironmentConfigurations.RSS_MANAGEMENT_MODE_SYSTEM);
        rssInstance.setServerCategory(
                RSSTestConstants.EnvironmentConfigurations.RSS_INSTANCE_SERVER_CATEGORY_LOCAL);
        return rssInstance;
    }

    public static RSSInstance modifyRSSInstanceConfiguration(RSSInstance rssInstance) {
        rssInstance.setServerURL(
                RSSTestConstants.SampleConfigurations.SAMPLE_RSS_INSTANCE_MODIFIED_URL);
        rssInstance.setAdminUsername(
                RSSTestConstants.SampleConfigurations.SAMPLE_RSS_INSTANCE_ADMIN_MODIFIED_USERNAME);
        rssInstance.setAdminPassword(
                RSSTestConstants.SampleConfigurations.SAMPLE_RSS_INSTANCE_ADMIN_MODIFIED_USERNAME);
        rssInstance.setDbmsType(RSSTestConstants.EnvironmentConfigurations.DATABASE_TYPE_MYSQL);
        rssInstance.setInstanceType(
                RSSTestConstants.EnvironmentConfigurations.RSS_INSTANCE_SERVER_CATEGORY_RDS);
        rssInstance.setServerCategory(
                RSSTestConstants.EnvironmentConfigurations.RSS_INSTANCE_SERVER_CATEGORY_LOCAL);
        return rssInstance;
    }
     */

}
