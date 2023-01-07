package com.rookieintraining.services;

import com.rookieintraining.browser.BrowserManager;
import org.testng.ISuite;
import org.testng.ITestContext;
import org.testng.ITestResult;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ConsoleService implements ITestNGService {

    private static final Logger LOGGER = Logger.getLogger("ConsoleService");

    public ConsoleService() {

    }

    @Override
    public void startLaunch() {
        LOGGER.setLevel(Level.OFF);
        BrowserManager.init();
        LOGGER.info("********* Started Launch");
    }

    @Override
    public void finishLaunch() {
        BrowserManager.quitDriver();
        LOGGER.info("********* Finished Launch");
    }

    @Override
    public void startSuite(ISuite iSuite) {
        LOGGER.info("********* Started Suite");
    }

    @Override
    public void finishSuite(ISuite iSuite) {
        LOGGER.info("********* Finished Suite");
    }

    @Override
    public void startTest(ITestContext iTestContext) {
        LOGGER.info("********* Started Test");
    }

    @Override
    public void finishTest(ITestContext iTestContext) {
        LOGGER.info("********* Finished Test");
    }

    @Override
    public void startTestMethod(ITestResult iTestResult) {
        LOGGER.info("********* Started Test Method");
    }

    @Override
    public void finishTestMethod(String status, ITestResult iTestResult) {
        LOGGER.info("********* Finished Test Method : " + status);
    }

    @Override
    public void startConfiguration(ITestResult iTestResult) {
        LOGGER.info("********* Started Configuration");
    }

    @Override
    public void log(String testLog) {
        LOGGER.info("********* " + testLog);
    }

}
