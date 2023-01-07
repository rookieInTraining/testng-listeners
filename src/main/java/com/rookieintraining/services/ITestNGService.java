package com.rookieintraining.services;

import org.testng.ISuite;
import org.testng.ITestContext;
import org.testng.ITestResult;

public interface ITestNGService {

    void startLaunch();

    void finishLaunch();

    void startSuite(ISuite iSuite);

    void finishSuite(ISuite iSuite);

    void startTest(ITestContext iTestContext);

    void finishTest(ITestContext iTestContext);

    void startTestMethod(ITestResult iTestResult);

    void finishTestMethod(String status, ITestResult iTestResult);

    void startConfiguration(ITestResult iTestResult);

    void log(String testLog);

}