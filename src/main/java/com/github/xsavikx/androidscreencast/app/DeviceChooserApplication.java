package com.github.xsavikx.androidscreencast.app;

import com.android.ddmlib.IDevice;
import com.github.xsavikx.androidscreencast.api.adb.AndroidDebugBridgeWrapper;
import com.github.xsavikx.androidscreencast.configuration.ApplicationConfiguration;
import com.github.xsavikx.androidscreencast.exception.NoDeviceChosenException;
import com.github.xsavikx.androidscreencast.exception.WaitDeviceListTimeoutException;
import com.github.xsavikx.androidscreencast.ui.JDialogDeviceList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.github.xsavikx.androidscreencast.configuration.ApplicationConfigurationProperty.ADB_DEVICE_TIMEOUT;

@Singleton
public class DeviceChooserApplication extends SwingApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceChooserApplication.class);
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
        return Long.valueOf(applicationConfiguration.getProperty(ADB_DEVICE_TIMEOUT));
    }

    @Override
    public void stop() {
        // ignore
    }

    @Override
    public void start() {
        LOGGER.info("Starting application");
        try {

            waitDeviceList(bridge);

            final IDevice devices[] = bridge.getDevices();

            // Let the user choose the device
            if (devices.length == 1) {
                device = devices[0];
                LOGGER.info("1 device was found by ADB");
            } else {
                final JDialogDeviceList jd = new JDialogDeviceList(devices);
                jd.setVisible(true);
                device = jd.getDevice();
                LOGGER.info("{} devices were found by ADB", devices.length);
            }

            if (device == null) {
                throw new NoDeviceChosenException();
            }
        } catch (final Throwable e) {
            bridge.stop();
            throw e;
        }
        LOGGER.info("{} was chosen", device.getName());
    }

    private void waitDeviceList(final AndroidDebugBridgeWrapper bridge) {
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
