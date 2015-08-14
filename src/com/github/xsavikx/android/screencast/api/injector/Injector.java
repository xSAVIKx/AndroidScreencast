package com.github.xsavikx.android.screencast.api.injector;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;

public class Injector {
  /**
   * Logger for this class
   */
  private static final Logger logger = Logger.getLogger(Injector.class);
  private static int PORT = 2436;
  IDevice device;

  public ScreenCaptureThread screencapture;

  public Injector(IDevice d) {
    this.device = d;
    this.screencapture = new ScreenCaptureThread(device);
  }

  public Injector(IDevice d, int port) {
    this(d);
    Injector.PORT = port;
  }

  public void restart() {
    close();
    screencapture = new ScreenCaptureThread(device);
    start();
  }

  public void close() {
    if (logger.isDebugEnabled()) {
      logger.debug("close() - start");
    }

    screencapture.interrupt();

    if (logger.isDebugEnabled()) {
      logger.debug("close() - end");
    }
  }

  public void start() {
    if (logger.isDebugEnabled()) {
      logger.debug("start() - start");
    }

    screencapture.start();

    if (logger.isDebugEnabled()) {
      logger.debug("start() - end");
    }
  }
}
