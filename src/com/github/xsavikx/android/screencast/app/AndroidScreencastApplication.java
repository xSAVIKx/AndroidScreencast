package com.github.xsavikx.android.screencast.app;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.github.xsavikx.android.screencast.api.command.executor.CommandExecutor;
import com.github.xsavikx.android.screencast.api.injector.Injector;
import com.github.xsavikx.android.screencast.constant.Constants;
import com.github.xsavikx.android.screencast.ui.JDialogDeviceList;
import com.github.xsavikx.android.screencast.ui.JFrameMain;
import com.github.xsavikx.android.screencast.ui.JSplashScreen;

@Component
public class AndroidScreencastApplication extends SwingApplication {

  @Autowired
  private Environment env;

  private static final Logger logger = Logger.getLogger(AndroidScreencastApplication.class);
  private JFrameMain jf;
  private Injector injector;
  @Autowired
  private CommandExecutor commandExecutor;
  @Autowired
  private AndroidDebugBridge bridge;
  @Autowired
  private IDevice device;

  @Override
  public void close() {
    if (logger.isDebugEnabled()) {
      logger.debug("close() - start");
    }

    if (logger.isInfoEnabled()) {
      logger.info("close() - Cleaning up...");
    }
    if (injector != null)
      injector.close();

    if (device != null) {
      synchronized (device) {
        if (hasFilledAdbPath())
          AndroidDebugBridge.disconnectBridge();
        AndroidDebugBridge.terminate();
      }
    }
    if (logger.isInfoEnabled()) {
      logger.info("close() - Cleaning done, exiting...");
    }

    if (logger.isDebugEnabled()) {
      logger.debug("close() - end");
    }
  }

  private void initialize(JSplashScreen jw) {
    if (logger.isDebugEnabled()) {
      logger.debug("initialize(JSplashScreen) - start");
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

      if (logger.isDebugEnabled()) {
        logger.debug("initialize(JSplashScreen) - end");
      }
      return;
    }

    // Start showing the device screen
    jf = new JFrameMain(device);
    jf.setTitle("" + device);

    // Show window
    jf.setVisible(true);

    // Starting injector
    jw.setText("Starting input injector...");
    jw.setVisible(true);

    injector = new Injector(device, getAdbPort());
    injector.start();
    commandExecutor.setDevice(device);
    jf.setInjector(injector);
    jf.setCommandExecutor(commandExecutor);
    jw.removeAll();
    jw.setVisible(false);
    if (logger.isDebugEnabled()) {
      logger.debug("initialize(JSplashScreen) - end");
    }
  }

  private void waitDeviceList(AndroidDebugBridge bridge) {
    if (logger.isDebugEnabled()) {
      logger.debug("waitDeviceList(AndroidDebugBridge) - start");
    }

    int count = 0;
    while (bridge.hasInitialDeviceList() == false) {
      try {
        Thread.sleep(100);
        count++;
      } catch (InterruptedException e) {
        logger.warn("waitDeviceList(AndroidDebugBridge) - exception ignored", e);
      }
      // let's not wait > 10 sec.
      if (count > 300) {
        throw new RuntimeException("Timeout getting device list!");
      }
    }

    if (logger.isDebugEnabled()) {
      logger.debug("waitDeviceList(AndroidDebugBridge) - end");
    }
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

  @SuppressWarnings("boxing")
  private int getAdbPort() {
    return env.getProperty(Constants.ADB_PORT_PROPERTY, Integer.class, Constants.DEFAULT_ADB_PORT);
  }

  private boolean hasFilledAdbPath() {
    return env.getProperty(Constants.ADB_PATH_PROPERTY) != null;
  }
}
