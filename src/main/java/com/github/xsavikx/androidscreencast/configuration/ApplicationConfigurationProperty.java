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

package com.github.xsavikx.androidscreencast.configuration;

import static com.github.xsavikx.androidscreencast.configuration.ApplicationConfigurationPropertyKeys.*;

public enum ApplicationConfigurationProperty {
    ADB_PATH(ADB_PATH_KEY, ""),
    ADB_DEVICE_TIMEOUT(ADB_DEVICE_TIMEOUT_KEY, "30"),
    ADB_COMMAND_TIMEOUT(ADB_COMMAND_TIMEOUT_KEY, "5"),
    APP_WINDOW_WIDTH(APP_WINDOW_WIDTH_KEY, "1024"),
    APP_WINDOW_HEIGHT(APP_WINDOW_HEIGHT_KEY, "768"),
    APP_NATIVE_LOOK(APP_NATIVE_LOOK_KEY, "true"),
    APP_DEBUG_ENABLED(APP_DEBUG_ENABLED_KEY, "false");
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