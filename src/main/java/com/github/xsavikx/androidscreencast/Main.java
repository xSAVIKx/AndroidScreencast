package com.github.xsavikx.androidscreencast;

import com.github.xsavikx.androidscreencast.app.Application;
import com.github.xsavikx.androidscreencast.dagger.MainComponentProvider;
import org.slf4j.Logger;

import java.util.Arrays;

import static org.slf4j.LoggerFactory.getLogger;

public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        log().debug("Starting Android Screencast with the args: {}", Arrays.toString(args));
        try {
            Application application = MainComponentProvider.mainComponent().application();
            application.init();
            application.start();
        } finally {
            log().debug("The application started.");
        }
    }

    private enum LogSingleton {
        INSTANCE;

        @SuppressWarnings({"NonSerializableFieldInSerializableClass", "ImmutableEnumChecker"})
        private final Logger value = getLogger(Main.class);
    }

    private static Logger log() {
        return LogSingleton.INSTANCE.value;
    }
}
