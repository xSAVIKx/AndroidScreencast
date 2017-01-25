package com.github.xsavikx.androidscreencast.app;

import com.github.xsavikx.androidscreencast.ui.JDialogError;
import org.springframework.beans.factory.annotation.Value;

import javax.swing.*;
import java.io.PrintWriter;
import java.io.StringWriter;

public abstract class SwingApplication extends GUIApplication {
    private JDialogError jd = null;
    @Value("${app.native.look:true")
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
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void handleException(Thread thread, Throwable ex) {
        try {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            if (sw.toString().contains("SynthTreeUI"))
                return;
            ex.printStackTrace(System.err);
            if (jd != null && jd.isVisible())
                return;
            jd = new JDialogError(ex);
            SwingUtilities.invokeLater(() -> jd.setVisible(true));
        } catch (Exception ex2) {
            // ignored
        }
    }

}
