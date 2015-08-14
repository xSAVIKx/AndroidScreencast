package com.github.xsavikx.android.screencast.api.command;

public class KeyCommand extends InputCommand {
  private static final String COMMAND_FORMAT = INPUT + WHITESPACE + "keyevent %d";
  private int keyCode;

  public KeyCommand(int keyCode) {
    this.keyCode = keyCode;
  }

  @Override
  public String getFormatedCommand() {
    return String.format(COMMAND_FORMAT, keyCode);
  }

}
