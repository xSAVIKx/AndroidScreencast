package com.github.xsavikx.android.screencast.api.command.factory;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.github.xsavikx.android.screencast.api.command.KeyCommand;
import com.github.xsavikx.android.screencast.api.command.SwipeCommand;
import com.github.xsavikx.android.screencast.api.command.TapCommand;

@Service
public final class AdbInputCommandFactory {
  /**
   * Logger for this class
   */
  private static final Logger LOGGER = Logger.getLogger(AdbInputCommandFactory.class);

  public static KeyCommand getKeyCommand(int keyCode) {
    LOGGER.debug("getKeyCommand(int keyCode=" + keyCode + ") - start");

    KeyCommand returnKeyCommand = new KeyCommand(keyCode);
    LOGGER.debug("getKeyCommand(int keyCode=" + keyCode + ") - end");
    return returnKeyCommand;
  }

  public static SwipeCommand getSwipeCommand(int x1, int y1, int x2, int y2) {
    LOGGER.debug("getSwipeCommand(int x1=" + x1 + ", int y1=" + y1 + ", int x2=" + x2 + ", int y2=" + y2 + ") - start");

    SwipeCommand returnSwipeCommand = new SwipeCommand(x1, y1, x2, y2);
    LOGGER.debug("getSwipeCommand(int x1=" + x1 + ", int y1=" + y1 + ", int x2=" + x2 + ", int y2=" + y2 + ") - end");
    return returnSwipeCommand;
  }

  public static TapCommand getTapCommand(int x, int y) {
    LOGGER.debug("getTapCommand(int x=" + x + ", int y=" + y + ") - start");

    TapCommand returnTapCommand = new TapCommand(x, y);
    LOGGER.debug("getTapCommand(int x=" + x + ", int y=" + y + ") - end");
    return returnTapCommand;
  }
}
