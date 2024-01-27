package com.rookieintraining.test;

import com.github.javafaker.Faker;
import com.rookieintraining.browser.BrowserManager;
import com.rookieintraining.browser.BrowserThread;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.support.ui.Select;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class RetroSpecTest extends AbstractTest {

    @DataProvider(parallel = true)
    private Object[][] dp() {
        return new Object[][]{
                {"right"},
                {"wrong"},
                {"improvement"}
        };
    }

    @Test(testName = "Hello RetroSpec", dataProvider = "dp")
    public void testFour(String text) throws InterruptedException {
        BrowserThread driver = BrowserManager.getDriver();
        driver.get("http://localhost:4201/ui/dashboard/64bd067fd9aa6fb6db4f07dc");
        Thread.sleep(500);
        int count = 0;
        do {
            driver.findElement(By.cssSelector("textarea")).sendKeys(Faker.instance().address().fullAddress());
            Select dropdown = new Select(driver.findElement(By.cssSelector("#bucket")).getWebElement());
            dropdown.selectByValue(text);
            driver.findElement(By.cssSelector("button")).click();
        } while(++count < 1000);
    }

}
