package com.github.xsavikx.android.screencast;

import org.apache.log4j.Logger;

import com.github.xsavikx.android.screencast.app.AndroidScreencastApplication;
import com.github.xsavikx.android.screencast.app.Application;
import com.github.xsavikx.android.screencast.app.DeviceChooserApplication;
import com.github.xsavikx.android.screencast.spring.config.ApplicationContextProvider;

public class Main {

  public static void main(String args[]) {
    if (logger.isDebugEnabled()) {
      logger.debug("main(String[]) - start");
    }
//    Application deviceChooser = ApplicationContextProvider.getApplicationContext()
//        .getBean(DeviceChooserApplication.class);
//    deviceChooser.init();
//    deviceChooser.start();
    Application application = ApplicationContextProvider.getApplicationContext()
        .getBean(AndroidScreencastApplication.class);
    application.init();
    application.start();
    if (logger.isDebugEnabled()) {
      logger.debug("main(String[]) - end");
    }
  }

  /**
   * Logger for this class
   */
  private static final Logger logger = Logger.getLogger(Main.class);

}
