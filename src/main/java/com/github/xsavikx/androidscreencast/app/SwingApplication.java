package com.github.xsavikx.androidscreencast.app;

import com.github.xsavikx.androidscreencast.exception.AndroidScreenCastRuntimeException;
import com.github.xsavikx.androidscreencast.ui.JDialogError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.swing.*;
import java.io.PrintWriter;
import java.io.StringWriter;

public abstract class SwingApplication extends GUIApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceChooserApplication.class);
    private JDialogError jd = null;
    @Value("${app.native.look:true}")
    private boolean nativeLook;

    private boolean useNativeLook() {
        return nativeLook;
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
    public void handleException(Thread thread, Throwable ex) {
        try {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            if (sw.toString().contains("SynthTreeUI"))
                return;
            LOGGER.error(ex.getClass().getSimpleName(), ex);
            if (jd != null && jd.isVisible())
                return;
            jd = new JDialogError(ex);
            SwingUtilities.invokeLater(() -> jd.setVisible(true));
        } catch (Exception ignored) {
            // ignored
        }
    }

}
