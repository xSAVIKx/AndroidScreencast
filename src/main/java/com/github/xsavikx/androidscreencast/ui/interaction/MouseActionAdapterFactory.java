package com.github.xsavikx.androidscreencast.ui.interaction;

import com.github.xsavikx.androidscreencast.api.injector.Injector;
import com.github.xsavikx.androidscreencast.ui.JPanelScreen;

public final class MouseActionAdapterFactory {
    public static MouseActionAdapter getInstance(JPanelScreen jPanelScreen) {
        return new MouseActionAdapter(jPanelScreen);
    }

    public static MouseActionAdapter getInstance(JPanelScreen jPanelScreen, Injector injector) {
        return new MouseActionAdapter(jPanelScreen, injector);
    }
}
