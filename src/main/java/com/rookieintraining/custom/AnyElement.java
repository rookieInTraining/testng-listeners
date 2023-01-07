package com.rookieintraining.custom;

import com.rookieintraining.listeners.ReportSampleListener;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebElement;

import java.util.List;

public class AnyElement {

    private final WebElement webElement;

    public AnyElement(WebElement webElement) {
        this.webElement = webElement;
    }

    public void click() {
        log("Clicking on the webElement : " + webElement.toString());
        webElement.click();
    }

    public void submit() {
        webElement.submit();
    }

    public void sendKeys(CharSequence... keysToSend) {
        log(String.format("Sending keys %s", (Object[]) keysToSend));
        webElement.sendKeys(keysToSend);
    }

    public void clear() {
        webElement.clear();
    }

    public String getTagName() {
        return webElement.getTagName();
    }

    public String getDomProperty(String name) {
        return webElement.getDomProperty(name);
    }

    public String getDomAttribute(String name) {
        return webElement.getDomAttribute(name);
    }

    public String getAttribute(String name) {
        return webElement.getAttribute(name);
    }

    public String getAriaRole() {
        return webElement.getAriaRole();
    }

    public String getAccessibleName() {
        return webElement.getAccessibleName();
    }

    public boolean isSelected() {
        return webElement.isSelected();
    }

    public boolean isEnabled() {
        return webElement.isEnabled();
    }

    public String getText() {
        return webElement.getText();
    }

    public List<WebElement> findElements(By by) {
        return webElement.findElements(by);
    }

    public WebElement findElement(By by) {
        return webElement.findElement(by);
    }

    public SearchContext getShadowRoot() {
        return webElement.getShadowRoot();
    }

    public boolean isDisplayed() {
        return webElement.isDisplayed();
    }

    public Point getLocation() {
        return webElement.getLocation();
    }

    public Dimension getSize() {
        return webElement.getSize();
    }

    public Rectangle getRect() {
        return webElement.getRect();
    }

    public String getCssValue(String propertyName) {
        return webElement.getCssValue(propertyName);
    }

    public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
        return webElement.getScreenshotAs(target);
    }

    private void log(String logText) {
        ReportSampleListener.iTestNgServices.forEach(s -> {
            s.log(logText);
        });
    }
}
