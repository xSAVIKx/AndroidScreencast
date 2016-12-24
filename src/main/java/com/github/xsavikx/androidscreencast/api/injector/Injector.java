package com.github.xsavikx.androidscreencast.api.injector;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Injector {
    private static final Logger LOGGER = Logger.getLogger(Injector.class);
    public final ScreenCaptureThread screenCaptureThread;

    @Autowired
    public Injector(ScreenCaptureThread screenCaptureThread) {
        this.screenCaptureThread = screenCaptureThread;
    }

    public void restart() {
        LOGGER.debug("restart() - start");

        stop();
        start();

        LOGGER.debug("restart() - end");
    }

    public void stop() {
        LOGGER.debug("stop() - start");

        screenCaptureThread.interrupt();

        LOGGER.debug("stop() - end");
    }

    public void start() {
        LOGGER.debug("start() - start");

        screenCaptureThread.start();

        LOGGER.debug("start() - end");
    }

}
