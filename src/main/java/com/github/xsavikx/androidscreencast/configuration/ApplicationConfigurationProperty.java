package com.github.xsavikx.androidscreencast.configuration;

import static com.github.xsavikx.androidscreencast.configuration.ApplicationConfigurationPropertyKeys.*;

public enum ApplicationConfigurationProperty {
    ADB_PATH(ADB_PATH_KEY, ""),
    ADB_DEVICE_TIMEOUT(ADB_DEVICE_TIMEOUT_KEY, "30"),
    ADB_COMMAND_TIMEOUT(ADB_COMMAND_TIMEOUT_KEY, "5"),
    APP_WINDOW_WIDTH(APP_WINDOW_WIDTH_KEY, "1024"),
    APP_WINDOW_HEIGHT(APP_WINDOW_HEIGHT_KEY, "768"),
    APP_NATIVE_LOOK(APP_NATIVE_LOOK_KEY, "true");
    private final String propertyKey;
    private final String defaultValue;

    ApplicationConfigurationProperty(final String propertyKey, String defaultValue) {
        this.propertyKey = propertyKey;
        this.defaultValue = defaultValue;
    }

    public String getPropertyKey() {
        return propertyKey;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
}