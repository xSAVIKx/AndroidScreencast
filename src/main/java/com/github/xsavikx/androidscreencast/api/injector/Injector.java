package com.github.xsavikx.androidscreencast.api.injector;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;

@Singleton
public class Injector {
    private final ScreenCaptureRunnable screenCaptureRunnable;
    private final Thread screenCaptureThread;

    @Inject
    public Injector(final ScreenCaptureRunnable screenCaptureRunnable) {
        this.screenCaptureRunnable = screenCaptureRunnable;
        this.screenCaptureThread = new Thread(screenCaptureRunnable, "Screen Capturer");
        this.screenCaptureThread.setDaemon(true);
    }

    public void stop() {
        screenCaptureRunnable.stop();
    }

    public void start() {
        screenCaptureThread.start();
    }

    public void setScreenCaptureListener(final ScreenCaptureRunnable.ScreenCaptureListener listener) {
        this.screenCaptureRunnable.setListener(listener);
    }

    public void startRecording(final File file) {
        screenCaptureRunnable.startRecording(file);
    }

    public void stopRecording() {
        screenCaptureRunnable.stopRecording();
    }

    public void toggleOrientation() {
        screenCaptureRunnable.toggleOrientation();
    }

}
