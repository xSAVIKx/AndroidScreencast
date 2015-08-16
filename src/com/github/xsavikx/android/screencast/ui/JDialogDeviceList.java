package com.github.xsavikx.android.screencast.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.android.ddmlib.IDevice;

public class JDialogDeviceList extends JDialog implements ActionListener {

  private static final long serialVersionUID = -3719844308147203239L;
  private static final String DEFAULT_HOST = "127.0.0.1";
  private static final int DEFAULT_PORT = 1324;

  private JTextField jtfHost = new JTextField(DEFAULT_HOST);
  private JFormattedTextField jftfPort = new JFormattedTextField(DEFAULT_PORT);
  private JList<IDevice> jlDevices = new JList<IDevice>();
  private JScrollPane jspDevices = new JScrollPane(jlDevices);
  private JPanel jpAgent = new JPanel();
  private JPanel jpButtons = new JPanel();
  private JButton jbOk = new JButton("OK");
  private JButton jbQuit = new JButton("Quit");

  private boolean cancelled = false;
  private IDevice[] devices;

  public JDialogDeviceList(IDevice[] devices) {
    super();
    setModal(true);
    this.devices = devices;
    initialize();
  }

  @Override
  public void actionPerformed(ActionEvent arg0) {
    cancelled = arg0.getSource() == jbQuit;

    setVisible(false);
  }

  public IDevice getDevice() {
    if (cancelled)
      return null;
    return jlDevices.getSelectedValue();
  }

  private void initialize() {
    setTitle("Please select a device");
    jlDevices.setListData(devices);
    jlDevices.setPreferredSize(new Dimension(400, 300));
    if (devices.length != 0)
      jlDevices.setSelectedIndex(0);
    jbOk.setEnabled(!jlDevices.isSelectionEmpty());

    jpAgent.setBorder(BorderFactory.createTitledBorder("Agent"));
    jpAgent.setLayout(new BorderLayout(10, 10));
    jpAgent.add(jtfHost, BorderLayout.CENTER);
    jpAgent.add(jftfPort, BorderLayout.EAST);

    jpButtons.setLayout(new FlowLayout(FlowLayout.RIGHT));
    jpButtons.add(jbOk, BorderLayout.CENTER);
    jpButtons.add(jbQuit, BorderLayout.SOUTH);

    JPanel jpBottom = new JPanel();
    jpBottom.setLayout(new BorderLayout());
    jpBottom.add(jpAgent, BorderLayout.CENTER);
    jpBottom.add(jpButtons, BorderLayout.SOUTH);

    setLayout(new BorderLayout());
    add(jlDevices, BorderLayout.CENTER);
    add(jpBottom, BorderLayout.SOUTH);

    pack();
    setLocationRelativeTo(null);

    jbOk.addActionListener(this);
    jbQuit.addActionListener(this);
    jlDevices.addMouseListener(new MouseAdapter() {

      @Override
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
          int index = jlDevices.locationToIndex(e.getPoint());
          jlDevices.ensureIndexIsVisible(index);
          cancelled = false;
          setVisible(false);
        }
      }

    });
  }
}
