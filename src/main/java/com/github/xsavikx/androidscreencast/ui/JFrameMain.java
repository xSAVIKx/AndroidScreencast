package com.github.xsavikx.androidscreencast.ui;

import com.github.xsavikx.androidscreencast.api.injector.Injector;
import com.github.xsavikx.androidscreencast.api.injector.InputKeyEvent;
import com.github.xsavikx.androidscreencast.dagger.MainComponentProvider;
import com.github.xsavikx.androidscreencast.exception.IORuntimeException;
import com.github.xsavikx.androidscreencast.ui.explorer.JFrameExplorer;
import com.github.xsavikx.androidscreencast.ui.interaction.KeyEventDispatcherFactory;
import com.github.xsavikx.androidscreencast.ui.interaction.KeyboardActionListenerFactory;
import com.github.xsavikx.androidscreencast.ui.interaction.MouseActionAdapter;
import com.google.common.io.Files;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static com.github.xsavikx.androidscreencast.configuration.ApplicationConfigurationPropertyKeys.APP_WINDOW_HEIGHT_KEY;
import static com.github.xsavikx.androidscreencast.configuration.ApplicationConfigurationPropertyKeys.APP_WINDOW_WIDTH_KEY;

@Singleton
public final class JFrameMain extends JFrame {

    private static final long serialVersionUID = -2085909236767692371L;
    private final JPanelScreen jp;
    private final MouseActionAdapter ma;
    private final Injector injector;
    private final Dimension windowSize;
    private final JFrameExplorer frameExplorer;
    private final JDialogExecuteKeyEvent dialogExecuteKeyEvent;
    private transient boolean isDisposed = false;

    private JToolBar jtb = new JToolBar();
    private JToolBar jtbHardkeys = new JToolBar();
    private JScrollPane jsp;
    private JButton jbExplorer = new JButton("Explore");
    private JButton jbExecuteKeyEvent = new JButton("Execute keycode");
    private JButton jbKbHome = new JButton("Home");
    private JButton jbKbMenu = new JButton("Menu");
    private JButton jbKbBack = new JButton("Back");
    private JButton jbKbSearch = new JButton("Search");
    private JButton jbKbPhoneOn = new JButton("Call");
    private JButton jbKbPhoneOff = new JButton("End call");
    private JButton jbRecord = new JButton("Start record");
    private Dimension oldImageDimension;

    @Inject
    JFrameMain(JPanelScreen jp,
               Injector injector,
               MouseActionAdapter ma,
               JFrameExplorer frameExplorer,
               JDialogExecuteKeyEvent dialogExecuteKeyEvent,
               @Named(APP_WINDOW_WIDTH_KEY) int width,
               @Named(APP_WINDOW_HEIGHT_KEY) int height) {
        this.jp = jp;
        this.injector = injector;
        this.ma = ma;
        this.frameExplorer = frameExplorer;
        this.dialogExecuteKeyEvent = dialogExecuteKeyEvent;
        this.windowSize = new Dimension(width, height);
    }

    @Override
    public void dispose() {
        if (isDisposed) {
            return;
        }
        isDisposed = true;
        super.dispose();
        MainComponentProvider.mainComponent().application().stop();
    }

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
        jbExecuteKeyEvent.setFocusable(false);
        jbRecord.setFocusable(false);

        jbKbHome.addActionListener(KeyboardActionListenerFactory.getInstance(InputKeyEvent.KEYCODE_HOME));
        jbKbMenu.addActionListener(KeyboardActionListenerFactory.getInstance(InputKeyEvent.KEYCODE_MENU));
        jbKbBack.addActionListener(KeyboardActionListenerFactory.getInstance(InputKeyEvent.KEYCODE_BACK));
        jbKbSearch.addActionListener(KeyboardActionListenerFactory.getInstance(InputKeyEvent.KEYCODE_SEARCH));
        jbKbPhoneOn.addActionListener(KeyboardActionListenerFactory.getInstance(InputKeyEvent.KEYCODE_CALL));
        jbKbPhoneOff.addActionListener(KeyboardActionListenerFactory.getInstance(InputKeyEvent.KEYCODE_ENDCALL));
        jbRecord.addActionListener(createRecordActionListener());


        jtbHardkeys.add(jbKbHome);
        jtbHardkeys.add(jbKbMenu);
        jtbHardkeys.add(jbKbBack);
        jtbHardkeys.add(jbKbSearch);
        jtbHardkeys.add(jbKbPhoneOn);
        jtbHardkeys.add(jbKbPhoneOff);

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
            SwingUtilities.invokeLater(() -> {
                JFrameExplorer jf = frameExplorer;
                jf.setIconImage(getIconImage());
                jf.launch();
                jf.setVisible(true);
            });
        });
        jtb.add(jbExplorer);

        jbExecuteKeyEvent.addActionListener(actionEvent -> {
            SwingUtilities.invokeLater(dialogExecuteKeyEvent::open);
        });

        jtb.add(jbExecuteKeyEvent);
        jtb.add(jbRecord);
    }

    private void setPreferredWindowSize() {
        getContentPane().setPreferredSize(windowSize);
        pack();
    }

    private ActionListener createRecordActionListener() {
        return new ActionListener() {
            private final Path tmpDir = Files.createTempDir().toPath();
            private boolean recording = false;
            private File tmpVideoFile;

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (!recording) {
                        recording = true;
                        jbRecord.setText("Stop record");
                        tmpVideoFile = java.nio.file.Files.createTempFile(tmpDir, "androidScreenCast", ".mov.tmp").toFile();
                        startRecording(tmpVideoFile);
                    } else {
                        recording = false;
                        stopRecording();
                        jbRecord.setText("Start record");
                        JFileChooser jFileChooser = new JFileChooser();
                        FileNameExtensionFilter filter = new FileNameExtensionFilter("Video file", "mov");
                        jFileChooser.setFileFilter(filter);
                        int returnVal = jFileChooser.showSaveDialog(JFrameMain.this);
                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                            File resultFile = jFileChooser.getSelectedFile();
                            if (!resultFile.getName().endsWith(".mov")) {
                                resultFile = new File(resultFile.getAbsolutePath() + ".mov");
                            }
                            Files.move(tmpVideoFile, resultFile);
                        } else {
                            tmpVideoFile.deleteOnExit();
                        }
                    }
                } catch (IOException ex) {
                    throw new IORuntimeException(ex);
                }
            }
        };
    }

    public void launchInjector() {
        injector.setScreenCaptureListener((size, image, landscape) -> {
            if (!size.equals(oldImageDimension)) {
                jsp.setPreferredSize(size);
                JFrameMain.this.pack();
                oldImageDimension = size;
            }
            jp.handleNewImage(size, image);
        });
        injector.start();
    }

    private void startRecording(File file) {
        injector.startRecording(file);
    }

    private void stopRecording() {
        injector.stopRecording();
    }
}
