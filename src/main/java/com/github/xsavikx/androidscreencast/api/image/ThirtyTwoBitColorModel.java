package com.github.xsavikx.androidscreencast.api.image;

import com.android.ddmlib.RawImage;

/**
 * Modified version of ThirtyTwoBitColorModel from
 * <a href="https://android.googlesource.com/platform/tools/swt/+/master/chimpchat/src/main/java/com/android/chimpchat/adb/image/ThirtyTwoBitColorModel.java">android.chimpchat</a>
 */
final class ThirtyTwoBitColorModel extends AbstractRawImageColorModel {

    private final int alphaLength;
    private final int alphaMask;
    private final int alphaOffset;
    private final int blueMask;
    private final int blueLength;
    private final int blueOffset;
    private final int greenMask;
    private final int greenLength;
    private final int greenOffset;
    private final int redMask;
    private final int redLength;
    private final int redOffset;

    ThirtyTwoBitColorModel(RawImage rawImage) {
        redOffset = rawImage.red_offset;
        redLength = rawImage.red_length;
        redMask = getMask(redLength);
        greenOffset = rawImage.green_offset;
        greenLength = rawImage.green_length;
        greenMask = getMask(greenLength);
        blueOffset = rawImage.blue_offset;
        blueLength = rawImage.blue_length;
        blueMask = getMask(blueLength);
        alphaLength = rawImage.alpha_length;
        alphaOffset = rawImage.alpha_offset;
        alphaMask = getMask(alphaLength);
    }

    @Override
    protected int getPixel(byte[] data) {
        int value = data[0] & 0x00FF;
        value |= (data[1] & 0x00FF) << 8;
        value |= (data[2] & 0x00FF) << 16;
        value |= (data[3] & 0x00FF) << 24;
        return value;
    }

    @Override
    public int getAlpha(Object inData) {
        int pixel = getPixel(inData);
        if (alphaLength == 0) {
            return 0xff;
        }
        return ((pixel >>> alphaOffset) & alphaMask) << (8 - alphaLength);
    }

    @Override
    public int getBlue(Object inData) {
        int pixel = getPixel(inData);
        return ((pixel >>> blueOffset) & blueMask) << (8 - blueLength);
    }

    @Override
    public int getGreen(Object inData) {
        int pixel = getPixel(inData);
        return ((pixel >>> greenOffset) & greenMask) << (8 - greenLength);
    }

    @Override
    public int getRed(Object inData) {
        int pixel = getPixel(inData);
        return ((pixel >>> redOffset) & redMask) << (8 - redLength);
    }
}