package com.github.xsavikx.android.screencast.ui.interaction;

import com.github.xsavikx.android.screencast.api.injector.InputKeyEvent;

public final class KeyboardActionListenerFactory {
  public static KeyboardActionListener getInstance(InputKeyEvent inputKeyEvent) {
    return new KeyboardActionListener(inputKeyEvent.getCode());
  }
}
