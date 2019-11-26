package com.github.xsavikx.androidscreencast.api.recording.exception;

import com.github.xsavikx.androidscreencast.exception.AndroidScreenCastRuntimeException;

public final class OutputStreamAlreadyClosedException extends AndroidScreenCastRuntimeException {

    public OutputStreamAlreadyClosedException() {
        super("Output stream is already closed.");
    }
}
