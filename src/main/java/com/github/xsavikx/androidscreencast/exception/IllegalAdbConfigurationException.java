package com.github.xsavikx.androidscreencast.exception;

/**
 * Created by User on 16.02.2017.
 */
public class IllegalAdbConfigurationException extends AndroidScreenCastRuntimeException {
    public IllegalAdbConfigurationException(String adbPath) {
        super(String.format("Exception happened during running your ADB instance. Probably ADB path is misconfigured. ADB path='%s'", adbPath));
    }
}
