package com.github.xsavikx.android.screencast.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTextField;

public class JDialogUrl extends JDialog {

  private static final long serialVersionUID = -331017582679776599L;
  private JTextField jtfUrl = new JTextField();

  public JTextField getJtfUrl() {
    return jtfUrl;
  }

  public void setJtfUrl(JTextField jtfUrl) {
    this.jtfUrl = jtfUrl;
  }

  private JButton jbOk = new JButton("Ok");
  private boolean result = false;

  public JDialogUrl() {
    setModal(true);
    setTitle("Open url");

    setLayout(new BorderLayout());
    add(jbOk, BorderLayout.SOUTH);
    add(jtfUrl, BorderLayout.CENTER);
    jtfUrl.setColumns(50);

    jbOk.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        setResult(true);
        JDialogUrl.this.setVisible(false);
      }
    });

    jbOk.setDefaultCapable(true);
    getRootPane().setDefaultButton(jbOk);
    pack();
    setLocationRelativeTo(null);

  }

  public boolean isResult() {
    return result;
  }

  public void setResult(boolean result) {
    this.result = result;
  }
}
