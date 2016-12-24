package com.github.xsavikx.androidscreencast.app;

public interface Application {

    void stop();

    void handleException(Thread thread, Throwable ex);

    void start();

    void init();
}
