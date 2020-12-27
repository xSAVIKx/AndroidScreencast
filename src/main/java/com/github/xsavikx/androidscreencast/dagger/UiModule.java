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
