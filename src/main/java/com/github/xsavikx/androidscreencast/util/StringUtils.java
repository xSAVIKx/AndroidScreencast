package com.github.xsavikx.androidscreencast.util;

public class StringUtils {
    private StringUtils() {
        //
    }

    public static boolean isNotEmpty(final CharSequence charSequence) {
        return charSequence != null && charSequence.length() > 0;
    }
}
