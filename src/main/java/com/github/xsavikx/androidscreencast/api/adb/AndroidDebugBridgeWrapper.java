package com.github.xsavikx.androidscreencast.api.adb;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.github.xsavikx.androidscreencast.exception.IllegalAdbConfigurationException;
import com.github.xsavikx.androidscreencast.util.StringUtils;

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
        AndroidDebugBridge.disconnectBridge();
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
            AndroidDebugBridge.initIfNeeded(false);
            if (StringUtils.isNotEmpty(adbPath)) {
                adb = AndroidDebugBridge.createBridge(adbPath, false);
            } else {
                adb = AndroidDebugBridge.createBridge();
            }
        } catch (IllegalArgumentException e) {
            if (hasAdbProcFailed(e)) {
                throw new IllegalAdbConfigurationException(adbPath);
            }
            throw e;
        }
    }

    private boolean hasAdbProcFailed(IllegalArgumentException e) {
        return e.getCause() != null
                && e.getCause() instanceof IOException
                && e.getCause().getMessage().contains("Cannot run program")
                && e.getCause().getMessage().contains("adb");

    }
}
