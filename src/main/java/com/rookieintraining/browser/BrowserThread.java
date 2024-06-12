package com.rookieintraining.browser;

import com.rookieintraining.aspects.Step;
import com.rookieintraining.custom.AnyElement;
import com.rookieintraining.listeners.ReportSampleListener;
import com.rookieintraining.recorder.ScreenRecorder;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BrowserThread {

    private static final Logger LOGGER = Logger.getLogger("BrowserThread");
    private WebDriver webDriver;
    private ScreenRecorder screenRecorder;

    protected WebDriver getDriver() {
        if (Objects.isNull(webDriver)) {
            WebDriverManager.chromedriver().setup();
            FirefoxOptions options = new FirefoxOptions();
//            options.setCapability("webSocketUrl", true);
//            options.addArguments("--remote-allow-origins=*");
            try {
                webDriver = new RemoteWebDriver(new URL("http://192.168.1.36:4444/"), options);
                screenRecorder = new ScreenRecorder(webDriver);
                screenRecorder.startRecording();
            } catch (SessionNotCreatedException e) {
                throw new RuntimeException(e);
                //webDriver = new EdgeDriver(options);
            } catch (MalformedURLException me) {
                throw new RuntimeException();
            }

            webDriver.manage().window().maximize();
        }

        return webDriver;
    }

    public WebDriver getWebDriver() {
        return webDriver;
    }

    private void setDriver(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    @Step("Given I go to the url {0}")
    public void get(String url) {
        log(String.format("Accessing the Url : %s", url));
        getDriver().get(url);
    }

    @Step("And I find the element by {0}")
    public AnyElement findElement(By byContext) {
        log(String.format("Finding an element with By context : %s", byContext));
        return new AnyElement(getDriver().findElement(byContext));
    }

    @Step("And I find the elements by {0}")
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
        String outputFile = screenRecorder.stopRecording(true);
        File tmpRecording = new File(outputFile);
        File finalRecording = new File(System.getProperty("user.dir") + "/build/recordings/" + UUID.randomUUID() + ".webm");

        try {
            FileUtils.copyFile(tmpRecording, finalRecording);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
