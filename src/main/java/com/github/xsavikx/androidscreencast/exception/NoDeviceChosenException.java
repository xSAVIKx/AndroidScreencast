package com.github.xsavikx.androidscreencast.exception;

public final class NoDeviceChosenException extends AndroidScreenCastRuntimeException {

    public NoDeviceChosenException() {
        super("No device was chosen or connected via ADB.", "Try to choose correct ADB path or reconnect your device.");
    }
}
