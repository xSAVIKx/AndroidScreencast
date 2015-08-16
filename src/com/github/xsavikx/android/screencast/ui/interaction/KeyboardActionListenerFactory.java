package com.github.xsavikx.android.screencast.ui.interaction;

public final class KeyboardActionListenerFactory {
  public static KeyboardActionListener getInstance(int key) {
    return new KeyboardActionListener(key);
  }
}
