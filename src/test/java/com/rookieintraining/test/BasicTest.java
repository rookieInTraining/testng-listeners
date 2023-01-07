package com.rookieintraining.test;

import com.rookieintraining.browser.BrowserManager;
import com.rookieintraining.browser.BrowserThread;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class BasicTest extends AbstractTest {

    @DataProvider(parallel = true)
    private Object[][] dp() {
        return new Object[][]{
                {"One"},
                {"Two"},
                {"Three"}
        };
    }

    @Test(testName = "Hello World - DP", dataProvider = "dp")
    public void testFour(String text) throws InterruptedException {
        BrowserThread driver = BrowserManager.getDriver();
        driver.get("https://www.google.com");
        Thread.sleep(5000);
        driver.findElement(By.cssSelector("input")).sendKeys(text, Keys.ENTER);
        Thread.sleep(5000);
    }

    @Test(testName = "Hello World")
    public void testOne() throws InterruptedException {
        BrowserThread driver = BrowserManager.getDriver();
        driver.get("https://maps.google.com");
        Thread.sleep(5000);
        driver.get("https://duckduckgo.com");
        Thread.sleep(5000);
        driver.get("https://google.com");
    }

    @Test(testName = "Hello World - Failed")
    public void testFailed() {
        BrowserManager.getDriver().get("https://www.airasia.com");
        Assert.assertEquals( 1, 1, "Test failed");
    }

    @Test(testName = "Hello World - Skipped")
    public void testSkipped() {
        BrowserManager.getDriver().get("https://www.sprinklr.com");
        System.out.println("Test Skipped");
    }
}
