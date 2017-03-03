package com.github.xsavikx.androidscreencast.dagger;

import com.github.xsavikx.androidscreencast.api.injector.InputKeyEvent;
import com.github.xsavikx.androidscreencast.configuration.ApplicationConfiguration;
import com.github.xsavikx.androidscreencast.configuration.ApplicationConfigurationProperty;
import dagger.Module;
import dagger.Provides;

import javax.inject.Named;
import javax.inject.Singleton;

import static com.github.xsavikx.androidscreencast.configuration.ApplicationConfigurationPropertyKeys.APP_WINDOW_HEIGHT_KEY;
import static com.github.xsavikx.androidscreencast.configuration.ApplicationConfigurationPropertyKeys.APP_WINDOW_WIDTH_KEY;

@Singleton
@Module
public class UiModule {
    @Singleton
    @Named(APP_WINDOW_WIDTH_KEY)
    @Provides
    public static int appWindowHeight(final ApplicationConfiguration applicationConfiguration) {
        return Integer.valueOf(applicationConfiguration.getProperty(ApplicationConfigurationProperty.APP_WINDOW_HEIGHT));
    }

    @Singleton
    @Named(APP_WINDOW_HEIGHT_KEY)
    @Provides
    public static int appWindowWidth(final ApplicationConfiguration applicationConfiguration) {
        return Integer.valueOf(applicationConfiguration.getProperty(ApplicationConfigurationProperty.APP_WINDOW_WIDTH));
    }

    @Singleton
    @Provides
    public static InputKeyEvent[] initialData() {
        return InputKeyEvent.values();
    }
}
