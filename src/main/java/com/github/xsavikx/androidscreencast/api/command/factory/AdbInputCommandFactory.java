package com.github.xsavikx.androidscreencast.api.command.factory;

import com.github.xsavikx.androidscreencast.api.command.KeyCommand;
import com.github.xsavikx.androidscreencast.api.command.SwipeCommand;
import com.github.xsavikx.androidscreencast.api.command.TapCommand;
import com.github.xsavikx.androidscreencast.api.injector.InputKeyEvent;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

import static org.slf4j.LoggerFactory.getLogger;

@Singleton
public final class AdbInputCommandFactory implements InputCommandFactory {

    @Inject
    public AdbInputCommandFactory() {
    }

    @Override
    public KeyCommand getKeyCommand(final int keyCode) {
        final KeyCommand returnKeyCommand = new KeyCommand(keyCode);
        log().debug(String.valueOf(returnKeyCommand));
        return returnKeyCommand;
    }

    @Override
    public KeyCommand getKeyCommand(final InputKeyEvent inputKeyEvent, final boolean longpress) {
        final KeyCommand returnKeyCommand = new KeyCommand(inputKeyEvent, longpress);
        log().debug(String.valueOf(returnKeyCommand));
        return returnKeyCommand;
    }

    @Override
    public SwipeCommand getSwipeCommand(final int x1, final int y1, final int x2, final int y2, final long duration) {
        final SwipeCommand returnSwipeCommand = new SwipeCommand(x1, y1, x2, y2, duration);
        log().debug(String.valueOf(returnSwipeCommand));
        return returnSwipeCommand;
    }

    @Override
    public TapCommand getTapCommand(final int x, final int y) {
        final TapCommand returnTapCommand = new TapCommand(x, y);
        log().debug(String.valueOf(returnTapCommand));
        return returnTapCommand;
    }

    private enum LogSingleton {
        INSTANCE;

        @SuppressWarnings({"NonSerializableFieldInSerializableClass", "ImmutableEnumChecker"})
        private final Logger value = getLogger(AdbInputCommandFactory.class);
    }

    private static Logger log() {
        return LogSingleton.INSTANCE.value;
    }
}
