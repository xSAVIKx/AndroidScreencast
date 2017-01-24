package com.github.xsavikx.androidscreencast.api.image;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;

/**
 * Abstract color model for adb RawImage
 */
public abstract class AbstractRawImageColorModel extends ColorModel {
    private static final int PIXEL_BITS = 32;
    private static final boolean HAS_ALPHA = true;
    private static final boolean IS_ALPHA_PRE_MULTIPLIED = false;
    private static final int[] BITS = {
            8, 8, 8, 8,
    };

    public AbstractRawImageColorModel() {
        super(PIXEL_BITS, BITS, ColorSpace.getInstance(ColorSpace.CS_sRGB),
                HAS_ALPHA, IS_ALPHA_PRE_MULTIPLIED, Transparency.TRANSLUCENT,
                DataBuffer.TYPE_BYTE);
    }

    protected abstract int getPixel(byte[] data);

    int getPixel(Object inData) {
        return getPixel((byte[]) inData);
    }

    int getMask(int length) {
        int res = 0;
        for (int i = 0; i < length; i++) {
            res = (res << 1) + 1;
        }
        return res;
    }

    @Override
    public boolean isCompatibleRaster(Raster raster) {
        return true;
    }

    @Override
    public int getAlpha(int pixel) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getBlue(int pixel) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getGreen(int pixel) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getRed(int pixel) {
        throw new UnsupportedOperationException();
    }
}
