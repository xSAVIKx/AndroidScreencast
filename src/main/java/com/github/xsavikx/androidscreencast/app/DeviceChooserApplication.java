package com.github.xsavikx.androidscreencast.app;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.github.xsavikx.androidscreencast.constant.Constants;
import com.github.xsavikx.androidscreencast.exception.NoDeviceChosenException;
import com.github.xsavikx.androidscreencast.exception.WaitDeviceListTimeoutException;
import com.github.xsavikx.androidscreencast.ui.JDialogDeviceList;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class DeviceChooserApplication extends SwingApplication {
    private static final Logger LOGGER = Logger.getLogger(DeviceChooserApplication.class);

    private final Environment env;
    private final AndroidDebugBridge bridge;

    private IDevice device;

    @Autowired
    public DeviceChooserApplication(Environment env, AndroidDebugBridge bridge) {
        this.env = env;
        this.bridge = bridge;
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

    @SuppressWarnings("boxing")
    @Override
    protected boolean isNativeLook() {
        LOGGER.debug("isNativeLook() - start");

        boolean nativeLook = env.getProperty(Constants.APP_NATIVE_LOOK_PROPERTY, Boolean.class,
                Constants.DEFAULT_APP_NATIVE_LOOK);
        LOGGER.debug("isNativeLook() - end");
        return nativeLook;
    }

    private void waitDeviceList(AndroidDebugBridge bridge) {
        LOGGER.debug("waitDeviceList(AndroidDebugBridge bridge=" + bridge + ") - start");

        int count = 0;
        while (!bridge.hasInitialDeviceList()) {
            try {
                Thread.sleep(100);
                count++;
            } catch (InterruptedException e) {
                LOGGER.warn("waitDeviceList(AndroidDebugBridge) - exception ignored", e);

            }
            // let's not wait > 10 sec.
            if (count > 300) {
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
