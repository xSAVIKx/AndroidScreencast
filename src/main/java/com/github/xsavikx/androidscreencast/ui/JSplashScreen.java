package com.github.xsavikx.androidscreencast.ui;

import javax.swing.*;
import java.awt.*;

public class JSplashScreen extends JWindow {

    private static final long serialVersionUID = -4537199368044671301L;
    private JLabel label;

    public JSplashScreen(String text) {
        label = new JLabel("Loading...", SwingConstants.CENTER);
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
