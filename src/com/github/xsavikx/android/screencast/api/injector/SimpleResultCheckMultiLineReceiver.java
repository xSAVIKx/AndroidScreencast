package com.github.xsavikx.android.screencast.api.injector;

import com.android.ddmlib.MultiLineReceiver;

public class SimpleResultCheckMultiLineReceiver extends MultiLineReceiver {
  private String successPattern;
  private boolean isSucceed;

  public SimpleResultCheckMultiLineReceiver(String successPattern) {
    this.successPattern = successPattern;
    this.isSucceed = false;
  }

  @Override
  public boolean isCancelled() {
    return false;
  }

  @Override
  public void processNewLines(String[] arg0) {
    for (String elem : arg0) {
      if (!successPattern.isEmpty() && elem.indexOf(successPattern) > 0) {
        isSucceed = true;
      }
      System.out.println(elem);
    }
  }

  public boolean isSucceed() {
    return isSucceed;
  }
}
