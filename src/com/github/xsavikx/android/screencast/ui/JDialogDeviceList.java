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
import javax.swing.ListModel;

import com.android.ddmlib.IDevice;

public class JDialogDeviceList extends JDialog implements ActionListener {

	private static final String DEFAULT_HOST = "127.0.0.1";
	private static final int DEFAULT_PORT = 1324;

	JTextField jtfHost = new JTextField(DEFAULT_HOST);
	JFormattedTextField jftfPort = new JFormattedTextField(DEFAULT_PORT);
	JList<IDevice> jlDevices = new JList<IDevice>();
	JScrollPane jspDevices = new JScrollPane(jlDevices);
	JPanel jpAgent = new JPanel();
	JPanel jpButtons = new JPanel();
	JButton jbOk = new JButton("OK");
	JButton jbQuit = new JButton("Quit");

	boolean cancelled = false;
	IDevice[] devices;

	public JDialogDeviceList(IDevice[] devices) {
		super();
		setModal(true);
		this.devices = devices;
		initialize();
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
					ListModel dlm = jlDevices.getModel();
					Object item = dlm.getElementAt(index);
					;
					jlDevices.ensureIndexIsVisible(index);
					cancelled = false;
					setVisible(false);
				}
			}

		});
	}

	public IDevice getDevice() {
		if (cancelled)
			return null;
		return (IDevice) jlDevices.getSelectedValue();
	}

	public void actionPerformed(ActionEvent arg0) {
		cancelled = arg0.getSource() == jbQuit;

		setVisible(false);
	}
}
