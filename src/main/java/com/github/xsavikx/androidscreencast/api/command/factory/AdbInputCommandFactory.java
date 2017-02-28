package com.github.xsavikx.androidscreencast.api.command.factory;

import com.github.xsavikx.androidscreencast.api.command.KeyCommand;
import com.github.xsavikx.androidscreencast.api.command.SwipeCommand;
import com.github.xsavikx.androidscreencast.api.command.TapCommand;
import com.github.xsavikx.androidscreencast.api.injector.InputKeyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AdbInputCommandFactory implements InputCommandFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdbInputCommandFactory.class);

    @Inject
    public AdbInputCommandFactory() {

    }

    @Override
    public KeyCommand getKeyCommand(final int keyCode) {
        final KeyCommand returnKeyCommand = new KeyCommand(keyCode);
        LOGGER.debug(String.valueOf(returnKeyCommand));
        return returnKeyCommand;
    }

    @Override
    public KeyCommand getKeyCommand(final InputKeyEvent inputKeyEvent, final boolean longpress) {
        final KeyCommand returnKeyCommand = new KeyCommand(inputKeyEvent, longpress);
        LOGGER.debug(String.valueOf(returnKeyCommand));
        return returnKeyCommand;
    }

    @Override
    public SwipeCommand getSwipeCommand(final int x1, final int y1, final int x2, final int y2, final long duration) {
        final SwipeCommand returnSwipeCommand = new SwipeCommand(x1, y1, x2, y2, duration);
        LOGGER.debug(String.valueOf(returnSwipeCommand));
        return returnSwipeCommand;
    }

    @Override
    public TapCommand getTapCommand(final int x, final int y) {
        final TapCommand returnTapCommand = new TapCommand(x, y);
        LOGGER.debug(String.valueOf(returnTapCommand));
        return returnTapCommand;
    }

}
