package com.github.xsavikx.androidscreencast.api.command.exception;

import com.github.xsavikx.androidscreencast.exception.AndroidScreenCastRuntimeException;

public class CommandExecutionException extends AndroidScreenCastRuntimeException {

    private static final long serialVersionUID = 8676432388325401069L;

    public CommandExecutionException() {
        super();
    }

    public CommandExecutionException(String message, Throwable cause, boolean enableSuppression,
                                     boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public CommandExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommandExecutionException(String message) {
        super(message);
    }

    public CommandExecutionException(Throwable cause) {
        super(cause);
    }

}
