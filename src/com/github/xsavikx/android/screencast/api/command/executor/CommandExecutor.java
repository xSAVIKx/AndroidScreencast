package com.github.xsavikx.android.screencast.api.command.executor;

import com.android.ddmlib.IDevice;
import com.github.xsavikx.android.screencast.api.command.Command;

public interface CommandExecutor {
  public void execute(Command command);

  public void setDevice(IDevice device);
}
