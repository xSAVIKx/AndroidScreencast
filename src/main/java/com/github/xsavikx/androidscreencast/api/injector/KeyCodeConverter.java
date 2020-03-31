package com.github.xsavikx.androidscreencast.api.injector;

import org.slf4j.Logger;

import java.awt.event.KeyEvent;

import static org.slf4j.LoggerFactory.getLogger;

public final class KeyCodeConverter {

    private KeyCodeConverter() {
    }

    public static int getKeyCode(KeyEvent e) {
        log().debug("Getting key code for event: {}.", e);
        int code = InputKeyEvent.KEYCODE_UNKNOWN.getCode();
        char c = e.getKeyChar();
        int keyCode = e.getKeyCode();
        InputKeyEvent inputKeyEvent = InputKeyEvent.getByCharacterOrKeyCode(Character.toLowerCase(c), keyCode);
        if (inputKeyEvent != null) {
            code = inputKeyEvent.getCode();
        }
        log().debug("Received KeyEvent={}. Produced KeyCode={}", e, code);
        return code;
    }

    private enum LogSingleton {
        INSTANCE;

        @SuppressWarnings({"NonSerializableFieldInSerializableClass", "ImmutableEnumChecker"})
        private final Logger value = getLogger(KeyCodeConverter.class);
    }

    private static Logger log() {
        return LogSingleton.INSTANCE.value;
    }
}
