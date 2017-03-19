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
