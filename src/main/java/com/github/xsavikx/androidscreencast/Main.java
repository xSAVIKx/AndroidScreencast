package com.github.xsavikx.androidscreencast;

import com.github.xsavikx.androidscreencast.app.Application;
import com.github.xsavikx.androidscreencast.dagger.MainComponentProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String args[]) {
        LOGGER.debug("main(String[] args={}) - start", Arrays.toString(args));
        try {
            Application application = MainComponentProvider.mainComponent().application();
            application.init();
            application.start();
        } finally {
            LOGGER.debug("main(String[] args={}) - end", Arrays.toString(args));
        }
    }

}
