package com.github.xsavikx.androidscreencast.api.injector;

import com.android.ddmlib.MultiLineReceiver;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MultiLineReceiverPrinter extends MultiLineReceiver {
    @Inject
    public MultiLineReceiverPrinter() {

    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public void processNewLines(String[] arg0) {
        for (String elem : arg0) {
            System.out.println(elem);
        }
    }

}
