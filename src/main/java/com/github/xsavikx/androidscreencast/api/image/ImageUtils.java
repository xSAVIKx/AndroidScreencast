package com.github.xsavikx.androidscreencast.api.image;

import com.android.ddmlib.RawImage;

import java.awt.*;
import java.awt.image.*;
import java.util.Hashtable;

/**
 * Modified version of ImageUtils from <a href="https://android.googlesource.com/platform/tools/swt/+/master/chimpchat/src/main/java/com/android/chimpchat/adb/image/ImageUtils.java">android.chimpchat</a>
 */
public final class ImageUtils {
    private static final int SIXTEEN_BIT_IMAGE = 16;
    private static final int THIRTY_TWO_BIT_IMAGE = 32;
    private static final Hashtable<?, ?> EMPTY_HASH = new Hashtable();
    private static final int[] BAND_OFFSETS_32 = {0, 1, 2, 3};
    private static final int[] BAND_OFFSETS_16 = {0, 1};

    // Utility class
    private ImageUtils() {
    }

    /**
     * Convert a raw image into a buffered image.
     *
     * @param rawImage the image to convert.
     * @return the converted image.
     */
    public static BufferedImage convertImage(RawImage rawImage) {
        switch (rawImage.bpp) {
            case SIXTEEN_BIT_IMAGE:
                return rawImage16toARGB(rawImage);
            case THIRTY_TWO_BIT_IMAGE:
                return rawImage32toARGB(rawImage);
        }
        throw new IllegalArgumentException("Raw image contain wrong bpp: " + rawImage.bpp);
    }

    private static BufferedImage rawImage32toARGB(RawImage rawImage) {
        // Do as much as we can to not make an extra copy of the data.  This is just a bunch of
        // classes that wrap's the raw byte array of the image data.
        DataBufferByte dataBuffer = new DataBufferByte(rawImage.data, rawImage.size);
        PixelInterleavedSampleModel sampleModel =
                new PixelInterleavedSampleModel(DataBuffer.TYPE_BYTE, rawImage.width, rawImage.height,
                        4, rawImage.width * 4, BAND_OFFSETS_32);
        WritableRaster raster = Raster.createWritableRaster(sampleModel, dataBuffer,
                new Point(0, 0));
        return new BufferedImage(new ThirtyTwoBitColorModel(rawImage), raster, false, EMPTY_HASH);
    }

    private static BufferedImage rawImage16toARGB(RawImage rawImage) {
        // Do as much as we can to not make an extra copy of the data.  This is just a bunch of
        // classes that wrap's the raw byte array of the image data.
        DataBufferByte dataBuffer = new DataBufferByte(rawImage.data, rawImage.size);
        PixelInterleavedSampleModel sampleModel =
                new PixelInterleavedSampleModel(DataBuffer.TYPE_BYTE, rawImage.width, rawImage.height,
                        2, rawImage.width * 2, BAND_OFFSETS_16);
        WritableRaster raster = Raster.createWritableRaster(sampleModel, dataBuffer,
                new Point(0, 0));
        return new BufferedImage(new SixteenBitColorModel(), raster, false, EMPTY_HASH);
    }
}
