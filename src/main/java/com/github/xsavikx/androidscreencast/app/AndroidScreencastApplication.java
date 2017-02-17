package com.github.xsavikx.androidscreencast.app;

import com.android.ddmlib.IDevice;
import com.github.xsavikx.androidscreencast.api.injector.Injector;
import com.github.xsavikx.androidscreencast.ui.JFrameMain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.swing.*;
import java.awt.*;

@Component
public class AndroidScreencastApplication extends SwingApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(AndroidScreencastApplication.class);
    private final JFrameMain jFrameMain;
    private final Injector injector;
    private final IDevice iDevice;
    @Value("${adb.path:}")
    private String adbPath;
    private transient boolean isStopped = false;

    @Autowired
    public AndroidScreencastApplication(Injector injector, IDevice iDevice, JFrameMain jFrameMain) {
        this.injector = injector;
        this.iDevice = iDevice;
        this.jFrameMain = jFrameMain;
    }

    @Override
    public void stop() {
        LOGGER.info("Stopping application");
        if (isStopped) {
            LOGGER.debug("Application is already stopped.");
            return;
        }
        if (injector != null)
            injector.stop();
        for (Frame frame : Frame.getFrames()) {
            frame.dispose();
        }
        isStopped = true;
    }

    @Override
    public void start() {
        LOGGER.info("Starting application");
        if (iDevice == null) {
            LOGGER.warn("No valid device was chosen. Please try to chose correct one.");
            stop();
        }
        SwingUtilities.invokeLater(() -> {
            // Start showing the iDevice screen
            jFrameMain.setTitle("" + iDevice);

            // Show window
            jFrameMain.setVisible(true);

            jFrameMain.launchInjector();
        });
    }


    private boolean hasFilledAdbPath() {
        return !StringUtils.isEmpty(adbPath);
    }
}
