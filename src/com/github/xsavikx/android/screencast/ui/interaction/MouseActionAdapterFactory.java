package com.github.xsavikx.android.screencast.ui.interaction;

import com.github.xsavikx.android.screencast.api.injector.Injector;
import com.github.xsavikx.android.screencast.ui.JPanelScreen;

public final class MouseActionAdapterFactory {
  public static MouseActionAdapter getInstance(JPanelScreen jPanelScreen) {
    return new MouseActionAdapter(jPanelScreen);
  }

  public static MouseActionAdapter getInstance(JPanelScreen jPanelScreen, Injector injector) {
    return new MouseActionAdapter(jPanelScreen, injector);
  }
}
