package com.github.xsavikx.androidscreencast.api.command.factory;

import com.github.xsavikx.androidscreencast.api.command.KeyCommand;
import com.github.xsavikx.androidscreencast.api.command.SwipeCommand;
import com.github.xsavikx.androidscreencast.api.command.TapCommand;
import com.github.xsavikx.androidscreencast.api.injector.InputKeyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AdbInputCommandFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdbInputCommandFactory.class);

    public static KeyCommand getKeyCommand(int keyCode) {
        KeyCommand returnKeyCommand = new KeyCommand(keyCode);
        LOGGER.debug(String.valueOf(returnKeyCommand));
        return returnKeyCommand;
    }

    public static KeyCommand getKeyCommand(InputKeyEvent inputKeyEvent) {
        KeyCommand returnKeyCommand = new KeyCommand(inputKeyEvent);
        LOGGER.debug(String.valueOf(returnKeyCommand));
        return returnKeyCommand;
    }


    public static KeyCommand getKeyCommand(int keyCode, boolean longpress) {
        KeyCommand returnKeyCommand = new KeyCommand(keyCode, longpress);
        LOGGER.debug(String.valueOf(returnKeyCommand));
        return returnKeyCommand;
    }

    public static KeyCommand getKeyCommand(InputKeyEvent inputKeyEvent, boolean longpress) {
        KeyCommand returnKeyCommand = new KeyCommand(inputKeyEvent, longpress);
        LOGGER.debug(String.valueOf(returnKeyCommand));
        return returnKeyCommand;
    }

    public static SwipeCommand getSwipeCommand(int x1, int y1, int x2, int y2, long duration) {
        SwipeCommand returnSwipeCommand = new SwipeCommand(x1, y1, x2, y2, duration);
        LOGGER.debug(String.valueOf(returnSwipeCommand));
        return returnSwipeCommand;
    }

    public static SwipeCommand getSwipeCommand(int x1, int y1, int x2, int y2) {
        SwipeCommand returnSwipeCommand = new SwipeCommand(x1, y1, x2, y2);
        LOGGER.debug(String.valueOf(returnSwipeCommand));
        return returnSwipeCommand;
    }

    public static TapCommand getTapCommand(int x, int y) {
        TapCommand returnTapCommand = new TapCommand(x, y);
        LOGGER.debug(String.valueOf(returnTapCommand));
        return returnTapCommand;
    }
}
