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
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.github.xsavikx.android.screencast.api.AndroidDevice;
import com.github.xsavikx.android.screencast.api.injector.Injector;
import com.github.xsavikx.android.screencast.api.injector.InputKeyEvent;
import com.github.xsavikx.android.screencast.api.injector.ScreenCaptureThread.ScreenCaptureListener;
import com.github.xsavikx.android.screencast.constant.Constants;
import com.github.xsavikx.android.screencast.spring.config.ApplicationContextProvider;
import com.github.xsavikx.android.screencast.ui.explorer.JFrameExplorer;
import com.github.xsavikx.android.screencast.ui.interaction.KeyEventDispatcherFactory;
import com.github.xsavikx.android.screencast.ui.interaction.KeyboardActionListenerFactory;
import com.github.xsavikx.android.screencast.ui.interaction.MouseActionAdapterFactory;

@Component
public class JFrameMain extends JFrame {

  private static final long serialVersionUID = -2085909236767692371L;
  private JPanelScreen jp = new JPanelScreen();
  private JToolBar jtb = new JToolBar();
  private JToolBar jtbHardkeys = new JToolBar();
  private JToggleButton jtbRecord = new JToggleButton("Record");

  private JButton jbOpenUrl = new JButton("Open_Url");
  private JScrollPane jsp;
  private JButton jbExplorer = new JButton("Explore");
  private JButton jbRestartClient = new JButton("Restart_Client");
  private JButton jbKbHome = new JButton("Home");
  private JButton jbKbMenu = new JButton("Menu");
  private JButton jbKbBack = new JButton("Back");
  private JButton jbKbSearch = new JButton("Search");

  private JButton jbKbPhoneOn = new JButton("Call");

  private JButton jbKbPhoneOff = new JButton("End_Call");
  private JButton jbKbPower = new JButton("Power");
  private AndroidDevice androidDevice;
  private Injector injector;
  private Environment env;
  private Dimension oldImageDimension = null;

  @Autowired
  public JFrameMain(Environment env, Injector injector, AndroidDevice androidDevice) {
    this.injector = injector;
    this.env = env;
    this.androidDevice = androidDevice;
    initialize();
    KeyboardFocusManager.getCurrentKeyboardFocusManager()
        .addKeyEventDispatcher(KeyEventDispatcherFactory.getKeyEventDispatcher(this));
  }

  private boolean useCustomWindowSize() {
    if (env.getProperty(Constants.CUSTOM_WINDOW_SIZE) != null &&
            (env.getProperty(Constants.CUSTOM_WINDOW_SIZE)).equals("true") == true) {
	  boolean useCustomWindowSize = env.getProperty(Constants.CUSTOM_WINDOW_SIZE, Boolean.class);
      return useCustomWindowSize;
	} else {
      boolean useCustomWindowSize = false;
      return useCustomWindowSize;
	}
  }

  private void setPrefferedWindowSize() {
    if (env.containsProperty(Constants.DEFAULT_WINDOW_HEIGHT) && env.containsProperty(Constants.DEFAULT_WINDOW_WIDTH)) {
      int height = env.getProperty(Constants.DEFAULT_WINDOW_HEIGHT, Integer.class).intValue();
      int width = env.getProperty(Constants.DEFAULT_WINDOW_WIDTH, Integer.class).intValue();
      getContentPane().setPreferredSize(new Dimension(width, height));
    }
    pack();
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
    jbKbPower.setFocusable(false);

    jbKbHome.addActionListener(KeyboardActionListenerFactory.getInstance(InputKeyEvent.KEYCODE_HOME));
    jbKbMenu.addActionListener(KeyboardActionListenerFactory.getInstance(InputKeyEvent.KEYCODE_MENU));
    jbKbBack.addActionListener(KeyboardActionListenerFactory.getInstance(InputKeyEvent.KEYCODE_BACK));
    jbKbSearch.addActionListener(KeyboardActionListenerFactory.getInstance(InputKeyEvent.KEYCODE_SEARCH));
    jbKbPhoneOn.addActionListener(KeyboardActionListenerFactory.getInstance(InputKeyEvent.KEYCODE_CALL));
    jbKbPhoneOff.addActionListener(KeyboardActionListenerFactory.getInstance(InputKeyEvent.KEYCODE_ENDCALL));
    jbKbPower.addActionListener(KeyboardActionListenerFactory.getInstance(InputKeyEvent.KEYCODE_POWER));

    jtbHardkeys.add(jbKbHome);
    jtbHardkeys.add(jbKbMenu);
    jtbHardkeys.add(jbKbBack);
    jtbHardkeys.add(jbKbSearch);
    jtbHardkeys.add(jbKbPhoneOn);
    jtbHardkeys.add(jbKbPhoneOff);
    jtbHardkeys.add(jbKbPower);

    // setIconImage(Toolkit.getDefaultToolkit().getImage(
    // getClass().getResource("icon.png")));
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
        JFrameExplorer jf = ApplicationContextProvider.getApplicationContext().getBean(JFrameExplorer.class);
        jf.setIconImage(getIconImage());
        jf.launch();
        jf.setVisible(true);
      }
    });
    jtb.add(jbExplorer);

    jbOpenUrl.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        JDialogUrl jdUrl = new JDialogUrl();
        jdUrl.setVisible(true);
        if (!jdUrl.isResult())
          return;
        String url = jdUrl.getJtfUrl().getText();
        androidDevice.openUrl(url);
      }
    });
    jtb.add(jbOpenUrl);

    jtb.add(jbRestartClient);

  }

  public void launchInjector() {
    injector.screencapture.setListener(new ScreenCaptureListener() {

      @Override
      public void handleNewImage(Dimension size, BufferedImage image, boolean landscape) {
        if (oldImageDimension == null || !size.equals(oldImageDimension)) {
          if (useCustomWindowSize()) {
            setPrefferedWindowSize();
          } else {
            jsp.setPreferredSize(size);
          }
          JFrameMain.this.pack();
          oldImageDimension = size;
        }
        jp.handleNewImage(size, image);
      }
    });
    injector.start();
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

}
