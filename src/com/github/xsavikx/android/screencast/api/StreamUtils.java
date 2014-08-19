package com.github.xsavikx.android.screencast.api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamUtils {

	public static void transfert(InputStream is, OutputStream os) {
		try {
			while (true) {
				int val = is.read();
				if (val <= -1)
					break;
				os.write(val);
			}
		} catch (IOException io) {
			throw new RuntimeException(io);
		}
	}

	public static void transfertResource(Class c, String resourceName,
			File output) {
		InputStream resStream = c.getResourceAsStream(resourceName);
		if (resStream == null)
			throw new RuntimeException("Cannot find resource " + resourceName);
		try {
			FileOutputStream fos = new FileOutputStream(output);
			transfert(resStream, fos);
			fos.close();
			resStream.close();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
}
