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

package com.github.xsavikx.androidscreencast.api.command;

import com.github.xsavikx.androidscreencast.api.injector.InputKeyEvent;

public final class KeyCommand extends InputCommand {

    private final int code;
    private boolean longpress;

    public KeyCommand(int keyCode) {
        this.code = keyCode;
    }

    private KeyCommand(InputKeyEvent inputKeyEvent) {
        code = inputKeyEvent.getCode();
    }

    public KeyCommand(int keyCode, boolean longpress) {
        this(keyCode);
        this.longpress = longpress;
    }

    public KeyCommand(InputKeyEvent inputKeyEvent, boolean longpress) {
        this(inputKeyEvent);
        this.longpress = longpress;
    }

    public void setLongPress(boolean longpress) {
        this.longpress = longpress;
    }

    @Override
    protected String getCommandPart() {
        StringBuilder stringBuilder = new StringBuilder("keyevent");
        if (longpress) {
            stringBuilder.append(" --longpress");
        }
        stringBuilder.append(' ').append(code);
        return stringBuilder.toString();
    }
}
