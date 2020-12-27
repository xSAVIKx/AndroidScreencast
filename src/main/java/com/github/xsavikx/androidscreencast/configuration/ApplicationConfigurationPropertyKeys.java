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

public final class ApplicationConfigurationPropertyKeys {
    public static final String ADB_PATH_KEY = "adb.path";
    public static final String ADB_DEVICE_TIMEOUT_KEY = "adb.device.timeout";
    public static final String ADB_COMMAND_TIMEOUT_KEY = "adb.command.timeout";
    public static final String APP_WINDOW_WIDTH_KEY = "app.window.width";
    public static final String APP_WINDOW_HEIGHT_KEY = "app.window.height";
    public static final String APP_NATIVE_LOOK_KEY = "app.native.look";
    public static final String APP_DEBUG_ENABLED_KEY = "app.debug.enabled";

    private ApplicationConfigurationPropertyKeys() {
        //
    }
}
