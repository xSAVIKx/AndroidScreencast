package com.github.xsavikx.android.screencast.ui;

import java.awt.BorderLayout;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JDialog;
import javax.swing.JTextArea;

public class JDialogError extends JDialog {

  private static final long serialVersionUID = -2562084286663149628L;

  public JDialogError(Throwable ex) {
    getRootPane().setLayout(new BorderLayout());
    JTextArea l = new JTextArea();
    StringWriter w = new StringWriter();
    if (ex.getClass() == RuntimeException.class && ex.getCause() != null)
      ex = ex.getCause();
    ex.printStackTrace(new PrintWriter(w));
    l.setText(w.toString());
    getRootPane().add(l, BorderLayout.CENTER);
    pack();
    setLocationRelativeTo(null);
    setAlwaysOnTop(true);
    setAlwaysOnTop(false);
  }

}
