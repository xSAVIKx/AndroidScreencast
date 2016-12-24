package com.github.xsavikx.androidscreencast.exception;

public class AndroidScreenCastRuntimeException extends RuntimeException {
    private String additionalInformation;

    public AndroidScreenCastRuntimeException() {
    }

    public AndroidScreenCastRuntimeException(String message) {
        super(message);
    }


    public AndroidScreenCastRuntimeException(String message, String additionalInformation) {
        super(message);
        this.additionalInformation = additionalInformation;
    }

    public AndroidScreenCastRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public AndroidScreenCastRuntimeException(Throwable cause) {
        super(cause);
    }

    public AndroidScreenCastRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }
}
