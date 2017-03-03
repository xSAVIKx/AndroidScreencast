package com.github.xsavikx.androidscreencast.ui.interaction;

import com.github.xsavikx.androidscreencast.api.command.KeyCommand;
import com.github.xsavikx.androidscreencast.api.command.executor.CommandExecutor;
import com.github.xsavikx.androidscreencast.api.command.factory.InputCommandFactory;
import com.github.xsavikx.androidscreencast.api.injector.KeyCodeConverter;
import com.github.xsavikx.androidscreencast.dagger.MainComponentProvider;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class KeyEventDispatcherImpl implements KeyEventDispatcher {
    private final Window window;
    private CommandExecutor commandExecutor;
    private InputCommandFactory inputCommandFactory;

    public KeyEventDispatcherImpl(Window frame) {
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
