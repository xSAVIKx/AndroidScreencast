package com.github.xsavikx.android.screencast.app;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.github.xsavikx.android.screencast.constant.Constants;
import com.github.xsavikx.android.screencast.ui.JDialogDeviceList;
import com.github.xsavikx.android.screencast.ui.JSplashScreen;

@Component
public class DeviceChooserApplication extends SwingApplication {
  private static final Logger LOGGER = Logger.getLogger(DeviceChooserApplication.class);

  @Autowired
  private Environment env;
  @Autowired
  private AndroidDebugBridge bridge;

  private IDevice device;

  @Override
  public void close() {

  }

  @Override
  public void start() {
    JSplashScreen jw = new JSplashScreen("");

    try {
      initialize(jw);
    } finally {
      jw.setVisible(false);
      jw.removeAll();
      jw = null;
    }

  }

  @SuppressWarnings("boxing")
  @Override
  protected boolean isNativeLook() {
    return env.getProperty(Constants.APP_NATIVE_LOOK_PROPERTY, Boolean.class, Constants.DEFAULT_APP_NATIVE_LOOK);
  }

  private void waitDeviceList(AndroidDebugBridge bridge) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("waitDeviceList(AndroidDebugBridge) - start");
    }

    int count = 0;
    while (bridge.hasInitialDeviceList() == false) {
      try {
        Thread.sleep(100);
        count++;
      } catch (InterruptedException e) {
        LOGGER.warn("waitDeviceList(AndroidDebugBridge) - exception ignored", e);
      }
      // let's not wait > 10 sec.
      if (count > 300) {
        throw new RuntimeException("Timeout getting device list!");
      }
    }

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("waitDeviceList(AndroidDebugBridge) - end");
    }
  }

  private void initialize(JSplashScreen jw) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("initialize(JSplashScreen) - start");
    }

    jw.setText("Getting devices list...");
    jw.setVisible(true);

    waitDeviceList(bridge);

    IDevice devices[] = bridge.getDevices();
    jw.setVisible(false);
    jw.removeAll();
    // Let the user choose the device
    if (devices.length == 1) {
      device = devices[0];
    } else {
      JDialogDeviceList jd = new JDialogDeviceList(devices);
      jd.setVisible(true);

      device = jd.getDevice();
    }
    if (device == null) {
      System.exit(0);

      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("initialize(JSplashScreen) - end");
      }
      return;
    }

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("initialize(JSplashScreen) - end");
    }
  }

  public IDevice getDevice() {
    return device;
  }
}
