package com.github.xsavikx.android.screencast.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTextField;

public class JDialogUrl extends JDialog {

	JTextField jtfUrl = new JTextField();
	JButton jbOk = new JButton("Ok");
	boolean result = false;
	
	public JDialogUrl() {
		setModal(true);
		setTitle("Open url");
		
		setLayout(new BorderLayout());
		add(jbOk,BorderLayout.SOUTH);
		add(jtfUrl,BorderLayout.CENTER);
		jtfUrl.setColumns(50);
		
		jbOk.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				result = true;
				JDialogUrl.this.setVisible(false);
			}
		});
		
		jbOk.setDefaultCapable(true);
		getRootPane().setDefaultButton(jbOk);
		pack();
		setLocationRelativeTo(null);
		
	}
}
