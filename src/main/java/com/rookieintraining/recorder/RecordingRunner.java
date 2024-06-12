package com.rookieintraining.recorder;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class RecordingRunner implements Runnable {

    private static final int MAX_DURATION = 900;

    private final ScreenRecorder recorder;
    private final FFMpegRecorder ffmpeg;
    final AtomicBoolean stop = new AtomicBoolean();
    final AtomicBoolean finished = new AtomicBoolean();

    public RecordingRunner(ScreenRecorder recorder, String outputFile) {
        this.recorder = recorder;
        this.ffmpeg = new FFMpegRecorder(recorder.sessionId, outputFile);
        new Thread(this, "session-" + recorder.sessionId + "-screen-recorder").start();
    }

    @Override
    public void run() {
        long lastId = 0;
        try {
            this.ffmpeg.start();
            long startTime = System.currentTimeMillis();

            while(System.currentTimeMillis() - startTime < MAX_DURATION * 1000 && !stop.get()) {
                final ImageFrame frameInstance = recorder.currentFrame;
                System.out.println("FRAME : " + frameInstance);
                if (frameInstance != null) {
                    final byte[] imageInstance = frameInstance.imageBytes;
                    if (imageInstance != null) {
                        lastId++;
                        ffmpeg.write(imageInstance);
                    } else {
                        System.out.println("No image bytes");
                    }
                } else {
                    System.out.println("No frame captured");
                }

                try {
                    Thread.sleep(1000/4);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        } finally {
            finished.set(true);
        }
    }

    public String finish() {
        stop.set(true);
        int startTime = (int) System.currentTimeMillis();
        while(!finished.get() && System.currentTimeMillis() - startTime < TimeUnit.SECONDS.toMillis(60)) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        if (!finished.get()) {
            System.out.println("Error while stopping the recording");
        }

        return ffmpeg.stop();
    }
}
