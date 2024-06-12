package com.rookieintraining.recorder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicReference;

public class FFMpegRecorder {

    private final ProcessBuilder processBuilder;
    private final String outputFile;
    private final AtomicReference<Process> ffmpegProcess = new AtomicReference<>();
    private final AtomicReference<OutputStream> ffmpegStream = new AtomicReference<>();
    private final AtomicReference<Thread> outputThread = new AtomicReference<>();
    private final AtomicReference<Thread> errorThread = new AtomicReference<>();
    private final String sessionId;

    public FFMpegRecorder(String sessionId, String outputFile) {
        this.sessionId = sessionId;
        this.outputFile = outputFile;

        String[] ffmpegCommand = {
                getCommand(),
                "-loglevel", "error",
                "-f", "image2pipe",
                "-avioflags", "direct",
                "-fpsprobesize", "0",
                "-probesize", "32",
                "-analyzeduration", "0",
                "-c:v", "mjpeg",
                "-r", "4",
                "-i", "-",
                "-y", "-an",
                "-c:v", "vp8",
                "-qmin", "10",
                "-qmax", "60",
                "-crf", "25",
                "-deadline", "realtime",
                "-speed", "8",
                "-b:v", "500K",
                "-threads", "1",
                outputFile
        };

        processBuilder = new ProcessBuilder(ffmpegCommand);
    }

    private String getCommand() {
        String currentDir = System.getProperty("user.dir");
        String osName = System.getProperty("os.name").toLowerCase();
        String osArch = System.getProperty("os.arch");
        String ffmpegPath = currentDir + "/framework/build/resources/main/ffmpeg";
        boolean bundledFFmpeg = true;

        if(osName.contains("linux")) {
            switch (osArch) {
                case "amd64":
                    ffmpegPath += "ffmpeg-6-1-amd64";
                    break;
                case "aarch64":
                    ffmpegPath += "ffmpeg-6-1-arm64";
                    break;
                default:
                    ffmpegPath = "ffmpeg";
                    bundledFFmpeg = false;
            }
        } else if (osName.contains("mac")){
                    ffmpegPath += "ffmpeg-6-1-osx";
        } else {
            ffmpegPath = "ffmpeg.exe";
            bundledFFmpeg = false;
        }

        if (bundledFFmpeg) {
            final File ffmpegExec = new File(ffmpegPath);
            if (!ffmpegExec.exists()) {
                ffmpegPath = "ffmpeg";
            } else {
                ffmpegExec.setExecutable(true, false);
            }
        }

        return ffmpegPath;
    }

    public void start() {
        try {
            final Process ffmpegProcess = processBuilder.start();
            this.ffmpegProcess.set(ffmpegProcess);

            final OutputStream outputStream = ffmpegProcess.getOutputStream();
            this.ffmpegStream.set(outputStream);

            InputStream ffmpegOutput = ffmpegProcess.getInputStream();
            InputStream ffmpegError = ffmpegProcess.getErrorStream();

            Thread outputThread = new Thread(() -> {
               try {
                   byte[] buffer = new byte[1024];
                   int byteRead;
                   while((byteRead = ffmpegOutput.read(buffer)) != -1) {
                       System.out.println(new String(buffer, 0, byteRead));
                   }
               } catch (IOException ioe) {
                   ioe.printStackTrace();
               }
            }, "session-" + sessionId + "-ffmpeg-output");
            this.outputThread.set(outputThread);

            Thread errThread = new Thread(() -> {
                try {
                    byte[] buffer = new byte[1024];
                    int byteRead;
                    while((byteRead = ffmpegError.read(buffer)) != -1) {
                        System.err.println(new String(buffer, 0, byteRead));
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }, "session-" + sessionId + "-ffmpeg-output");
            this.errorThread.set(errThread);

            outputThread.start();
            errThread.start();
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    public void write(byte[] imageBytes) {
        try {
            ffmpegStream.get().write(imageBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String stop() {
        try {
            ffmpegStream.get().close();
            ffmpegProcess.get().waitFor();
            outputThread.get().join();
            errorThread.get().join();

            return outputFile;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            new File(outputFile).delete();
            return null;
        }
    }

}
