package com.rookieintraining.services;

import com.google.common.io.Files;
import com.google.gson.GsonBuilder;
import com.rookieintraining.browser.BrowserManager;
import com.rookieintraining.object.Scenario;
import com.rookieintraining.object.Suite;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.testng.ISuite;
import org.testng.ITestContext;
import org.testng.ITestResult;

import java.io.*;
import java.sql.Date;
import java.time.Instant;
import java.util.*;

public class UIReportingService implements ITestNGService {

    private static ThreadLocal<Scenario> scenarioThreadLocal;
    private final List<Scenario> scenarioList = Collections.synchronizedList(new ArrayList<>());

    public static ThreadLocal<Scenario> getScenarioThreadLocal() {
        return scenarioThreadLocal;
    }

    public UIReportingService() {

    }

    public Scenario getCurrentScenario() {
        return scenarioThreadLocal.get();
    }

    @Override
    public void startLaunch() {
        scenarioThreadLocal = ThreadLocal.withInitial(() -> {
            Scenario scenario = new Scenario();
            scenario.suite = Suite.createOrGetInstance();
            scenarioList.add(scenario);
            return scenario;
        });
    }

    @Override
    public void finishLaunch() {
        generateReport();
    }

    @Override
    public void startSuite(ISuite iSuite) {

    }

    @Override
    public void finishSuite(ISuite iSuite) {
        scenarioList.forEach(scenario -> {
            try (Writer writer = new FileWriter(System.getProperty("report.path")
                    + "test--" + scenario.id + ".json")) {
                writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(scenario));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void startTest(ITestContext iTestContext) {

    }

    @Override
    public void finishTest(ITestContext iTestContext) {

    }

    @Override
    public void startTestMethod(ITestResult iTestResult) {
        String testName = iTestResult.getAttribute("name").toString();
        String testMethod = iTestResult.getMethod().getMethodName();
        scenarioThreadLocal.get().testName = testName;
        scenarioThreadLocal.get().testMethod = testMethod;
        scenarioThreadLocal.get().startTime = Date.from(Instant.now());
    }

    @Override
    public void finishTestMethod(String status, ITestResult iTestResult) {
        scenarioThreadLocal.get().result = status;
        scenarioThreadLocal.get().endTime = Date.from(Instant.now());
        if (Objects.nonNull(iTestResult.getThrowable())) {
            scenarioThreadLocal.get().errorMessage = Objects.nonNull(iTestResult.getThrowable().getMessage())
                    ? iTestResult.getThrowable().getMessage() : iTestResult.getThrowable().toString();
            scenarioThreadLocal.get().stackTrace = iTestResult.getThrowable().getStackTrace();
        }
        File ss = new File(System.getProperty("report.path")
                + "ss-" + iTestResult.getAttribute("thread") + ".png");
        File screenshot = BrowserManager.getDriver().getScreenshotAs(OutputType.FILE);

        try {
            Files.copy(screenshot,ss);
            scenarioThreadLocal.get().screenshotPaths.add(ss.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void startConfiguration(ITestResult iTestResult) {
        scenarioThreadLocal.get().scenarioLog.add("Started Test Configurations : " + iTestResult.getAttribute("name"));
    }

    @Override
    public void log(String testLog) {
        //scenarioThreadLocal.get().scenarioLog.add(testLog);
    }

    private void generateReport() {
        Collection<File> files = FileUtils.listFiles(new File(System.getProperty("report.path")), new String[]{"json"}, true);
        System.out.println("-----------------------------------------------------------------------------------");
        System.out.printf("|%-30s\t|%-10s|\t%-30s|\n", "TEST NAME", "RESULT", "START TIME");
        System.out.println("-----------------------------------------------------------------------------------");
        files.forEach(file -> {
            try {
                Scenario scn = new GsonBuilder().setPrettyPrinting().create().fromJson(new FileReader(file), Scenario.class);
                System.out.printf("|%-30s\t|%-10s|\t%-30s|\n", scn.testName, scn.result, scn.startTime);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
        System.out.println("-----------------------------------------------------------------------------------");
    }
}
