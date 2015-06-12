package com.github.xsavikx.android.screencast.api.injector;

import com.android.ddmlib.MultiLineReceiver;

public class SimpleResultCheckMultiLineReceiver extends MultiLineReceiver {
    private String successStrPtn;
    private String failureStrPtn;
    private boolean isSucceed;

    public SimpleResultCheckMultiLineReceiver(String successStrPtn, String failureStrPtn) {
        this.successStrPtn = successStrPtn;
        // failure check is not done right now
        this.failureStrPtn = failureStrPtn;
        // default is false, you MUST pass a correct successStrPtn
        this.isSucceed = false;
    }


    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public void processNewLines(String[] arg0) {
        for (String elem : arg0) {

            if(!successStrPtn.isEmpty() && elem.indexOf(successStrPtn) != -1) {
                isSucceed = true;
            }

            System.out.println(elem);
        }
    }

    public boolean isSucceed() {
        return isSucceed;
    }
}
