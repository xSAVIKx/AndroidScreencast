package com.github.xsavikx.androidscreencast.api.command.executor;

import com.android.ddmlib.*;
import com.github.xsavikx.androidscreencast.api.command.Command;
import com.github.xsavikx.androidscreencast.api.command.exception.AdbShellCommandExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static com.github.xsavikx.androidscreencast.configuration.ApplicationConfigurationPropertyKeys.ADB_COMMAND_TIMEOUT_KEY;

@Singleton
public class ShellCommandExecutor implements CommandExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShellCommandExecutor.class);
    private final IDevice device;
    private final IShellOutputReceiver shellOutputReceiver;
    private final long adbCommandTimeout;

    @Inject
    public ShellCommandExecutor(final IDevice device,
                                final IShellOutputReceiver shellOutputReceiver,
                                @Named(ADB_COMMAND_TIMEOUT_KEY) long adbCommandTimeout) {
        this.device = device;
        this.shellOutputReceiver = shellOutputReceiver;
        this.adbCommandTimeout = adbCommandTimeout;
    }

    @Override
    public void execute(Command command) {
        LOGGER.debug("execute(Command command={}) - start", command);

        try {
            device.executeShellCommand(command.getFormattedCommand(), shellOutputReceiver,
                    adbCommandTimeout, TimeUnit.SECONDS);
        } catch (TimeoutException | AdbCommandRejectedException | ShellCommandUnresponsiveException | IOException e) {
            LOGGER.error("execute(Command command={})", command, e);
            throw new AdbShellCommandExecutionException(command, e);
        }

        LOGGER.debug("execute(Command command={}) - end", command);
    }

}
