package com.github.xsavikx.android.screencast;

import java.util.Arrays;

import org.apache.log4j.Logger;

import com.github.xsavikx.android.screencast.app.AndroidScreencastApplication;
import com.github.xsavikx.android.screencast.app.Application;
import com.github.xsavikx.android.screencast.spring.config.ApplicationContextProvider;

public class Main {
  private static final Logger LOGGER = Logger.getLogger(Main.class);

  public static void main(String args[]) {
    LOGGER.debug("main(String[] args=" + Arrays.toString(args) + ") - start");
    Application application = ApplicationContextProvider.getApplicationContext()
        .getBean(AndroidScreencastApplication.class);
    application.init();
    application.start();

    LOGGER.debug("main(String[] args=" + Arrays.toString(args) + ") - end");
  }

}
