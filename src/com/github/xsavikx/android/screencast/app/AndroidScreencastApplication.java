package com.github.xsavikx.android.screencast.app;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.github.xsavikx.android.screencast.api.injector.Injector;
import com.github.xsavikx.android.screencast.constant.Constants;
import com.github.xsavikx.android.screencast.ui.JFrameMain;

@Component
public class AndroidScreencastApplication extends SwingApplication {

  @Autowired
  private Environment env;

  private static final Logger LOGGER = Logger.getLogger(AndroidScreencastApplication.class);
  @Autowired
  private JFrameMain jf;
  @Autowired
  private Injector injector;
  @Autowired
  private IDevice device;

  @Override
  public void close() {
    LOGGER.debug("close() - start");

    if (injector != null)
      injector.close();

    if (device != null) {
      synchronized (device) {
        if (hasFilledAdbPath())
          AndroidDebugBridge.disconnectBridge();
        AndroidDebugBridge.terminate();
      }
    }

    LOGGER.debug("close() - end");
  }

  @Override
  public void start() {
    LOGGER.debug("start() - start");

    // Start showing the device screen
    jf.setTitle("" + device);

    // Show window
    jf.setVisible(true);

    jf.launchInjector();

    LOGGER.debug("start() - end");
  }

  @SuppressWarnings("boxing")
  @Override
  protected boolean isNativeLook() {
    LOGGER.debug("isNativeLook() - start");

    boolean returnboolean = env.getProperty(Constants.APP_NATIVE_LOOK_PROPERTY, Boolean.class,
        Constants.DEFAULT_APP_NATIVE_LOOK);
    LOGGER.debug("isNativeLook() - end");
    return returnboolean;
  }

  private boolean hasFilledAdbPath() {
    LOGGER.debug("hasFilledAdbPath() - start");

    boolean returnboolean = env.getProperty(Constants.ADB_PATH_PROPERTY) != null;
    LOGGER.debug("hasFilledAdbPath() - end");
    return returnboolean;
  }
}
