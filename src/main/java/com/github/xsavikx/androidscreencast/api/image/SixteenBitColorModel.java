package com.github.xsavikx.androidscreencast.api.image;

/**
 * Modified version of SixteenBitColorModel from <a href="https://android.googlesource.com/platform/tools/swt/+/master/chimpchat/src/main/java/com/android/chimpchat/adb/image/SixteenBitColorModel.java">android.chimpchat</a>
 */
public final class SixteenBitColorModel extends AbstractRawImageColorModel {

    @Override
    protected int getPixel(byte[] data) {
        int value = data[0] & 0x00FF;
        value |= (data[1] << 8) & 0x0FF00;
        return value;
    }

    @Override
    public int getAlpha(Object inData) {
        return 0xff;
    }

    @Override
    public int getBlue(Object inData) {
        int pixel = getPixel(inData);
        return ((pixel) & 0x01F) << 3;
    }

    @Override
    public int getGreen(Object inData) {
        int pixel = getPixel(inData);
        return ((pixel >> 5) & 0x03F) << 2;
    }

    @Override
    public int getRed(Object inData) {
        int pixel = getPixel(inData);
        return ((pixel >> 11) & 0x01F) << 3;
    }
}