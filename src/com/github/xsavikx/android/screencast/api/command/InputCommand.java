package com.github.xsavikx.android.screencast.api.command;

public abstract class InputCommand implements Command {
  private static final String TO_STRING_PATTERN = "%s [%s]";
  protected static final String INPUT = "input";
  protected static final String WHITESPACE = " ";

  @Override
  public String toString() {
    return String.format(TO_STRING_PATTERN, this.getClass().getSimpleName(), getFormatedCommand());
  }
}
