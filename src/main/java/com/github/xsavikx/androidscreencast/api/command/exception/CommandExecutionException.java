package com.github.xsavikx.androidscreencast.api.command.exception;

import com.github.xsavikx.androidscreencast.exception.AndroidScreenCastRuntimeException;

class CommandExecutionException extends AndroidScreenCastRuntimeException {

    private static final long serialVersionUID = 8676432388325401069L;

    CommandExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
