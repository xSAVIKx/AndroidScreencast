package com.github.xsavikx.androidscreencast.api.recording.exception;

import com.github.xsavikx.androidscreencast.exception.AndroidScreenCastRuntimeException;

public class MaximumAtomSizeExeededException extends AndroidScreenCastRuntimeException {
    public MaximumAtomSizeExeededException(long maximumSize, long size) {
        super(String.format("Atom size should be %d, but was %d", maximumSize, size));
    }
}
