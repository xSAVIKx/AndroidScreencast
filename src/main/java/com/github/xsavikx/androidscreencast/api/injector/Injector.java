package com.github.xsavikx.androidscreencast.api.injector;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class Injector {
    private static final Logger LOGGER = Logger.getLogger(Injector.class);
    private final ScreenCaptureRunnable screenCaptureRunnable;
    private final Thread screenCaptureThread;

    @Autowired
    public Injector(ScreenCaptureRunnable screenCaptureRunnable) {
        this.screenCaptureRunnable = screenCaptureRunnable;
        this.screenCaptureThread = new Thread(screenCaptureRunnable, "Screen Capturer");
    }

    public void stop() {
        screenCaptureRunnable.stop();
    }

    public void start() {
        screenCaptureThread.start();
    }

    public void setScreenCaptureListener(ScreenCaptureRunnable.ScreenCaptureListener listener) {
        this.screenCaptureRunnable.setListener(listener);
    }

    public void startRecording(File file) {
        screenCaptureRunnable.startRecording(file);
    }

    public void stopRecording() {
        screenCaptureRunnable.stopRecording();
    }

    public void toggleOrientation() {
        screenCaptureRunnable.toggleOrientation();
    }

}
