package com.github.xsavikx.androidscreencast.api.command.factory;

import com.github.xsavikx.androidscreencast.api.command.KeyCommand;
import com.github.xsavikx.androidscreencast.api.command.SwipeCommand;
import com.github.xsavikx.androidscreencast.api.command.TapCommand;
import com.github.xsavikx.androidscreencast.api.injector.InputKeyEvent;

public interface InputCommandFactory {

    KeyCommand getKeyCommand(int keyCode);

    KeyCommand getKeyCommand(InputKeyEvent inputKeyEvent, boolean longpress);

    SwipeCommand getSwipeCommand(int x1, int y1, int x2, int y2, long duration);

    TapCommand getTapCommand(int x, int y);
}
