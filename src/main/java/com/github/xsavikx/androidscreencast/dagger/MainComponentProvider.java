package com.github.xsavikx.androidscreencast.dagger;

public final class MainComponentProvider {
    private static MainComponent INSTANCE;

    private MainComponentProvider() {

    }

    public static MainComponent mainComponent() {
        if (INSTANCE == null) {
            INSTANCE = DaggerMainComponent.create();
        }
        return INSTANCE;
    }
}
