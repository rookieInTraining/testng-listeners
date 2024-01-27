package com.rookieintraining.browser;

import com.rookieintraining.aspects.Step;
import com.rookieintraining.custom.AnyElement;
import com.rookieintraining.listeners.ReportSampleListener;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BrowserThread {

    private static final Logger LOGGER = Logger.getLogger("BrowserThread");
    private WebDriver webDriver;

    protected WebDriver getDriver() {
        if (Objects.isNull(webDriver)) {
            WebDriverManager.edgedriver().setup();
            EdgeOptions options = new EdgeOptions();
            options.setCapability("webSocketUrl", true);
            options.addArguments("--remote-allow-origins=*");
            try {
                webDriver = new RemoteWebDriver(new URL("http://35.200.214.0:4444/"), options);
            } catch (SessionNotCreatedException e) {
                webDriver = new EdgeDriver(options);
            } catch (MalformedURLException me) {
                throw new RuntimeException();
            }
//            webDriver.manage().window().maximize();

        }

        return new Augmenter().augment(webDriver);
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
