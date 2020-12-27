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

package com.github.xsavikx.androidscreencast.api.command.factory;

import com.github.xsavikx.androidscreencast.api.command.KeyCommand;
import com.github.xsavikx.androidscreencast.api.command.SwipeCommand;
import com.github.xsavikx.androidscreencast.api.command.TapCommand;
import com.github.xsavikx.androidscreencast.api.injector.InputKeyEvent;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

import static org.slf4j.LoggerFactory.getLogger;

@Singleton
public final class AdbInputCommandFactory implements InputCommandFactory {

    @Inject
    public AdbInputCommandFactory() {
    }

    @Override
    public KeyCommand getKeyCommand(final int keyCode) {
        final KeyCommand returnKeyCommand = new KeyCommand(keyCode);
        log().debug(String.valueOf(returnKeyCommand));
        return returnKeyCommand;
    }

    @Override
    public KeyCommand getKeyCommand(final InputKeyEvent inputKeyEvent, final boolean longpress) {
        final KeyCommand returnKeyCommand = new KeyCommand(inputKeyEvent, longpress);
        log().debug(String.valueOf(returnKeyCommand));
        return returnKeyCommand;
    }

    @Override
    public SwipeCommand getSwipeCommand(final int x1, final int y1, final int x2, final int y2, final long duration) {
        final SwipeCommand returnSwipeCommand = new SwipeCommand(x1, y1, x2, y2, duration);
        log().debug(String.valueOf(returnSwipeCommand));
        return returnSwipeCommand;
    }

    @Override
    public TapCommand getTapCommand(final int x, final int y) {
        final TapCommand returnTapCommand = new TapCommand(x, y);
        log().debug(String.valueOf(returnTapCommand));
        return returnTapCommand;
    }

    private enum LogSingleton {
        INSTANCE;

        @SuppressWarnings({"NonSerializableFieldInSerializableClass", "ImmutableEnumChecker"})
        private final Logger value = getLogger(AdbInputCommandFactory.class);
    }

    private static Logger log() {
        return LogSingleton.INSTANCE.value;
    }
}
