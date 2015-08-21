package com.github.xsavikx.android.screencast.api.command;

import com.github.xsavikx.android.screencast.api.injector.InputKeyEvent;

public class KeyCommand extends InputCommand {
  private static final String COMMAND_FORMAT = INPUT + WHITESPACE + "keyevent %d";
  private int code;

  public KeyCommand(int keyCode) {
    this.code = keyCode;
  }

  public KeyCommand(InputKeyEvent inputKeyEvent) {
    code = inputKeyEvent.getCode();
  }

  @Override
  public String getFormatedCommand() {
    return String.format(COMMAND_FORMAT, code);
  }

}
