package com.github.xsavikx.androidscreencast.ui;

import com.github.xsavikx.androidscreencast.api.injector.Injector;
import com.github.xsavikx.androidscreencast.api.injector.InputKeyEvent;
import com.github.xsavikx.androidscreencast.app.AndroidScreencastApplication;
import com.github.xsavikx.androidscreencast.constant.Constants;
import com.github.xsavikx.androidscreencast.spring.config.ApplicationContextProvider;
import com.github.xsavikx.androidscreencast.ui.explorer.JFrameExplorer;
import com.github.xsavikx.androidscreencast.ui.interaction.KeyEventDispatcherFactory;
import com.github.xsavikx.androidscreencast.ui.interaction.KeyboardActionListenerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;

@Component
public class JFrameMain extends JFrame {
    private static final long serialVersionUID = -2085909236767692371L;
    private final JPanelScreen jp;
    private final MouseAdapter ma;
    private final Injector injector;
    private final Environment env;
    private transient boolean isDisposed = false;
    private JToolBar jtb = new JToolBar();
    private JToolBar jtbHardkeys = new JToolBar();
    private JScrollPane jsp;
    private JButton jbExplorer = new JButton("Explore");
    private JButton jbRestartClient = new JButton("Restart client");
    private JButton jbExecuteKeyEvent = new JButton("Execute keycode");
    private JButton jbKbHome = new JButton("Home");
    private JButton jbKbMenu = new JButton("Menu");
    private JButton jbKbBack = new JButton("Back");
    private JButton jbKbSearch = new JButton("Search");
    private JButton jbKbPhoneOn = new JButton("Call");
    private JButton jbKbPhoneOff = new JButton("End call");
    private Dimension oldImageDimension;

    @Autowired
    public JFrameMain(JPanelScreen jp, Environment env, Injector injector, MouseAdapter ma) {
        this.jp = jp;
        this.injector = injector;
        this.env = env;
        this.ma = ma;
    }

    private void setPreferredWindowSize() {
        if (env.containsProperty(Constants.DEFAULT_WINDOW_HEIGHT) && env.containsProperty(Constants.DEFAULT_WINDOW_WIDTH)) {
            Integer height = env.getProperty(Constants.DEFAULT_WINDOW_HEIGHT, Integer.class);
            Integer width = env.getProperty(Constants.DEFAULT_WINDOW_WIDTH, Integer.class);
            if (height != null && width != null)
                getContentPane().setPreferredSize(new Dimension(width, height));
        }
        pack();
    }

    @Override
    public void dispose() {
        if (isDisposed) {
            return;
        }
        isDisposed = true;
        super.dispose();
        ApplicationContextProvider.getBean(AndroidScreencastApplication.class).stop();
    }

    @PostConstruct
    public void initialize() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(KeyEventDispatcherFactory.getKeyEventDispatcher(this));
        jtb.setFocusable(false);
        jbExplorer.setFocusable(false);
        jbKbHome.setFocusable(false);
        jbKbMenu.setFocusable(false);
        jbKbBack.setFocusable(false);
        jbKbSearch.setFocusable(false);
        jbKbPhoneOn.setFocusable(false);
        jbKbPhoneOff.setFocusable(false);
        jbRestartClient.setFocusable(false);
        jbExecuteKeyEvent.setFocusable(false);

        jbKbHome.addActionListener(KeyboardActionListenerFactory.getInstance(InputKeyEvent.KEYCODE_HOME));
        jbKbMenu.addActionListener(KeyboardActionListenerFactory.getInstance(InputKeyEvent.KEYCODE_MENU));
        jbKbBack.addActionListener(KeyboardActionListenerFactory.getInstance(InputKeyEvent.KEYCODE_BACK));
        jbKbSearch.addActionListener(KeyboardActionListenerFactory.getInstance(InputKeyEvent.KEYCODE_SEARCH));
        jbKbPhoneOn.addActionListener(KeyboardActionListenerFactory.getInstance(InputKeyEvent.KEYCODE_CALL));
        jbKbPhoneOff.addActionListener(KeyboardActionListenerFactory.getInstance(InputKeyEvent.KEYCODE_ENDCALL));

        jtbHardkeys.add(jbKbHome);
        jtbHardkeys.add(jbKbMenu);
        jtbHardkeys.add(jbKbBack);
        jtbHardkeys.add(jbKbSearch);
        jtbHardkeys.add(jbKbPhoneOn);
        jtbHardkeys.add(jbKbPhoneOff);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        add(jtb, BorderLayout.NORTH);
        add(jtbHardkeys, BorderLayout.SOUTH);
        jsp = new JScrollPane(jp);
        add(jsp, BorderLayout.CENTER);
        jsp.setPreferredSize(new Dimension(100, 100));
        pack();
        setLocationRelativeTo(null);
        setPreferredWindowSize();

        jp.addMouseMotionListener(ma);
        jp.addMouseListener(ma);
        jp.addMouseWheelListener(ma);

        jbExplorer.addActionListener(actionEvent -> {
            JFrameExplorer jf = ApplicationContextProvider.getBean(JFrameExplorer.class);
            jf.setIconImage(getIconImage());
            jf.launch();
            jf.setVisible(true);
        });
        jtb.add(jbExplorer);

        jtb.add(jbRestartClient);

        jbExecuteKeyEvent.addActionListener(actionEvent -> {
            JDialogExecuteKeyEvent jdExecuteKeyEvent = ApplicationContextProvider
                    .getBean(JDialogExecuteKeyEvent.class);
            jdExecuteKeyEvent.setVisible(true);
        });

        jtb.add(jbExecuteKeyEvent);
    }

    public void launchInjector() {
        injector.screenCaptureThread.setListener((size, image, landscape) -> {
            if (oldImageDimension == null || !size.equals(oldImageDimension)) {
                jsp.setPreferredSize(size);
                JFrameMain.this.pack();
                oldImageDimension = size;
            }
            jp.handleNewImage(size, image);
        });
        injector.start();
    }

    private void startRecording() {
        JFileChooser jFileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Video file", "mov");
        jFileChooser.setFileFilter(filter);
        int returnVal = jFileChooser.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            injector.screenCaptureThread.startRecording(jFileChooser.getSelectedFile());
        }
    }

    private void stopRecording() {
        injector.screenCaptureThread.stopRecording();
    }

}
