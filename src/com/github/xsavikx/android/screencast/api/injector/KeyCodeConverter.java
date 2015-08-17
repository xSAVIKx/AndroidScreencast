package com.github.xsavikx.android.screencast.api.injector;

import org.apache.log4j.Logger;

import java.awt.event.KeyEvent;

public class KeyCodeConverter {
  private static final Logger LOGGER = Logger.getLogger(KeyCodeConverter.class);

  public static int getKeyCode(KeyEvent e) {
    LOGGER.debug("getKeyCode(KeyEvent e=" + e + ") - start");
    int code = InputKeyEvent.KEYCODE_UNKNOWN.getCode();
    char c = e.getKeyChar();
    int keyCode = e.getKeyCode();
    InputKeyEvent inputKeyEvent = InputKeyEvent.getByCharacterOrKeyCode(Character.toLowerCase(c), keyCode);
    if (inputKeyEvent != null) {
      code = inputKeyEvent.getCode();
    }
    LOGGER.debug("getKeyCode(KeyEvent e=" + e + ") - end");
    return code;
  }

}
