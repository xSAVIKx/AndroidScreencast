package com.github.xsavikx.androidscreencast.api.injector;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.RawImage;
import com.android.ddmlib.TimeoutException;
import com.github.xsavikx.androidscreencast.api.image.ImageUtils;
import com.github.xsavikx.androidscreencast.api.recording.QuickTimeOutputStream;
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
            LOGGER.warn("Adb command timeout happened. Timeout would be increased by 1 second for the next try.", e);
            currentAdbCommandTimeout++;
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
                    qos.writeFrame(image, 10);
                } catch (IOException e) {
                    LOGGER.error(e);
                }
            });
        }
    }

    public void setListener(ScreenCaptureListener listener) {
        this.listener = listener;
    }


    public void startRecording(File f) {
        try {
            if (!f.getName().toLowerCase().endsWith(".mov"))
                f = new File(f.getAbsolutePath() + ".mov");
            qos = new QuickTimeOutputStream(f, QuickTimeOutputStream.VideoFormat.JPG);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        qos.setVideoCompressionQuality(1f);
        qos.setTimeScale(30); // 30 fps
    }

    public void stopRecording() {
        try {
            QuickTimeOutputStream o = qos;
            qos = null;
            o.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
