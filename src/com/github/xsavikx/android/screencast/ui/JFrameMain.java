package com.github.xsavikx.android.screencast.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.android.ddmlib.IDevice;
import com.github.xsavikx.android.screencast.api.AndroidDeviceImpl;
import com.github.xsavikx.android.screencast.api.command.executor.CommandExecutor;
import com.github.xsavikx.android.screencast.api.injector.ConstEvtKey;
import com.github.xsavikx.android.screencast.api.injector.Injector;
import com.github.xsavikx.android.screencast.api.injector.ScreenCaptureThread.ScreenCaptureListener;
import com.github.xsavikx.android.screencast.ui.explorer.JFrameExplorer;
import com.github.xsavikx.android.screencast.ui.interaction.KeyEventDispatcherFactory;
import com.github.xsavikx.android.screencast.ui.interaction.KeyboardActionListenerFactory;
import com.github.xsavikx.android.screencast.ui.interaction.MouseActionAdapterFactory;

public class JFrameMain extends JFrame {

  private static final long serialVersionUID = -2085909236767692371L;
  private JPanelScreen jp = new JPanelScreen();
  private JToolBar jtb = new JToolBar();
  private JToolBar jtbHardkeys = new JToolBar();
  private JToggleButton jtbRecord = new JToggleButton("Record");

  private JButton jbOpenUrl = new JButton("Open Url");
  private JScrollPane jsp;
  private JButton jbExplorer = new JButton("Explore");
  private JButton jbRestartClient = new JButton("Restart client");
  private JButton jbKbHome = new JButton("Home");
  private JButton jbKbMenu = new JButton("Menu");
  private JButton jbKbBack = new JButton("Back");
  private JButton jbKbSearch = new JButton("Search");

  private JButton jbKbPhoneOn = new JButton("Call");

  private JButton jbKbPhoneOff = new JButton("End call");
  private IDevice device;
  private CommandExecutor commandExecutor;
  private Injector injector;
  private Dimension oldImageDimension = null;

  public JFrameMain(IDevice device) {
    this.device = device;
    initialize();
    KeyboardFocusManager.getCurrentKeyboardFocusManager()
        .addKeyEventDispatcher(KeyEventDispatcherFactory.getKeyEventDispatcher(this));
  }

  public void initialize() {
    jtb.setFocusable(false);
    jbExplorer.setFocusable(false);
    jtbRecord.setFocusable(false);
    jbOpenUrl.setFocusable(false);
    jbKbHome.setFocusable(false);
    jbKbMenu.setFocusable(false);
    jbKbBack.setFocusable(false);
    jbKbSearch.setFocusable(false);
    jbKbPhoneOn.setFocusable(false);
    jbKbPhoneOff.setFocusable(false);
    jbRestartClient.setFocusable(false);

    jbKbHome.addActionListener(KeyboardActionListenerFactory.getInstance(ConstEvtKey.KEYCODE_HOME));
    jbKbMenu.addActionListener(KeyboardActionListenerFactory.getInstance(ConstEvtKey.KEYCODE_MENU));
    jbKbBack.addActionListener(KeyboardActionListenerFactory.getInstance(ConstEvtKey.KEYCODE_BACK));
    jbKbSearch.addActionListener(KeyboardActionListenerFactory.getInstance(ConstEvtKey.KEYCODE_SEARCH));
    jbKbPhoneOn.addActionListener(KeyboardActionListenerFactory.getInstance(ConstEvtKey.KEYCODE_CALL));
    jbKbPhoneOff.addActionListener(KeyboardActionListenerFactory.getInstance(ConstEvtKey.KEYCODE_ENDCALL));

    jtbHardkeys.add(jbKbHome);
    jtbHardkeys.add(jbKbMenu);
    jtbHardkeys.add(jbKbBack);
    jtbHardkeys.add(jbKbSearch);
    jtbHardkeys.add(jbKbPhoneOn);
    jtbHardkeys.add(jbKbPhoneOff);

    // setIconImage(Toolkit.getDefaultToolkit().getImage(
    // getClass().getResource("icon.png")));
    setDefaultCloseOperation(3);
    setLayout(new BorderLayout());
    add(jtb, BorderLayout.NORTH);
    add(jtbHardkeys, BorderLayout.SOUTH);
    jsp = new JScrollPane(jp);
    add(jsp, BorderLayout.CENTER);
    jsp.setPreferredSize(new Dimension(100, 100));
    pack();
    setLocationRelativeTo(null);

    MouseAdapter ma = MouseActionAdapterFactory.getInstance(jp);

    jp.addMouseMotionListener(ma);
    jp.addMouseListener(ma);
    jp.addMouseWheelListener(ma);

    jtbRecord.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        if (jtbRecord.isSelected()) {
          startRecording();
        } else {
          stopRecording();
        }
      }

    });
    jtb.add(jtbRecord);

    jbExplorer.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        JFrameExplorer jf = new JFrameExplorer(device);
        jf.setIconImage(getIconImage());
        jf.setVisible(true);
      }
    });
    jtb.add(jbExplorer);

    jtb.add(jbRestartClient);

    jbOpenUrl.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        JDialogUrl jdUrl = new JDialogUrl();
        jdUrl.setVisible(true);
        if (!jdUrl.isResult())
          return;
        String url = jdUrl.getJtfUrl().getText();
        new AndroidDeviceImpl(device).openUrl(url);
      }
    });
    jtb.add(jbOpenUrl);

  }

  public void setInjector(Injector injector) {
    this.injector = injector;
    injector.screencapture.setListener(new ScreenCaptureListener() {

      @Override
      public void handleNewImage(Dimension size, BufferedImage image, boolean landscape) {
        if (oldImageDimension == null || !size.equals(oldImageDimension)) {
          jsp.setPreferredSize(size);
          JFrameMain.this.pack();
          oldImageDimension = size;
        }
        jp.handleNewImage(size, image, landscape);
      }
    });
  }

  private void startRecording() {
    JFileChooser jFileChooser = new JFileChooser();
    FileNameExtensionFilter filter = new FileNameExtensionFilter("Video file", "mov");
    jFileChooser.setFileFilter(filter);
    int returnVal = jFileChooser.showSaveDialog(this);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      injector.screencapture.startRecording(jFileChooser.getSelectedFile());
    }
  }

  private void stopRecording() {
    injector.screencapture.stopRecording();
  }

  public CommandExecutor getCommandExecutor() {
    return commandExecutor;
  }

  public void setCommandExecutor(CommandExecutor commandExecutor) {
    this.commandExecutor = commandExecutor;
  }

}
