package com.github.xsavikx.androidscreencast.exception;

public final class IllegalAdbConfigurationException extends AndroidScreenCastRuntimeException {

    public IllegalAdbConfigurationException(String adbPath) {
        super(String.format("Exception happened during running your ADB instance. Probably ADB path is misconfigured. ADB path='%s'", adbPath));
    }
}
