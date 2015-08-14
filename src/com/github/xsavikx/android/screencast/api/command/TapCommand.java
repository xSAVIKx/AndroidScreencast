package com.github.xsavikx.android.screencast.api.command;

public class TapCommand extends InputCommand {
  private static final String COMMAND_FORMAT = INPUT + WHITESPACE + "tap %d %d";
  private int x;
  private int y;

  public TapCommand(int x, int y) {
    this.x = x;
    this.y = y;
  }

  @Override
  public String getFormatedCommand() {
    return String.format(COMMAND_FORMAT, x, y);
  }

}
