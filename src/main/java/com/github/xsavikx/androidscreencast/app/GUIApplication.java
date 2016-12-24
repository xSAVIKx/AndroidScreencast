package com.github.xsavikx.androidscreencast.app;

public abstract class GUIApplication implements Application {

    public GUIApplication() {
        Runtime.getRuntime().addShutdownHook(new Thread(GUIApplication.this::stop));
        Thread.setDefaultUncaughtExceptionHandler((thread, ex) -> {
            try {
                handleException(thread, ex);
            } catch (Exception ex2) {
                // ignored
                ex2.printStackTrace();
            }
        });
    }

}
