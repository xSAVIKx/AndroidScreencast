package com.github.xsavikx.androidscreencast.app;

import com.github.xsavikx.androidscreencast.configuration.ApplicationConfiguration;
import com.github.xsavikx.androidscreencast.exception.AndroidScreenCastRuntimeException;
import com.github.xsavikx.androidscreencast.ui.JDialogError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.PrintWriter;
import java.io.StringWriter;

import static com.github.xsavikx.androidscreencast.configuration.ApplicationConfigurationProperty.APP_NATIVE_LOOK;

public abstract class SwingApplication extends GUIApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceChooserApplication.class);
    private static final String SYNC_TREE_UI = "SynthTreeUI";
    protected final ApplicationConfiguration applicationConfiguration;
    private JDialogError jd = null;

    protected SwingApplication(final ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
    }

    private boolean useNativeLook() {
        return Boolean.valueOf(applicationConfiguration.getProperty(APP_NATIVE_LOOK));
    }

    @Override
    public void init() {
        try {
            if (useNativeLook())
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            throw new AndroidScreenCastRuntimeException(ex);
        }
    }

    @Override
    public void handleException(final Thread thread, final Throwable ex) {
        try {
            final StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            if (sw.toString().contains(SYNC_TREE_UI))
                return;
            LOGGER.error(ex.getClass().getSimpleName(), ex);
            if (jd != null && jd.isVisible())
                return;
            jd = new JDialogError(ex);
            SwingUtilities.invokeLater(() -> {
                jd.setVisible(true);
            });
        } catch (Exception e) {
            LOGGER.warn("Exception occurred during exception handling.", e);
        }
    }

}
