package com.github.xsavikx.androidscreencast.app;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
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
    private final AndroidDebugBridge bridge;
    private final long adbWaitSleepCyclesAmount;
    @Value("${adb.device.timeout:30}")
    private long adbDeviceTimeout;
    private IDevice device;

    @Autowired
    public DeviceChooserApplication(AndroidDebugBridge bridge) {
        this.bridge = bridge;
        this.adbWaitSleepCyclesAmount = adbDeviceTimeout * 10;
    }

    @Override
    public void stop() {
        // ignore
    }

    @Override
    public void start() {
        LOGGER.debug("start() - start");
        initialize();
        LOGGER.debug("start() - end");
    }

    private void waitDeviceList(AndroidDebugBridge bridge) {
        LOGGER.debug("waitDeviceList(AndroidDebugBridge bridge=" + bridge + ") - start");

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

        LOGGER.debug("waitDeviceList(AndroidDebugBridge bridge=" + bridge + ") - end");
    }

    private void initialize() {
        LOGGER.debug("initialize() - start");

        waitDeviceList(bridge);

        IDevice devices[] = bridge.getDevices();
        // Let the user choose the device
        if (devices.length == 1) {
            device = devices[0];
        } else {
            JDialogDeviceList jd = new JDialogDeviceList(devices);
            jd.setVisible(true);

            device = jd.getDevice();
            if (device == null) {
                return;
            }
        }

        if (device == null) {
            throw new NoDeviceChosenException();
        }
        LOGGER.debug("initialize() - end");
    }

    public IDevice getDevice() {
        return device;
    }
}
