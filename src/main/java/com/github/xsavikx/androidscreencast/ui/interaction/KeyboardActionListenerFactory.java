package com.github.xsavikx.androidscreencast.ui.interaction;

import com.github.xsavikx.androidscreencast.api.injector.InputKeyEvent;

public final class KeyboardActionListenerFactory {

    public static KeyboardActionListener getInstance(InputKeyEvent inputKeyEvent) {
        return new KeyboardActionListener(inputKeyEvent.getCode());
    }
}
