package com.github.xsavikx.androidscreencast.ui.interaction;

import java.awt.*;

public final class KeyEventDispatcherFactory {
    public static KeyEventDispatcher getKeyEventDispatcher(Window frame) {
        return new KeyEventDispatcherImpl(frame);
    }
}
