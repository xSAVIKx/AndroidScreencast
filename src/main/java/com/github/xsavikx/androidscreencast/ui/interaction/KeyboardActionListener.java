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
import com.github.xsavikx.androidscreencast.dagger.MainComponentProvider;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public final class KeyboardActionListener implements ActionListener {

    private InputCommandFactory inputCommandFactory;
    private CommandExecutor commandExecutor;
    private final int key;

    KeyboardActionListener(int key) {
        this.key = key;
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        SwingUtilities.invokeLater(() -> {
            final KeyCommand command = getInputCommandFactory().getKeyCommand(key);
            getCommandExecutor().execute(command);
        });
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
