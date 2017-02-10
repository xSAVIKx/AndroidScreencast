package com.github.xsavikx.androidscreencast.api.injector;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.RawImage;
import com.android.ddmlib.TimeoutException;
import com.github.xsavikx.androidscreencast.api.image.ImageUtils;
import com.github.xsavikx.androidscreencast.api.recording.QuickTimeOutputStream;
import com.github.xsavikx.androidscreencast.exception.IORuntimeException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.channels.ClosedByInterruptException;
import java.util.concurrent.TimeUnit;

@Component
public class ScreenCaptureRunnable implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(ScreenCaptureRunnable.class);
    private static final String MOV_FILE_TYPE = ".mov";
    private static final int MOV_FPS = 30;
    private static final float MOV_COMPRESSION_RATE = 1f;
    private static final int FRAME_DURATION = 10;
    private final IDevice device;
    private Dimension size;
    private QuickTimeOutputStream qos = null;
    private boolean landscape = false;
    private ScreenCaptureListener listener = null;
    @Value("${adb.command.timeout:5}")
    private long defaultAdbCommandTimeout;
    @Value("${adb.command.timeout:5}")
    private long currentAdbCommandTimeout;
    private boolean isStopped = false;

    @Autowired
    public ScreenCaptureRunnable(IDevice device) {
        size = new Dimension();
        this.device = device;
    }

    @Override
    public void run() {
        LOGGER.info("Starting ScreenCaptureRunnable");
        while (!isStopped) {
            try {
                RawImage screenshot = getScreenshot();
                if (screenshot != null) {
                    display(screenshot);
                } else {
                    LOGGER.info("Failed to get device screenshot.");
                }
            } catch (ClosedByInterruptException e) {
                LOGGER.error("ADB Channel closed due to interrupted exception", e);
                break;
            } catch (InterruptedException e) {
                LOGGER.error("Execution of thread was interrupted. Shutting down thread.", e);
                break;
            }
        }
        LOGGER.info("ScreenCaptureRunnable is stopped.");
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
            LOGGER.warn(String.format("Adb command timeout happened. Timeout would be set to %d for the next try.", currentAdbCommandTimeout), e);
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

    private void display(RawImage rawImage) {
        RawImage imageToProcess = landscape ? rawImage.getRotated() : rawImage;
        BufferedImage image = ImageUtils.convertImage(imageToProcess);
        size.setSize(image.getWidth(), image.getHeight());
        if (listener != null) {
            SwingUtilities.invokeLater(() -> listener.handleNewImage(size, image, landscape));
        }
        if (qos != null) {
            SwingUtilities.invokeLater(() -> {
                try {
                    qos.writeFrame(image, FRAME_DURATION);
                } catch (IORuntimeException e) {
                    LOGGER.error(e);
                }
            });
        }
    }

    public void setListener(ScreenCaptureListener listener) {
        this.listener = listener;
    }


    public void startRecording(File file) {
        try {
            File outputFile = file;
            if (!outputFile.getName().toLowerCase().endsWith(MOV_FILE_TYPE))
                outputFile = new File(file.getAbsolutePath() + MOV_FILE_TYPE);
            qos = new QuickTimeOutputStream(outputFile, QuickTimeOutputStream.VideoFormat.JPG);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
        qos.setVideoCompressionQuality(MOV_COMPRESSION_RATE);
        qos.setTimeScale(MOV_FPS);
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
