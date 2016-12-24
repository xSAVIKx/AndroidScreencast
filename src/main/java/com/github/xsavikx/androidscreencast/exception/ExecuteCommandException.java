package com.github.xsavikx.androidscreencast.exception;

public class ExecuteCommandException extends AndroidScreenCastRuntimeException {
    public ExecuteCommandException(String command) {
        super(String.format("Cannot execute command '%s'.", command));
    }
}
