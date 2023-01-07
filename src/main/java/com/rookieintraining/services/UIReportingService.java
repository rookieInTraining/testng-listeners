package com.rookieintraining.services;

import com.google.common.io.Files;
import com.google.gson.GsonBuilder;
import com.rookieintraining.browser.BrowserManager;
import com.rookieintraining.object.Scenario;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.testng.ISuite;
import org.testng.ITestContext;
import org.testng.ITestResult;

import java.io.*;
import java.sql.Date;
import java.time.Instant;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UIReportingService implements ITestNGService {

    private static final Logger LOGGER = Logger.getLogger("UIReportingService");
    private ThreadLocal<Scenario> scenarioThreadLocal;
    private final List<Scenario> scenarioList = Collections.synchronizedList(new ArrayList<>());

    public UIReportingService() {

    }

    public Scenario getCurrentScenario() {
        return scenarioThreadLocal.get();
    }

    @Override
    public void startLaunch() {
        LOGGER.setLevel(Level.OFF);
        BrowserManager.init();
        LOGGER.info("Started Launch");
    }

    @Override
    public void finishLaunch() {
        BrowserManager.quitDriver();
        LOGGER.info("Finished Launch");
    }

    @Override
    public void startSuite(ISuite iSuite) {
        LOGGER.info("Started Suite");
    }

    @Override
    public void finishSuite(ISuite iSuite) {
        generateReport();
        LOGGER.info("Finished Suite");
    }

    @Override
    public void startTest(ITestContext iTestContext) {
        scenarioThreadLocal = ThreadLocal.withInitial(() -> {
            Scenario scenario = new Scenario();
            scenarioList.add(scenario);
            scenario.startTime = Date.from(Instant.now());
            return scenario;
        });
        LOGGER.info("Started Test");
    }

    @Override
    public void finishTest(ITestContext iTestContext) {
        LOGGER.info("Finished Test");
    }

    @Override
    public void startTestMethod(ITestResult iTestResult) {
        String testName = iTestResult.getAttribute("name").toString();
        String testMethod = iTestResult.getMethod().getMethodName();
        scenarioThreadLocal.get().testName = testName;
        scenarioThreadLocal.get().testMethod = testMethod;
        LOGGER.info("Started Test Method" + iTestResult.getMethod());
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

        try (Writer writer = new FileWriter(System.getProperty("report.path")
                + "test--" + iTestResult.getAttribute("thread") + ".json");) {
            Files.copy(screenshot,ss);
            scenarioThreadLocal.get().screenshotPaths.add(ss.getAbsolutePath());
            writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(scenarioThreadLocal.get()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        LOGGER.info("Finished Test Method : " + status);
    }

    @Override
    public void startConfiguration(ITestResult iTestResult) {
        LOGGER.info("Started Configuration");
    }

    @Override
    public void log(String testLog) {
        scenarioThreadLocal.get().scenarioLog.add(testLog);
        LOGGER.info(testLog);
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
