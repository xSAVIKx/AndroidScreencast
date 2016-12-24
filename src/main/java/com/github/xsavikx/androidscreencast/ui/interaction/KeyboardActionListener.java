package com.github.xsavikx.androidscreencast.ui.interaction;

import com.github.xsavikx.androidscreencast.api.command.executor.CommandExecutor;
import com.github.xsavikx.androidscreencast.api.command.factory.AdbInputCommandFactory;
import com.github.xsavikx.androidscreencast.spring.config.ApplicationContextProvider;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class KeyboardActionListener implements ActionListener {
    private CommandExecutor commandExecutor;
    private int key;

    public KeyboardActionListener(int key) {
        this.key = key;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(() -> getCommandExecutor().execute(AdbInputCommandFactory.getKeyCommand(key)));

    }

    private CommandExecutor getCommandExecutor() {
        if (commandExecutor == null) {
            commandExecutor = ApplicationContextProvider.getBean(CommandExecutor.class);
        }
        return commandExecutor;
    }

}
