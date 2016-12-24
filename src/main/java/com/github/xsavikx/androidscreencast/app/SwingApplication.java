package com.github.xsavikx.androidscreencast.app;

import com.github.xsavikx.androidscreencast.ui.JDialogError;

import javax.swing.*;
import java.io.PrintWriter;
import java.io.StringWriter;

public abstract class SwingApplication extends GUIApplication {
    private JDialogError jd = null;

    protected abstract boolean isNativeLook();

    @Override
    public void init() {
        try {
            if (isNativeLook())
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
