package com.github.xsavikx.android.screencast.ui.interaction;

import java.awt.KeyEventDispatcher;
import java.awt.Window;

public final class KeyEventDispatcherFactory {
  public static KeyEventDispatcher getKeyEventDispatcher(Window frame) {
    return new KeyEventDispatcherImpl(frame);
  }
}
