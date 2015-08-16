package com.github.xsavikx.android.screencast.ui.interaction;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.github.xsavikx.android.screencast.api.command.executor.CommandExecutor;
import com.github.xsavikx.android.screencast.api.command.factory.AdbInputCommandFactory;
import com.github.xsavikx.android.screencast.spring.config.ApplicationContextProvider;

public class KeyboardActionListener implements ActionListener {
  private CommandExecutor commandExecutor;
  private int key;

  public KeyboardActionListener(int key) {
    this.key = key;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    getCommandExecutor().execute(AdbInputCommandFactory.getKeyCommand(key));
  }

  private CommandExecutor getCommandExecutor() {
    if (commandExecutor == null) {
      commandExecutor = ApplicationContextProvider.getApplicationContext().getBean(CommandExecutor.class);
    }
    return commandExecutor;
  }

}
