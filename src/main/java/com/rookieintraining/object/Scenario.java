package com.rookieintraining.object;

import java.util.*;
import java.util.logging.Logger;

public class Scenario {

    private static final Logger LOGGER = Logger.getAnonymousLogger();

    public String testName;
    public String testMethod;
    public String result;
    public String errorMessage;
    public StackTraceElement[] stackTrace;
    public List<String> scenarioLog = new ArrayList<>();
    public List<String> screenshotPaths = new ArrayList<>();
    public Date startTime;
    public Date endTime;

    public Scenario() {
        LOGGER.info("Initializing scenario with thread Id : " + Thread.currentThread().getId());
    }

    public Scenario(String result) {
        this.result = result;
    }

    public Scenario(String result, String errorMessage, StackTraceElement[] stackTrace) {
        this.result = result;
        this.errorMessage = errorMessage;
        this.stackTrace = stackTrace;
    }

    @Override
    public String toString() {
        return "Scenario{" +
                "testName='" + testName + '\'' +
                ", testMethod='" + testMethod + '\'' +
                ", result='" + result + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", stackTrace=" + Arrays.toString(stackTrace) +
                ", scenarioLog=" + scenarioLog +
                ", screenshotPaths=" + screenshotPaths +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
