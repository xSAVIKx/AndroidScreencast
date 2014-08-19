package com.github.xsavikx.android.screencast.ui;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JWindow;

public class JSplashScreen extends JWindow {
	JLabel label;

	public JSplashScreen(String text) {
		label = new JLabel("Loading...", (int) JLabel.CENTER_ALIGNMENT);
		initialize();
		setText(text);
	}

	public void setText(String text) {
		label.setText(text);
		pack();
		setLocationRelativeTo(null);
	}

	private void initialize() {
		setLayout(new BorderLayout());
		label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		// createLineBorder(Color.BLACK));
		add(label, BorderLayout.CENTER);
	}

}
