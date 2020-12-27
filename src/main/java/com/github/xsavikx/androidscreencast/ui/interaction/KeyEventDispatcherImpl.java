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

package com.github.xsavikx.androidscreencast.ui.interaction;

import com.github.xsavikx.androidscreencast.api.command.KeyCommand;
import com.github.xsavikx.androidscreencast.api.command.executor.CommandExecutor;
import com.github.xsavikx.androidscreencast.api.command.factory.InputCommandFactory;
import com.github.xsavikx.androidscreencast.api.injector.KeyCodeConverter;
import com.github.xsavikx.androidscreencast.dagger.MainComponentProvider;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public final class KeyEventDispatcherImpl implements KeyEventDispatcher {

    private final Window window;
    private CommandExecutor commandExecutor;
    private InputCommandFactory inputCommandFactory;

    KeyEventDispatcherImpl(Window frame) {
        this.window = frame;
    }

    @Override
    public boolean dispatchKeyEvent(final KeyEvent e) {
        if (!window.isActive())
            return false;
        if (e.getID() == KeyEvent.KEY_TYPED) {
            final int code = KeyCodeConverter.getKeyCode(e);
            SwingUtilities.invokeLater(() -> {
                final KeyCommand command = getInputCommandFactory().getKeyCommand(code);
                getCommandExecutor().execute(command);
            });
        }
        return false;
    }

    private InputCommandFactory getInputCommandFactory() {
        if (inputCommandFactory == null) {
            inputCommandFactory = MainComponentProvider.mainComponent().inputCommandFactory();
        }
        return inputCommandFactory;
    }

    private CommandExecutor getCommandExecutor() {
        if (commandExecutor == null) {
            commandExecutor = MainComponentProvider.mainComponent().commandExecutor();
        }
        return commandExecutor;
    }
}
