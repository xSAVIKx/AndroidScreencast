package com.github.xsavikx.android.screencast;

import org.apache.log4j.Logger;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.github.xsavikx.android.screencast.api.command.executor.CommandExecutor;
import com.github.xsavikx.android.screencast.api.command.executor.ShellCommandExecutor;
import com.github.xsavikx.android.screencast.api.injector.Injector;
import com.github.xsavikx.android.screencast.app.SwingApplication;
import com.github.xsavikx.android.screencast.ui.JDialogDeviceList;
import com.github.xsavikx.android.screencast.ui.JFrameMain;
import com.github.xsavikx.android.screencast.ui.JSplashScreen;

public class Main extends SwingApplication {
  private static int PORT = 2436;

  public static void main(String args[]) {
    if (logger.isDebugEnabled()) {
      logger.debug("main(String[]) - start");
    }
    boolean nativeLook = true;
    for (int i = 0; i < args.length; i++) {
      if (args[i].equalsIgnoreCase("nonativelook")) {
        nativeLook = false;
      }
      if (args[i].equalsIgnoreCase("port") && i + 1 < args.length) {
        PORT = Integer.parseInt(args[i + 1]);
      }
    }
    new Main(nativeLook);

    if (logger.isDebugEnabled()) {
      logger.debug("main(String[]) - end");
    }
  }

  /**
   * Logger for this class
   */
  private static final Logger logger = Logger.getLogger(Main.class);
  JFrameMain jf;
  Injector injector;
  CommandExecutor commandExecutor;

  volatile IDevice device;

  public Main(boolean nativeLook) {
    super(nativeLook);
    JSplashScreen jw = new JSplashScreen("");

    try {
      initialize(jw);
    } finally {
      jw.setVisible(false);
      jw = null;
    }
  }

  @Override
  protected void close() {
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
        AndroidDebugBridge.terminate();
      }
    }
    if (logger.isInfoEnabled()) {
      logger.info("close() - Cleaning done, exiting...");
    }
    super.close();

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
    AndroidDebugBridge.initIfNeeded(false);
    AndroidDebugBridge bridge = AndroidDebugBridge.createBridge();

    waitDeviceList(bridge);

    IDevice devices[] = bridge.getDevices();

    jw.setVisible(false);

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

    injector = new Injector(device, PORT);
    injector.start();
    commandExecutor = new ShellCommandExecutor(device);
    jf.setInjector(injector);
    jf.setCommandExecutor(commandExecutor);
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

}
