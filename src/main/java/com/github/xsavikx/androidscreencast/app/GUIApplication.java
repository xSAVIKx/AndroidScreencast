package com.github.xsavikx.androidscreencast.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class GUIApplication implements Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(GUIApplication.class);

    GUIApplication() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
        Thread.setDefaultUncaughtExceptionHandler((thread, ex) -> {
            try {
                handleException(thread, ex);
            } catch (final Exception ex2) {
                LOGGER.error("Error occurred during exception handling.", ex2);
            }
        });
    }

}
