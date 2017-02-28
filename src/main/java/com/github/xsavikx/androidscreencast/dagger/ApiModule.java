package com.github.xsavikx.androidscreencast.dagger;

import com.github.xsavikx.androidscreencast.api.AndroidDevice;
import com.github.xsavikx.androidscreencast.api.AndroidDeviceImpl;
import com.github.xsavikx.androidscreencast.api.command.executor.CommandExecutor;
import com.github.xsavikx.androidscreencast.api.command.executor.ShellCommandExecutor;
import com.github.xsavikx.androidscreencast.configuration.ApplicationConfiguration;
import dagger.Module;
import dagger.Provides;

import javax.inject.Named;
import javax.inject.Singleton;

import static com.github.xsavikx.androidscreencast.configuration.ApplicationConfigurationProperty.*;
import static com.github.xsavikx.androidscreencast.configuration.ApplicationConfigurationPropertyKeys.*;

@Singleton
@Module
public class ApiModule {
    @Singleton
    @Named(ADB_COMMAND_TIMEOUT_KEY)
    @Provides
    public static long adbCommandTimeout(ApplicationConfiguration applicationConfiguration) {
        return Long.valueOf(applicationConfiguration.getProperty(ADB_COMMAND_TIMEOUT));
    }

    @Singleton
    @Named(ADB_DEVICE_TIMEOUT_KEY)
    @Provides
    public static long adbDeviceTimeout(ApplicationConfiguration applicationConfiguration) {
        return Long.valueOf(applicationConfiguration.getProperty(ADB_DEVICE_TIMEOUT));
    }

    @Singleton
    @Named(ADB_PATH_KEY)
    @Provides
    public static String adbPath(ApplicationConfiguration applicationConfiguration) {
        return applicationConfiguration.getProperty(ADB_PATH);
    }

    @Singleton
    @Provides
    public CommandExecutor commandExecutor(ShellCommandExecutor shellCommandExecutor) {
        return shellCommandExecutor;
    }

    @Singleton
    @Provides
    public AndroidDevice androidDevice(AndroidDeviceImpl androidDevice) {
        return androidDevice;
    }
}
