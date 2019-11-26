package com.github.xsavikx.androidscreencast.exception;

import java.io.IOException;

/**
 * Runtime Exception wrapper for {@link IOException}
 */
public final class IORuntimeException extends AndroidScreenCastRuntimeException {

    public IORuntimeException(String message, IOException cause) {
        super(message, cause);
    }

    public IORuntimeException(IOException cause) {
        super(cause);
    }

    public IORuntimeException(String message, IOException cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
