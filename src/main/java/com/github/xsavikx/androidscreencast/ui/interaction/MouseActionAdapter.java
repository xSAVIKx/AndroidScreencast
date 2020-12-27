/*
 * Copyright 2020 Yurii Serhiichuk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.github.xsavikx.androidscreencast.ui.interaction;

import com.github.xsavikx.androidscreencast.api.command.SwipeCommand;
import com.github.xsavikx.androidscreencast.api.command.TapCommand;
import com.github.xsavikx.androidscreencast.api.command.executor.CommandExecutor;
import com.github.xsavikx.androidscreencast.api.command.factory.InputCommandFactory;
import com.github.xsavikx.androidscreencast.api.injector.Injector;
import com.github.xsavikx.androidscreencast.ui.JPanelScreen;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

@Singleton
public final class MouseActionAdapter extends MouseAdapter {

    private final static long ONE_SECOND = 1000L;
    private final JPanelScreen jp;
    private final CommandExecutor commandExecutor;
    private final InputCommandFactory inputCommandFactory;
    private final Injector injector;
    private int dragFromX = -1;
    private int dragFromY = -1;
    private long timeFromPress = -1;

    @Inject
    MouseActionAdapter(final JPanelScreen jp,
                       final CommandExecutor commandExecutor,
                       final InputCommandFactory inputCommandFactory,
                       final Injector injector) {
        this.jp = jp;
        this.commandExecutor = commandExecutor;
        this.inputCommandFactory = inputCommandFactory;
        this.injector = injector;
    }


    @Override
    public void mouseClicked(final MouseEvent e) {
        if (injector != null && e.getButton() == MouseEvent.BUTTON3) {
            injector.toggleOrientation();
            e.consume();
            return;
        }
        final Point p2 = jp.getRawPoint(e.getPoint());
        if (p2.x > 0 && p2.y > 0) {
            SwingUtilities.invokeLater(() -> {
                final TapCommand command = inputCommandFactory.getTapCommand(p2.x, p2.y);
                commandExecutor.execute(command);
            });
        }
    }

    @Override
    public void mouseDragged(final MouseEvent e) {
        if (dragFromX == -1 && dragFromY == -1) {
            final Point p2 = jp.getRawPoint(e.getPoint());
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
            SwingUtilities.invokeLater(() -> {
                final SwipeCommand command = inputCommandFactory.getSwipeCommand(xFrom, yFrom, xTo, yTo, timeFromPress);
                commandExecutor.execute(command);
            });
            clearState();
        }
    }

    private void clearState() {
        dragFromX = -1;
        dragFromY = -1;
        timeFromPress = -1;
    }

    @Override
    public void mouseWheelMoved(final MouseWheelEvent arg0) {
        // if (JFrameMain.this.injector == null)
        // return;
        // JFrameMain.this.injector.injectTrackball(arg0.getWheelRotation() < 0 ?
        // -1f : 1f);
    }
}
