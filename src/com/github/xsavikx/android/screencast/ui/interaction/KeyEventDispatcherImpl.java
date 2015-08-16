package com.github.xsavikx.android.screencast.ui.interaction;

import java.awt.KeyEventDispatcher;
import java.awt.Window;
import java.awt.event.KeyEvent;

import com.github.xsavikx.android.screencast.api.command.executor.CommandExecutor;
import com.github.xsavikx.android.screencast.api.command.factory.AdbInputCommandFactory;
import com.github.xsavikx.android.screencast.api.injector.KeyCodeConverter;
import com.github.xsavikx.android.screencast.spring.config.ApplicationContextProvider;

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
      int code = KeyCodeConverter.getKeyCode(e);
      getCommandExecutor().execute(AdbInputCommandFactory.getKeyCommand(code));
    }
    return false;
  }

  private CommandExecutor getCommandExecutor() {
    if (commandExecutor == null) {
      commandExecutor = ApplicationContextProvider.getApplicationContext().getBean(CommandExecutor.class);
    }
    return commandExecutor;
  }
}
