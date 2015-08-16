package com.github.xsavikx.android.screencast.app;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.github.xsavikx.android.screencast.ui.JDialogError;

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
      SwingUtilities.invokeLater(new Runnable() {

        @Override
        public void run() {
          jd.setVisible(true);

        }
      });
    } catch (Exception ex2) {
      // ignored
    }
  }

}
