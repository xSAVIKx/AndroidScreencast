/*
 * Copyright 2020 Yurii Serhiichuk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.github.xsavikx.androidscreencast.api.command.executor;

import com.android.ddmlib.*;
import com.github.xsavikx.androidscreencast.api.command.Command;
import com.github.xsavikx.androidscreencast.api.command.exception.AdbShellCommandExecutionException;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static com.github.xsavikx.androidscreencast.configuration.ApplicationConfigurationPropertyKeys.ADB_COMMAND_TIMEOUT_KEY;
import static org.slf4j.LoggerFactory.getLogger;

@Singleton
public final class ShellCommandExecutor implements CommandExecutor {

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
        log().debug("Executing command: {}", command);

        try {
            device.executeShellCommand(command.getFormattedCommand(), shellOutputReceiver,
                    adbCommandTimeout, TimeUnit.SECONDS);
            log().debug("Command {} successfully executed.", command);
        } catch (TimeoutException | AdbCommandRejectedException | ShellCommandUnresponsiveException | IOException e) {
            log().error("An exception happened during command execution: {}.", command, e);
            throw new AdbShellCommandExecutionException(command, e);
        }
    }

    private enum LogSingleton {
        INSTANCE;

        @SuppressWarnings({"NonSerializableFieldInSerializableClass", "ImmutableEnumChecker"})
        private final Logger value = getLogger(ShellCommandExecutor.class);
    }

    private static Logger log() {
        return LogSingleton.INSTANCE.value;
    }
}
