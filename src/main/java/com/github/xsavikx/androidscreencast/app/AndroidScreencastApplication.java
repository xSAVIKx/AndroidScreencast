package com.github.xsavikx.androidscreencast.app;

import com.android.ddmlib.IDevice;
import com.github.xsavikx.androidscreencast.api.adb.AndroidDebugBridgeWrapper;
import com.github.xsavikx.androidscreencast.api.injector.Injector;
import com.github.xsavikx.androidscreencast.configuration.ApplicationConfiguration;
import com.github.xsavikx.androidscreencast.ui.JFrameMain;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.*;
import java.awt.*;

import static org.slf4j.LoggerFactory.getLogger;

@Singleton
public final class AndroidScreencastApplication extends SwingApplication {

    private final JFrameMain jFrameMain;
    private final Injector injector;
    private final IDevice iDevice;
    private final AndroidDebugBridgeWrapper wrapper;
    private transient boolean isStopped = false;

    @Inject
    public AndroidScreencastApplication(final Injector injector, final IDevice iDevice, final JFrameMain jFrameMain,
                                        final ApplicationConfiguration applicationConfiguration, AndroidDebugBridgeWrapper wrapper) {
        super(applicationConfiguration);
        this.injector = injector;
        this.iDevice = iDevice;
        this.jFrameMain = jFrameMain;
        this.wrapper = wrapper;
    }

    @Override
    public void stop() {
        log().info("Stopping application.");
        if (isStopped) {
            log().warn("Application is already stopped.");
            return;
        }
        injector.stop();
        wrapper.stop();
        for (final Frame frame : Frame.getFrames()) {
            frame.dispose();
        }
        isStopped = true;
    }

    @Override
    public void start() {
        log().info("Starting application.");
        if (iDevice == null) {
            log().warn("No valid device was chosen. Please try to chose correct one.");
            stop();
        }
        SwingUtilities.invokeLater(() -> {
            jFrameMain.initialize();
            // Start showing the iDevice screen
            jFrameMain.setTitle(iDevice.getSerialNumber());
            // Show window
            jFrameMain.setVisible(true);

            jFrameMain.launchInjector();
        });
    }

    private enum LogSingleton {
        INSTANCE;

        @SuppressWarnings({"NonSerializableFieldInSerializableClass", "ImmutableEnumChecker"})
        private final Logger value = getLogger(AndroidScreencastApplication.class);
    }

    private static Logger log() {
        return LogSingleton.INSTANCE.value;
    }
}
