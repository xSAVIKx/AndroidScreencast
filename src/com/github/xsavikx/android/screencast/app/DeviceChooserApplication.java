package com.github.xsavikx.android.screencast.app;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.github.xsavikx.android.screencast.constant.Constants;
import com.github.xsavikx.android.screencast.ui.JDialogDeviceList;

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
    // ignore
  }

  @Override
  public void start() {
    LOGGER.debug("start() - start");
    initialize();

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

  private void waitDeviceList(AndroidDebugBridge bridge) {
    LOGGER.debug("waitDeviceList(AndroidDebugBridge bridge=" + bridge + ") - start");

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

    LOGGER.debug("waitDeviceList(AndroidDebugBridge bridge=" + bridge + ") - end");
  }

  private void initialize() {
    LOGGER.debug("initialize() - start");

    waitDeviceList(bridge);

    IDevice devices[] = bridge.getDevices();
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

      LOGGER.debug("initialize() - end");
      return;
    }

    LOGGER.debug("initialize() - end");
  }

  public IDevice getDevice() {
    return device;
  }
}
