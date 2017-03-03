package com.github.xsavikx.androidscreencast.api.injector;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.RawImage;
import com.android.ddmlib.TimeoutException;
import com.github.xsavikx.androidscreencast.api.image.ImageUtils;
import com.github.xsavikx.androidscreencast.api.recording.QuickTimeOutputStream;
import com.github.xsavikx.androidscreencast.exception.IORuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.channels.ClosedByInterruptException;
import java.util.concurrent.TimeUnit;

import static com.github.xsavikx.androidscreencast.configuration.ApplicationConfigurationPropertyKeys.ADB_COMMAND_TIMEOUT_KEY;

@Singleton
public class ScreenCaptureRunnable implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScreenCaptureRunnable.class);
    private static final int MOV_FPS = 30;
    private static final float MOV_COMPRESSION_RATE = 1f;
    private static final int FRAME_DURATION = 10;
    private final IDevice device;
    private final long defaultAdbCommandTimeout;
    private Dimension size;
    private QuickTimeOutputStream qos = null;
    private boolean landscape = false;
    private ScreenCaptureListener listener = null;
    private long currentAdbCommandTimeout;
    private boolean isStopped = false;

    @Inject
    public ScreenCaptureRunnable(final IDevice device, @Named(ADB_COMMAND_TIMEOUT_KEY) long adbCommandTimeout) {
        this.size = new Dimension();
        this.device = device;
        this.defaultAdbCommandTimeout = adbCommandTimeout;
        this.currentAdbCommandTimeout = defaultAdbCommandTimeout;
    }

    @Override
    public void run() {
        LOGGER.info("Starting screen capturing");
        while (!isStopped) {
            try {
                final RawImage screenshot = getScreenshot();
                if (screenshot != null) {
                    display(screenshot);
                } else {
                    LOGGER.info("Failed to get device screenshot");
                }
            } catch (final ClosedByInterruptException e) {
                LOGGER.error("ADB Channel closed due to interrupted exception", e);
                break;
            } catch (final InterruptedException e) {
                LOGGER.error("Execution of thread was interrupted. Shutting down thread.", e);
                break;
            }
        }
        LOGGER.info("Stopping screen capturing");
    }

    private RawImage getScreenshot() throws InterruptedException, ClosedByInterruptException {
        RawImage rawImage = null;
        try {
            synchronized (device) {
                rawImage = device.getScreenshot(currentAdbCommandTimeout, TimeUnit.SECONDS);
            }
            currentAdbCommandTimeout = defaultAdbCommandTimeout;
        } catch (TimeoutException e) {
            currentAdbCommandTimeout++;
            LOGGER.warn("Adb command timeout happened. Timeout would be set to {} for the next try.", currentAdbCommandTimeout, e);
        } catch (AdbCommandRejectedException e) {
            LOGGER.warn("ADB Command was rejected. Will try again in 100 ms.");
            Thread.sleep(100);
        } catch (ClosedByInterruptException e) {
            throw e;
        } catch (IOException e) {
            LOGGER.warn("IO Exception happened while getting device screenshot. Will try again in 100 ms.");
            Thread.sleep(100);
        }
        return rawImage;
    }

    private void display(final RawImage rawImage) {
        final RawImage imageToProcess = landscape ? rawImage.getRotated() : rawImage;
        final BufferedImage image = ImageUtils.convertImage(imageToProcess);
        size.setSize(image.getWidth(), image.getHeight());
        if (listener != null) {
            SwingUtilities.invokeLater(() -> listener.handleNewImage(size, image, landscape));
        }
        if (qos != null) {
            SwingUtilities.invokeLater(() -> {
                try {
                    qos.writeFrame(image, FRAME_DURATION);
                } catch (IORuntimeException e) {
                    LOGGER.error("IO exception during writing video frame happened", e);
                }
            });
        }
    }

    public void setListener(final ScreenCaptureListener listener) {
        this.listener = listener;
    }


    public void startRecording(final File file) {
        try {
            qos = new QuickTimeOutputStream(file, QuickTimeOutputStream.VideoFormat.JPG);
            qos.setVideoCompressionQuality(MOV_COMPRESSION_RATE);
            qos.setTimeScale(MOV_FPS);
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public void stopRecording() {
        QuickTimeOutputStream o = qos;
        qos = null;
        o.close();
    }

    public void toggleOrientation() {
        landscape = !landscape;
    }

    public void stop() {
        isStopped = true;
    }

    public interface ScreenCaptureListener {
        void handleNewImage(Dimension size, BufferedImage image, boolean landscape);
    }
}
