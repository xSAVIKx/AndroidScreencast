package com.github.xsavikx.androidscreencast.ui.interaction;

import com.github.xsavikx.androidscreencast.api.command.executor.CommandExecutor;
import com.github.xsavikx.androidscreencast.api.command.factory.AdbInputCommandFactory;
import com.github.xsavikx.androidscreencast.api.injector.Injector;
import com.github.xsavikx.androidscreencast.spring.config.ApplicationContextProvider;
import com.github.xsavikx.androidscreencast.ui.JPanelScreen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public class MouseActionAdapter extends MouseAdapter {
    private final static long ONE_SECOND = 1000L;
    private final JPanelScreen jp;
    private CommandExecutor commandExecutor;
    private Injector injector;
    private int dragFromX = -1;
    private int dragFromY = -1;
    private long timeFromPress = -1;

    public MouseActionAdapter(JPanelScreen jPanelScreen) {
        this.jp = jPanelScreen;
    }

    public MouseActionAdapter(JPanelScreen jPanelScreen, Injector injector) {
        this(jPanelScreen);
        this.injector = injector;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (injector != null && e.getButton() == MouseEvent.BUTTON3) {
            injector.screencapture.toogleOrientation();
            e.consume();
            return;
        }
        final Point p2 = jp.getRawPoint(e.getPoint());
        if (p2.x > 0 && p2.y > 0) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    getCommandExecutor().execute(AdbInputCommandFactory.getTapCommand(p2.x, p2.y));

                }
            });
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (dragFromX == -1 && dragFromY == -1) {
            Point p2 = jp.getRawPoint(e.getPoint());
            dragFromX = p2.x;
            dragFromY = p2.y;
            timeFromPress = System.currentTimeMillis();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (timeFromPress >= ONE_SECOND) {
            final Point p2 = jp.getRawPoint(e.getPoint());
            final int xFrom = dragFromX;
            final int yFrom = dragFromY;
            final int xTo = p2.x;
            final int yTo = p2.y;
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    getCommandExecutor().execute(AdbInputCommandFactory.getSwipeCommand(xFrom, yFrom, xTo, yTo, timeFromPress));
                }
            });
            dragFromX = -1;
            dragFromY = -1;
            timeFromPress = -1;
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent arg0) {
        // if (JFrameMain.this.injector == null)
        // return;
        // JFrameMain.this.injector.injectTrackball(arg0.getWheelRotation() < 0 ?
        // -1f : 1f);
    }

    private CommandExecutor getCommandExecutor() {
        if (commandExecutor == null) {
            commandExecutor = ApplicationContextProvider.getApplicationContext().getBean(CommandExecutor.class);
        }
        return commandExecutor;
    }
}
