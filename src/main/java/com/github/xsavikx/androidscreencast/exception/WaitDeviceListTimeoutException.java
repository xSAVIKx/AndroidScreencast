package com.github.xsavikx.androidscreencast.exception;

public class WaitDeviceListTimeoutException extends AndroidScreenCastRuntimeException {
    public WaitDeviceListTimeoutException() {
        super("Cannot initialize devices list.", "Try to choose correct ADB path or reconnect your device.");
    }
}
