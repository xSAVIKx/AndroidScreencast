package com.github.xsavikx.androidscreencast.api.adb;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.github.xsavikx.androidscreencast.exception.IllegalAdbConfigurationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PreDestroy;
import java.io.IOException;

@Service
public class AndroidDebugBridgeWrapper {
    @Value("${adb.path:}")
    private String adbPath;
    private AndroidDebugBridge adb;

    public IDevice[] getDevices() {
        init();
        return adb.getDevices();
    }

    public boolean hasInitialDeviceList() {
        init();
        return adb.hasInitialDeviceList();
    }


    @PreDestroy
    void cleanUp() {
        AndroidDebugBridge.disconnectBridge();
        AndroidDebugBridge.terminate();
    }

    private void init() {
        if (adb != null) {
            return;
        }
        try {
            AndroidDebugBridge.initIfNeeded(false);
            if (!StringUtils.isEmpty(adbPath)) {
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
