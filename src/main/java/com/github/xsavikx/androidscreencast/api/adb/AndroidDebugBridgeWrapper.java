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

package com.github.xsavikx.androidscreencast.api.adb;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.github.xsavikx.androidscreencast.exception.IllegalAdbConfigurationException;
import com.github.xsavikx.androidscreencast.util.StringUtils;
import com.google.common.annotations.VisibleForTesting;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;

import static com.github.xsavikx.androidscreencast.configuration.ApplicationConfigurationPropertyKeys.ADB_PATH_KEY;

@Singleton
public class AndroidDebugBridgeWrapper {
    private final String adbPath;
    private AndroidDebugBridge adb;

    @Inject
    public AndroidDebugBridgeWrapper(@Named(ADB_PATH_KEY) String adbPath) {
        this.adbPath = adbPath;
    }

    public IDevice[] getDevices() {
        return getAdb().getDevices();
    }

    public boolean hasInitialDeviceList() {
        return getAdb().hasInitialDeviceList();
    }

    public void stop() {
        if (hasAdbPathFilled()) {
            AndroidDebugBridge.disconnectBridge();
        }
        AndroidDebugBridge.terminate();
    }

    private AndroidDebugBridge getAdb() {
        if (adb == null) {
            init();
        }
        return adb;
    }

    private void init() {
        if (adb != null) {
            return;
        }
        try {
            adb = createAndroidDebugBridge();
        } catch (IllegalArgumentException e) {
            if (hasAdbProcFailed(e)) {
                throw new IllegalAdbConfigurationException(adbPath);
            }
            throw e;
        }
    }

    @VisibleForTesting
    AndroidDebugBridge createAndroidDebugBridge() {
        AndroidDebugBridge.initIfNeeded(false);
        if (hasAdbPathFilled()) {
            return AndroidDebugBridge.createBridge(adbPath, false);
        }
        return AndroidDebugBridge.createBridge();
    }

    private boolean hasAdbPathFilled() {
        return StringUtils.isNotEmpty(adbPath);
    }

    private boolean hasAdbProcFailed(IllegalArgumentException e) {
        return e.getCause() != null
                && e.getCause() instanceof IOException
                && e.getCause().getMessage().contains("Cannot run program")
                && e.getCause().getMessage().contains("adb");
    }
}
