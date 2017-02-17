package com.github.xsavikx.androidscreencast.app;

import com.android.ddmlib.IDevice;
import com.github.xsavikx.androidscreencast.api.adb.AndroidDebugBridgeWrapper;
import com.github.xsavikx.androidscreencast.exception.NoDeviceChosenException;
import com.github.xsavikx.androidscreencast.exception.WaitDeviceListTimeoutException;
import com.github.xsavikx.androidscreencast.ui.JDialogDeviceList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DeviceChooserApplication extends SwingApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceChooserApplication.class);
    private static final long WAIT_TIMEOUT = 100;
    private final AndroidDebugBridgeWrapper bridge;
    private final long adbWaitSleepCyclesAmount;
    @Value("${adb.device.timeout:30}")
    private long adbDeviceTimeout;
    private IDevice device;

    @Autowired
    public DeviceChooserApplication(AndroidDebugBridgeWrapper bridge) {
        this.bridge = bridge;
        this.adbWaitSleepCyclesAmount = adbDeviceTimeout * 10;
    }

    @Override
    public void stop() {
        // ignore
    }

    @Override
    public void start() {
        LOGGER.info("Starting application");

        waitDeviceList(bridge);

        IDevice devices[] = bridge.getDevices();

        // Let the user choose the device
        if (devices.length == 1) {
            device = devices[0];
            LOGGER.info("1 device was found by ADB");
        } else {
            JDialogDeviceList jd = new JDialogDeviceList(devices);
            jd.setVisible(true);
            device = jd.getDevice();
            LOGGER.info("{} devices were found by ADB", devices.length);
        }

        if (device == null) {
            throw new NoDeviceChosenException();
        }
        LOGGER.info("{} was chosen", device.getName());
    }

    private void waitDeviceList(AndroidDebugBridgeWrapper bridge) {
        int count = 0;
        while (!bridge.hasInitialDeviceList()) {
            try {
                Thread.sleep(WAIT_TIMEOUT);
                count++;
            } catch (InterruptedException e) {
                LOGGER.warn("waitDeviceList(AndroidDebugBridge) - exception ignored", e);
            }
            if (count > adbWaitSleepCyclesAmount) {
                throw new WaitDeviceListTimeoutException();
            }
        }
    }

    public IDevice getDevice() {
        return device;
    }
}
