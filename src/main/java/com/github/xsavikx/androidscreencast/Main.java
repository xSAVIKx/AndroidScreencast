package com.github.xsavikx.androidscreencast;

import com.github.xsavikx.androidscreencast.app.AndroidScreencastApplication;
import com.github.xsavikx.androidscreencast.app.Application;
import com.github.xsavikx.androidscreencast.spring.config.ApplicationContextProvider;
import org.apache.log4j.Logger;

import java.util.Arrays;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class);

    public static void main(String args[]) {
        LOGGER.debug("main(String[] args=" + Arrays.toString(args) + ") - start");
        Application application;
        try {
            application = ApplicationContextProvider.getBean(AndroidScreencastApplication.class);
            application.init();
            application.start();
        } finally {
            LOGGER.debug("main(String[] args=" + Arrays.toString(args) + ") - end");
        }
    }

}
