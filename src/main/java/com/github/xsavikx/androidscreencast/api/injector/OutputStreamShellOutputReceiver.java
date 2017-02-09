package com.github.xsavikx.androidscreencast.api.injector;

import com.android.ddmlib.IShellOutputReceiver;
import com.github.xsavikx.androidscreencast.exception.IORuntimeException;

import java.io.IOException;
import java.io.OutputStream;

public class OutputStreamShellOutputReceiver implements IShellOutputReceiver {

    private OutputStream os;

    public OutputStreamShellOutputReceiver(OutputStream os) {
        this.os = os;
    }

    @Override
    public void addOutput(byte[] buf, int off, int len) {
        try {
            os.write(buf, off, len);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    @Override
    public void flush() {
        try {
            os.flush();
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

}
