package com.rookieintraining.browser;

import com.rookieintraining.custom.AnyElement;
import com.rookieintraining.listeners.ReportSampleListener;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BrowserThread {

    private static final Logger LOGGER = Logger.getLogger("BrowserThread");
    private WebDriver webDriver;

    private WebDriver getDriver() {
        if (Objects.isNull(webDriver)) {
            WebDriverManager.edgedriver().setup();
            webDriver = new EdgeDriver();
        }
        return webDriver;
    }

    private void setDriver(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    public void get(String url) {
        log(String.format("Accessing the Url : %s", url));
        getDriver().get(url);
    }

    public AnyElement findElement(By byContext) {
        log(String.format("Finding an element with By context : %s", byContext));
        return new AnyElement(getDriver().findElement(byContext));
    }

    public List<AnyElement> findElements(By byContext) {
        List<AnyElement> anyElements = new ArrayList<>();
        log(String.format("Finding an element with By context : %s", byContext));
        getDriver().findElements(byContext).forEach(webElement -> anyElements.add(new AnyElement(webElement)));
        return anyElements;
    }

    public <T> T getScreenshotAs(OutputType<T> outputType) {
        log(String.format("Capturing screenshot as %s", outputType));
        return ((TakesScreenshot) getDriver()).getScreenshotAs(outputType);
    }

    public Map<String, Integer> getLocation() {
        return Map.of("x", 1, "y", 2);
    }

    public void quit() {
        if (Objects.nonNull(this.webDriver)) {
            this.webDriver.quit();
            this.webDriver = null;
        }
    }

    private void log(String logText) {
        LOGGER.log(Level.INFO, logText);
        ReportSampleListener.iTestNgServices.forEach(s -> {
            s.log(logText);
        });
    }
}
