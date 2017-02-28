package com.github.xsavikx.androidscreencast.ui.interaction;

import com.github.xsavikx.androidscreencast.api.command.KeyCommand;
import com.github.xsavikx.androidscreencast.api.command.executor.CommandExecutor;
import com.github.xsavikx.androidscreencast.api.command.factory.InputCommandFactory;
import com.github.xsavikx.androidscreencast.dagger.MainComponentProvider;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class KeyboardActionListener implements ActionListener {
    private final int key;
    private CommandExecutor commandExecutor;
    private InputCommandFactory inputCommandFactory;

    public KeyboardActionListener(int key) {
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
