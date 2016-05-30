package com.github.xsavikx.androidscreencast.app;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.github.xsavikx.androidscreencast.api.injector.Injector;
import com.github.xsavikx.androidscreencast.constant.Constants;
import com.github.xsavikx.androidscreencast.ui.JFrameMain;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.swing.*;

@Component
public class AndroidScreencastApplication extends SwingApplication {
    private static final Logger LOGGER = Logger.getLogger(AndroidScreencastApplication.class);

    private final Environment environment;
    private final JFrameMain jFrameMain;
    private final Injector injector;
    private final IDevice iDevice;

    @Autowired
    public AndroidScreencastApplication(Injector injector, IDevice iDevice, JFrameMain jFrameMain, Environment environment) {
        this.injector = injector;
        this.iDevice = iDevice;
        this.jFrameMain = jFrameMain;
        this.environment = environment;
    }

    @Override
    public void close() {
        LOGGER.debug("close() - start");

        if (injector != null)
            injector.close();

        if (iDevice != null) {
            synchronized (iDevice) {
                if (hasFilledAdbPath())
                    AndroidDebugBridge.disconnectBridge();
                AndroidDebugBridge.terminate();
            }
        }

        LOGGER.debug("close() - end");
    }

    @Override
    public void start() {
        LOGGER.debug("start() - start");
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Start showing the iDevice screen
                jFrameMain.setTitle("" + iDevice);

                // Show window
                jFrameMain.setVisible(true);

                jFrameMain.launchInjector();
            }
        });
        LOGGER.debug("start() - end");
    }

    @SuppressWarnings("boxing")
    @Override
    protected boolean isNativeLook() {
        LOGGER.debug("isNativeLook() - start");

        boolean useNativeLook = environment.getProperty(Constants.APP_NATIVE_LOOK_PROPERTY, Boolean.class,
                Constants.DEFAULT_APP_NATIVE_LOOK);
        LOGGER.debug("isNativeLook() - end");
        return useNativeLook;
    }

    private boolean hasFilledAdbPath() {
        LOGGER.debug("hasFilledAdbPath() - start");

        boolean hasAdbPath = environment.getProperty(Constants.ADB_PATH_PROPERTY) != null;
        LOGGER.debug("hasFilledAdbPath() - end");
        return hasAdbPath;
    }
}
