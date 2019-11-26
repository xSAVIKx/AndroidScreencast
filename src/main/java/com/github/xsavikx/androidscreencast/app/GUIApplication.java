package com.github.xsavikx.androidscreencast.app;

import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

abstract class GUIApplication implements Application {

    GUIApplication() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
        Thread.setDefaultUncaughtExceptionHandler((thread, ex) -> {
            try {
                handleException(thread, ex);
            } catch (final Exception ex2) {
                log().error("Error occurred during exception handling.", ex2);
            }
        });
    }

    private enum LogSingleton {
        INSTANCE;

        @SuppressWarnings({"NonSerializableFieldInSerializableClass", "ImmutableEnumChecker"})
        private final Logger value = getLogger(GUIApplication.class);
    }

    private static Logger log() {
        return LogSingleton.INSTANCE.value;
    }
}
