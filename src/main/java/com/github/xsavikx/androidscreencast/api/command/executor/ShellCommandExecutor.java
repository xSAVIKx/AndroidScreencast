package com.github.xsavikx.androidscreencast.api.command.executor;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;
import com.github.xsavikx.androidscreencast.api.command.Command;
import com.github.xsavikx.androidscreencast.api.command.exception.AdbShellCommandExecutionException;
import com.github.xsavikx.androidscreencast.api.injector.MultiLineReceiverPrinter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Service
public class ShellCommandExecutor implements CommandExecutor {
    private static final Logger LOGGER = Logger.getLogger(ShellCommandExecutor.class);
    private static final int MAX_TIME_TO_WAIT_RESPONSE = 5;
    private final IDevice device;

    @Autowired
    public ShellCommandExecutor(IDevice device) {
        this.device = device;
    }

    @Override
    public void execute(Command command) {
        LOGGER.debug("execute(Command command=" + command + ") - start");

        try {
            device.executeShellCommand(command.getFormattedCommand(), new MultiLineReceiverPrinter(),
                    MAX_TIME_TO_WAIT_RESPONSE, TimeUnit.SECONDS);
        } catch (TimeoutException | AdbCommandRejectedException | ShellCommandUnresponsiveException | IOException e) {
            LOGGER.error("execute(Command)", e);
            throw new AdbShellCommandExecutionException(command, e);
        }

        LOGGER.debug("execute(Command command=" + command + ") - end");
    }

}
