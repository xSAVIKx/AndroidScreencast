package com.github.xsavikx.android.screencast.api.injector;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.RawImage;
import com.android.ddmlib.TimeoutException;
import com.github.xsavikx.android.screencast.api.recording.QuickTimeOutputStream;

@Component
public class ScreenCaptureThread extends Thread {
  private static final Logger LOGGER = Logger.getLogger(ScreenCaptureThread.class);

  public interface ScreenCaptureListener {
    public void handleNewImage(Dimension size, BufferedImage image, boolean landscape);
  }

  private BufferedImage image;
  private Dimension size;
  @Autowired
  private IDevice device;
  private QuickTimeOutputStream qos = null;
  private boolean landscape = false;

  private ScreenCaptureListener listener = null;

  public ScreenCaptureThread() {
    super("Screen capture");
    image = null;
    size = new Dimension();
  }

  public void display(RawImage rawImage) {
    int width2 = landscape ? rawImage.height : rawImage.width;
    int height2 = landscape ? rawImage.width : rawImage.height;
    if (image == null) {
      image = new BufferedImage(width2, height2, BufferedImage.TYPE_INT_RGB);
      size.setSize(image.getWidth(), image.getHeight());
    } else {
      if (image.getHeight() != height2 || image.getWidth() != width2) {
        image = new BufferedImage(width2, height2, BufferedImage.TYPE_INT_RGB);
        size.setSize(image.getWidth(), image.getHeight());
      }
    }
    int index = 0;
    int indexInc = rawImage.bpp >> 3;
    for (int y = 0; y < rawImage.height; y++) {
      for (int x = 0; x < rawImage.width; x++, index += indexInc) {
        int value = rawImage.getARGB(index);
        if (landscape)
          image.setRGB(y, rawImage.width - x - 1, value);
        else
          image.setRGB(x, y, value);
      }
    }

    try {
      if (qos != null)
        qos.writeFrame(image, 10);
    } catch (IOException e) {
      LOGGER.error("display(RawImage)", e);

      throw new RuntimeException(e);
    }

    if (listener != null) {
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          listener.handleNewImage(size, image, landscape);
        }
      });
    }

  }

  private boolean fetchImage() throws IOException {

    if (device == null) {
      // device not ready
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        LOGGER.error("fetchImage()", e);
        return false;
      }
      return true;
    }
    RawImage rawImage = null;
    synchronized (device) {
      try {
        rawImage = device.getScreenshot(5, TimeUnit.SECONDS);
      } catch (TimeoutException | AdbCommandRejectedException e) {
        LOGGER.error("fetchImage()", e);
      }
    }
    if (rawImage != null) {
      display(rawImage);
    } else {
      LOGGER.info("failed getting screenshot through ADB ok");
    }
    try {
      Thread.sleep(5);
    } catch (InterruptedException e) {
      LOGGER.error("fetchImage()", e);
      return false;
    }
    return true;
  }

  public ScreenCaptureListener getListener() {
    return listener;
  }

  public Dimension getPreferredSize() {
    return size;
  }

  @Override
  public void run() {
    do {
      try {
        boolean ok = fetchImage();
        if (!ok)
          break;
      } catch (java.nio.channels.ClosedByInterruptException ciex) {
        LOGGER.error("run()", ciex);

        break;
      } catch (IOException e) {
        LOGGER.error("run()", e);
        LOGGER.error((new StringBuilder()).append("Exception fetching image: ").append(e.toString()).toString());
      }

    } while (true);
  }

  public void setListener(ScreenCaptureListener listener) {
    this.listener = listener;
  }

  public void startRecording(File f) {
    LOGGER.debug("startRecording(File f=" + f + ") - start");

    try {
      if (!f.getName().toLowerCase().endsWith(".mov"))
        f = new File(f.getAbsolutePath() + ".mov");
      qos = new QuickTimeOutputStream(f, QuickTimeOutputStream.VideoFormat.JPG);
    } catch (IOException e) {
      LOGGER.error("startRecording(File)", e);

      throw new RuntimeException(e);
    }
    qos.setVideoCompressionQuality(1f);
    qos.setTimeScale(30); // 30 fps

    LOGGER.debug("startRecording(File f=" + f + ") - end");
  }

  public void stopRecording() {
    LOGGER.debug("stopRecording() - start");

    try {
      QuickTimeOutputStream o = qos;
      qos = null;
      o.close();
    } catch (IOException e) {
      LOGGER.error("stopRecording()", e);

      throw new RuntimeException(e);
    }

    LOGGER.debug("stopRecording() - end");
  }

  public void toogleOrientation() {
    LOGGER.debug("toogleOrientation() - start");

    landscape = !landscape;

    LOGGER.debug("toogleOrientation() - end");
  }

}
