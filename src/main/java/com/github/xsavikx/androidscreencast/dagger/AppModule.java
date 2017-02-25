package com.github.xsavikx.androidscreencast.dagger;

import com.android.ddmlib.IDevice;
import com.github.xsavikx.androidscreencast.app.AndroidScreencastApplication;
import com.github.xsavikx.androidscreencast.app.Application;
import com.github.xsavikx.androidscreencast.app.DeviceChooserApplication;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class AppModule {
    @Singleton
    @Provides
    public static Application application(AndroidScreencastApplication application) {
        return application;
    }

    @Singleton
    @Provides
    public static IDevice iDevice(final DeviceChooserApplication application) {
        application.init();
        application.start();
        application.stop();
        IDevice device = application.getDevice();
        return device;
    }
}
