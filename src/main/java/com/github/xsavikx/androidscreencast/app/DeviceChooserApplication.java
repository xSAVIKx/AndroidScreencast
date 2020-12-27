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

package com.github.xsavikx.androidscreencast.app;

import com.android.ddmlib.IDevice;
import com.github.xsavikx.androidscreencast.api.adb.AndroidDebugBridgeWrapper;
import com.github.xsavikx.androidscreencast.configuration.ApplicationConfiguration;
import com.github.xsavikx.androidscreencast.exception.NoDeviceChosenException;
import com.github.xsavikx.androidscreencast.exception.WaitDeviceListTimeoutException;
import com.github.xsavikx.androidscreencast.ui.JDialogDeviceList;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.github.xsavikx.androidscreencast.configuration.ApplicationConfigurationProperty.ADB_DEVICE_TIMEOUT;
import static org.slf4j.LoggerFactory.getLogger;

@Singleton
public final class DeviceChooserApplication extends SwingApplication {

    private static final long WAIT_TIMEOUT = 100;
    private final AndroidDebugBridgeWrapper bridge;
    private final long adbWaitSleepCyclesAmount;
    private IDevice device;

    @Inject
    public DeviceChooserApplication(final AndroidDebugBridgeWrapper bridge,
                                    final ApplicationConfiguration applicationConfiguration) {
        super(applicationConfiguration);
        this.bridge = bridge;
        this.adbWaitSleepCyclesAmount = getAdbDeviceTimeout() * 10;
    }

    private long getAdbDeviceTimeout() {
        return Long.parseLong(applicationConfiguration.getProperty(ADB_DEVICE_TIMEOUT));
    }

    @Override
    public void stop() {
        bridge.stop();
    }

    @Override
    public void start() {
        log().info("Starting application");

        waitDeviceList(bridge);

        final IDevice[] devices = bridge.getDevices();

        // Let the user choose the device
        if (devices.length == 1) {
            device = devices[0];
            log().info("1 device was found by ADB");
        } else {
            final JDialogDeviceList jd = new JDialogDeviceList(devices);
            jd.setVisible(true);
            device = jd.getDevice();
            log().info("{} devices were found by ADB", devices.length);
        }
        if (device == null) {
            throw new NoDeviceChosenException();
        }
        log().info("{} was chosen", device.getName());
    }

    private void waitDeviceList(final AndroidDebugBridgeWrapper bridge) {
        int count = 0;
        while (!bridge.hasInitialDeviceList()) {
            try {
                Thread.sleep(WAIT_TIMEOUT);
                count++;
            } catch (InterruptedException e) {
                log().warn("waitDeviceList(AndroidDebugBridge) - exception ignored", e);
            }
            if (count > adbWaitSleepCyclesAmount) {
                throw new WaitDeviceListTimeoutException();
            }
        }
    }

    public IDevice getDevice() {
        return device;
    }

    private enum LogSingleton {
        INSTANCE;

        @SuppressWarnings({"NonSerializableFieldInSerializableClass", "ImmutableEnumChecker"})
        private final Logger value = getLogger(DeviceChooserApplication.class);
    }

    private static Logger log() {
        return LogSingleton.INSTANCE.value;
    }
}
