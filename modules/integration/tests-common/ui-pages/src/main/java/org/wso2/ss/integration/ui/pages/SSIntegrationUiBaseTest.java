package org.wso2.ss.integration.ui.pages;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.extensions.selenium.BrowserManager;
import org.wso2.carbon.integration.common.utils.LoginLogoutClient;
import org.wso2.ss.integration.ui.pages.login.LoginPage;

public class SSIntegrationUiBaseTest {
    private static final Log log = LogFactory.getLog(SSIntegrationUiBaseTest.class);
    protected AutomationContext ssServer;
    protected String sessionCookie;
    protected String backendURL;
    protected LoginLogoutClient loginLogoutClient;
    protected WebDriver driver;

    protected void init() throws Exception {
        ssServer = new AutomationContext("artifacts.SS", TestUserMode.SUPER_TENANT_ADMIN);
        loginLogoutClient = new LoginLogoutClient(ssServer);
        sessionCookie = loginLogoutClient.login();
        backendURL = ssServer.getContextUrls().getBackEndUrl();
        this.driver = BrowserManager.getWebDriver();
    }

    protected void init(TestUserMode testUserMode) throws Exception {
        ssServer = new AutomationContext("artifacts.SS", testUserMode);
        loginLogoutClient = new LoginLogoutClient(ssServer);
        sessionCookie = loginLogoutClient.login();
        backendURL = ssServer.getContextUrls().getBackEndUrl();
        this.driver = BrowserManager.getWebDriver();
    }

    protected String getLoginURL() throws Exception{
        return "https://localhost:9443/carbon/";
    }

    protected LoginPage logout() throws Exception {
        driver.findElement(By.xpath(UIElementMapper.getInstance().getElement("home.sign.out.xpath"))).click();
        return new LoginPage(driver);
    }
}
