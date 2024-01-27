package com.rookieintraining.listeners;

import com.rookieintraining.browser.BrowserManager;
import com.rookieintraining.services.ITestNGService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.*;
import org.testng.internal.IResultListener2;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class BaseTestNGListener implements IExecutionListener, ISuiteListener, IResultListener2, IInvokedMethodListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseTestNGListener.class);

    final String warning = "WARNING! More than one instance is running";
    private static final AtomicInteger INSTANCES = new AtomicInteger(0);
    private final List<ITestNGService> testNgServices;

    public BaseTestNGListener(List<ITestNGService> testNgService) {
        this.testNgServices = testNgService;
        if (INSTANCES.incrementAndGet() > 1) {
            LOGGER.warn(warning);
        }
    }

    @Override
    public void onExecutionStart() {
        BrowserManager.init();
        testNgServices.forEach(ITestNGService::startLaunch);
    }

    @Override
    public void onExecutionFinish() {
        BrowserManager.quitDriver();
        testNgServices.forEach(ITestNGService::finishLaunch);
    }

    @Override
    public void onStart(ISuite suite) {
        testNgServices.forEach((s) -> s.startSuite(suite));
    }

    @Override
    public void onFinish(ISuite suite) {
        testNgServices.forEach((s) -> s.finishSuite(suite));
    }

    @Override
    public void onStart(ITestContext context) {
        testNgServices.forEach((s) -> s.startTest(context));
    }

    @Override
    public void onFinish(ITestContext context) {
        testNgServices.forEach((s) -> s.finishTest(context));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        testNgServices.forEach((s) -> s.finishTestMethod("PASSED", result));
    }

    @Override
    public void onTestFailure(ITestResult result) {
        testNgServices.forEach((s) -> {
            s.finishTestMethod("FAILED", result);
        });
    }

    @Override
    public void onTestStart(ITestResult result) {
        testNgServices.forEach((s) -> s.startTestMethod(result));
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        testNgServices.forEach((s) -> s.finishTestMethod("SKIPPED", result));
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        testNgServices.forEach((s) -> s.finishTestMethod("FAILED", result));
    }

    @Override
    public void onTestFailedWithTimeout(ITestResult result) {
        testNgServices.forEach((s) -> s.finishTestMethod("FAILED", result));
    }

    @Override
    public void onConfigurationSkip(ITestResult tr) {
        testNgServices.forEach((s) -> {
            s.startConfiguration(tr);
            s.finishTestMethod("SKIPPED", tr);
        });
    }

}
