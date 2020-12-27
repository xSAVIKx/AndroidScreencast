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
        try {
            application.init();
            application.start();
            IDevice device = application.getDevice();
            return device;
        } catch (Throwable e) {
            application.stop();
            throw e;
        }
    }
}
