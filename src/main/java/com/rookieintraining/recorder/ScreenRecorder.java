package com.rookieintraining.recorder;

import com.shinyhut.vernacular.client.VernacularClient;
import com.shinyhut.vernacular.client.VernacularConfig;
import com.shinyhut.vernacular.client.rendering.ColorDepth;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScreenRecorder {

    private static final Pattern VNC_PATTERN = Pattern.compile("ws://([^:]+):(\\d+)");
    private static final boolean DEBUG = true;

    private RecordingRunner recorder;
    private VernacularClient client;
    ImageFrame currentFrame;
    String host;
    int port;
    String sessionId;

    public ScreenRecorder(WebDriver driver) {
        if (driver instanceof RemoteWebDriver) {
            this.sessionId = ((RemoteWebDriver) driver).getSessionId().toString();
            final Capabilities capabilities = ((RemoteWebDriver) driver).getCapabilities();
            if (Objects.isNull(capabilities)) {
                throw new RuntimeException("[ScreenRecorder] : Capabilities not found");
            }
            final Object vncAddress = capabilities.getCapability("se:vncLocalAddress");
            if (!(vncAddress instanceof String)) {
                throw new RuntimeException("[ScreenRecorder] : vnc address not found in capabilities" + vncAddress);
            }
            System.out.println("--------------------VNC---------- : " + vncAddress);
            Matcher m = VNC_PATTERN.matcher((String) vncAddress);
            if (m.find()) {
                this.host = "192.168.1.36";
                this.port = 5900;

                System.out.println("HOST : " + host + " PORT : " + port);
            } else {
                throw new RuntimeException("[ScreenRecorder] : Could not find VNC address in " + vncAddress);
            }
        } else {
            throw new RuntimeException("[ScreenRecorder] : Only supported in RemoteWebDriver");
        }
    }

    public void startRecording() {
        VernacularConfig config = new VernacularConfig();
        client = new VernacularClient(config);

        config.setColorDepth(ColorDepth.BPP_24_TRUE);
        config.setErrorListener(Throwable::printStackTrace);
        config.setPasswordSupplier(() -> "secret");
        config.setScreenUpdateListener(image -> {
            System.out.println("Obtained a new frame!!!");

            ImageFrame newFrame = new ImageFrame(image);
            if (Objects.nonNull(newFrame.imageBytes)) {
                this.currentFrame = newFrame;
            }
        });

        File tmpFile;
        try {
            tmpFile = File.createTempFile("recording", ".webm");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String path = tmpFile.getAbsolutePath();
        tmpFile.deleteOnExit();
        tmpFile.delete();

        recorder = new RecordingRunner(this, path);

        try {
            Socket socket = new Socket(host, port);
            client.start(socket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        new Thread(() -> {
//            try {
//                Socket socket = new Socket(host, port);
//                client.start(socket);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }).start();
    }

    public String stopRecording(boolean save) {
        String output = null;
        if (recorder != null) {
            output = recorder.finish();
        }

        if (client != null) {
            client.stop();
        }

        if (output == null || save) {
            System.out.println("~~~~~~~~~~~~~~~~~ OUTPUT FILE : " + output);
            return output;
        }

        new File(output).delete();
        return null;
    }

}
