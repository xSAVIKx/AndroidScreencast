package com.github.xsavikx.androidscreencast.ui.interaction;

import com.github.xsavikx.androidscreencast.api.command.executor.CommandExecutor;
import com.github.xsavikx.androidscreencast.api.command.factory.AdbInputCommandFactory;
import com.github.xsavikx.androidscreencast.api.injector.KeyCodeConverter;
import com.github.xsavikx.androidscreencast.dagger.MainComponentProvider;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class KeyEventDispatcherImpl implements KeyEventDispatcher {
    private CommandExecutor commandExecutor;
    private Window window;

    public KeyEventDispatcherImpl(Window frame) {
        this.window = frame;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if (!window.isActive())
            return false;
        if (e.getID() == KeyEvent.KEY_TYPED) {
            final int code = KeyCodeConverter.getKeyCode(e);
            SwingUtilities.invokeLater(() -> getCommandExecutor().execute(AdbInputCommandFactory.getKeyCommand(code)));

        }
        return false;
    }

    private CommandExecutor getCommandExecutor() {
        if (commandExecutor == null) {
            commandExecutor = MainComponentProvider.mainComponent().commandExecutor();
        }
        return commandExecutor;
    }
}
