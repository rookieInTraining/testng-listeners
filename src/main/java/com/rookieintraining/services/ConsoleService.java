package com.rookieintraining.services;

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
        LOGGER.setLevel(Level.WARNING);
        LOGGER.log(Level.FINEST, "********* Started Launch");
    }

    @Override
    public void finishLaunch() {
        LOGGER.log(Level.FINEST, "********* Finished Launch");
    }

    @Override
    public void startSuite(ISuite iSuite) {
        LOGGER.log(Level.FINEST, "********* Started Suite");
    }

    @Override
    public void finishSuite(ISuite iSuite) {
        LOGGER.log(Level.FINEST, "********* Finished Suite");
    }

    @Override
    public void startTest(ITestContext iTestContext) {
        LOGGER.log(Level.FINEST, "********* Started Test");
    }

    @Override
    public void finishTest(ITestContext iTestContext) {
        LOGGER.log(Level.FINEST, "********* Finished Test");
    }

    @Override
    public void startTestMethod(ITestResult iTestResult) {
        LOGGER.log(Level.FINEST, "********* Started Test Method");
    }

    @Override
    public void finishTestMethod(String status, ITestResult iTestResult) {
        LOGGER.log(Level.FINEST, "********* Finished Test Method : " + status);
    }

    @Override
    public void startConfiguration(ITestResult iTestResult) {
        LOGGER.log(Level.FINEST, "********* Started Configuration");
    }

    @Override
    public void log(String testLog) {
        LOGGER.log(Level.FINEST, "********* " + testLog);
    }

}
