package com.github.xsavikx.android.screencast.api.injector;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.SwingUtilities;

import com.android.ddmlib.IDevice;
import com.android.ddmlib.RawImage;
import com.github.xsavikx.android.screencast.api.recording.QuickTimeOutputStream;

public class ScreenCaptureThread extends Thread {

	public interface ScreenCaptureListener {
		public void handleNewImage(Dimension size, BufferedImage image,
				boolean landscape);
	}
	private BufferedImage image;
	private Dimension size;
	private IDevice device;
	private QuickTimeOutputStream qos = null;
	private boolean landscape = false;

	private ScreenCaptureListener listener = null;

	public ScreenCaptureThread(IDevice device) {
		super("Screen capture");
		this.device = device;
		image = null;
		size = new Dimension();
	}

	public void display(RawImage rawImage) {
		int width2 = landscape ? rawImage.height : rawImage.width;
		int height2 = landscape ? rawImage.width : rawImage.height;
		if (image == null) {
			image = new BufferedImage(width2, height2,
					BufferedImage.TYPE_INT_RGB);
			size.setSize(image.getWidth(), image.getHeight());
		} else {
			if (image.getHeight() != height2 || image.getWidth() != width2) {
				image = new BufferedImage(width2, height2,
						BufferedImage.TYPE_INT_RGB);
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
			throw new RuntimeException(e);
		}

		if (listener != null) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					listener.handleNewImage(size, image, landscape);
					// jp.handleNewImage(size, image, landscape);
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
				return false;
			}
			return true;
		}

		// System.out.println("Getting initial screenshot through ADB");
		RawImage rawImage = null;
		synchronized (device) {
			rawImage = device.getScreenshot();
		}
		if (rawImage != null) {
			// System.out.println("screenshot through ADB ok");
			display(rawImage);
		} else {
			System.out.println("failed getting screenshot through ADB ok");
		}
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
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
				break;
			} catch (IOException e) {
				System.err.println((new StringBuilder())
						.append("Exception fetching image: ")
						.append(e.toString()).toString());
			}

		} while (true);
	}

	public void setListener(ScreenCaptureListener listener) {
		this.listener = listener;
	}

	public void startRecording(File f) {
		try {
			if (!f.getName().toLowerCase().endsWith(".mov"))
				f = new File(f.getAbsolutePath() + ".mov");
			qos = new QuickTimeOutputStream(f,
					QuickTimeOutputStream.VideoFormat.JPG);
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

	public void toogleOrientation() {
		landscape = !landscape;
	}

}
