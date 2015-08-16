package com.github.xsavikx.android.screencast.api.injector;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.xsavikx.android.screencast.spring.config.ApplicationContextProvider;

@Service
public class Injector {
  private static final Logger LOGGER = Logger.getLogger(Injector.class);
  @Autowired
  public ScreenCaptureThread screencapture;

  public void restart() {
    LOGGER.debug("restart() - start");

    close();
    screencapture = ApplicationContextProvider.getApplicationContext().getBean(ScreenCaptureThread.class);
    start();

    LOGGER.debug("restart() - end");
  }

  public void close() {
    LOGGER.debug("close() - start");

    screencapture.interrupt();

    LOGGER.debug("close() - end");
  }

  public void start() {
    LOGGER.debug("start() - start");

    screencapture.start();

    LOGGER.debug("start() - end");
  }

}
