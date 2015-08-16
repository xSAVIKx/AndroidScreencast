package com.github.xsavikx.android.screencast.app;

public interface Application {

  public void close();

  public void handleException(Thread thread, Throwable ex);

  public void start();

  public void init();
}
