package com.github.xsavikx.android.screencast.api.command.executor;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;
import com.github.xsavikx.android.screencast.api.command.Command;
import com.github.xsavikx.android.screencast.api.command.exception.AdbShellCommandExecutionException;
import com.github.xsavikx.android.screencast.api.injector.MultiLineReceiverPrinter;

@Service
public class ShellCommandExecutor implements CommandExecutor {
  private static final Logger LOGGER = Logger.getLogger(ShellCommandExecutor.class);
  private static final int MAX_TIME_TO_WAIT_RESPONSE = 5;

  private IDevice device;

  @Override
  public void execute(Command command) {
    LOGGER.debug("execute(Command command=" + command + ") - start");

    try {
      device.executeShellCommand(command.getFormatedCommand(), new MultiLineReceiverPrinter(),
          MAX_TIME_TO_WAIT_RESPONSE, TimeUnit.SECONDS);
    } catch (TimeoutException | AdbCommandRejectedException | ShellCommandUnresponsiveException | IOException e) {
      LOGGER.error("execute(Command)", e);
      throw new AdbShellCommandExecutionException(command, e);
    }

    LOGGER.debug("execute(Command command=" + command + ") - end");
  }

  @Override
  public void setDevice(IDevice device) {
    this.device = device;
  }

}
