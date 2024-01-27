package com.rookieintraining.test;

import com.rookieintraining.browser.BrowserManager;
import com.rookieintraining.browser.BrowserThread;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.FFmpegLogCallback;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.devtools.v119.page.Page;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Base64;
import java.util.Optional;

public class TakeScreenshot extends AbstractTest {

    @Test(testName = "Convert a browser screencast to video")
    public void takeScreenshot() throws FFmpegFrameRecorder.Exception {
        BrowserThread browser = BrowserManager.getDriver();

        // Initialize FFmpeg
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder("C:\\output.mp4", 1920, 1080);
        FFmpegLogCallback.set();
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
        recorder.setFrameRate(60.0);

        // Start recording
        recorder.start();

        DevTools devTools = ((HasDevTools) browser.getWebDriver()).getDevTools();
        devTools.createSessionIfThereIsNotOne();

        devTools.addListener(Page.screencastFrame(), (response -> {
            String data = response.getData();
            try {
                BufferedImage image = ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(data)));
                recorder.record(new Java2DFrameConverter().convert(image));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));

        devTools.send(Page.startScreencast(Optional.empty(),Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(1)));

        browser.get("https://space.sprinklr.com");
        Wait<WebDriver> waits = new WebDriverWait(BrowserManager.getDriver().getWebDriver(), Duration.ofSeconds(30));
        waits.until(ExpectedConditions.elementToBeClickable(By.name("uid"))).sendKeys("ish.abbi@sprinklr.com");

        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        devTools.send(Page.stopScreencast());

        // Stop recording and release resources
        recorder.stop();
        recorder.release();
    }

    @Test(testName = "Print page to PDF using the chrome CDP API")
    public void castVideo() throws IOException {
        BrowserThread browser = BrowserManager.getDriver();

        DevTools devTools = ((HasDevTools) browser.getWebDriver()).getDevTools();
        devTools.createSessionIfThereIsNotOne();

        devTools.send(Page.enable());

        browser.get("https://www.sprinklr.com");

        Page.PrintToPDFResponse response = devTools.send(
                Page.printToPDF(Optional.of(true),
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty(), Optional.empty())
        );

        Files.write(Paths.get("C:\\file-duckduckgo.pdf"), Base64.getDecoder().decode(response.getData()));
    }

}
