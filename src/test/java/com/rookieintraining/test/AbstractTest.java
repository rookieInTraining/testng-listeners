package com.rookieintraining.test;

import com.google.common.io.Files;
import com.rookieintraining.browser.BrowserManager;
import org.openqa.selenium.OutputType;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestNG;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.logging.Logger;

public class AbstractTest extends TestNG {

    private static final Logger LOGGER = Logger.getLogger("AbstractTest");

    @BeforeMethod(alwaysRun = true)
    public void init(Method m, ITestContext context, ITestResult result) {
        Test test = m.getAnnotation(Test.class);
        System.out.println("RUNNING - ABSTRACT TEST SUITE CODE - BEFORE - " + Thread.currentThread().getId() + " : " + test.testName());
        result.setAttribute("name", test.testName());
        result.setAttribute("thread", Thread.currentThread().getId() + "_" + UUID.randomUUID());
        context.setAttribute("thread", Thread.currentThread().getId() + "_" + UUID.randomUUID());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestContext context, ITestResult result) {
        LOGGER.info("RUNNING - ABSTRACT TEST SUITE CODE - AFTER - " + Thread.currentThread().getId());
        LOGGER.info("RUNNING - CONTEXT - AFTER - " + result.getAttribute("name"));
    }

}
