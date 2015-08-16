package com.github.xsavikx.android.screencast.ui;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JWindow;

public class JSplashScreen extends JWindow {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private JLabel label;

  public JSplashScreen(String text) {
    label = new JLabel("Loading...", (int) Component.CENTER_ALIGNMENT);
    initialize();
    setText(text);
  }

  private void initialize() {
    setLayout(new BorderLayout());
    label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    // createLineBorder(Color.BLACK));
    add(label, BorderLayout.CENTER);
  }

  public void setText(String text) {
    label.setText(text);
    pack();
    setLocationRelativeTo(null);
  }

}
