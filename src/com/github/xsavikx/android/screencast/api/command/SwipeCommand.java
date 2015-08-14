package com.github.xsavikx.android.screencast.api.command;

public class SwipeCommand extends InputCommand {
  private static final String COMMAND_FORMAT = INPUT + WHITESPACE + "swipe %d %d %d %d";
  private int x1;
  private int y1;
  private int x2;
  private int y2;

  public SwipeCommand(int x1, int y1, int x2, int y2) {
    this.x1 = x1;
    this.y1 = y1;
    this.x2 = x2;
    this.y2 = y2;
  }

  @Override
  public String getFormatedCommand() {
    return String.format(COMMAND_FORMAT, x1, y1, x2, y2);
  }

}
