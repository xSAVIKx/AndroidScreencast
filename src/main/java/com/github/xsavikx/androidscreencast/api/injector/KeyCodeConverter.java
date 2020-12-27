/*
 * Copyright 2020 Yurii Serhiichuk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

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
