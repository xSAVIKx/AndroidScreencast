package com.github.xsavikx.android.screencast.api.injector;

import java.io.IOException;
import java.io.OutputStream;

import com.android.ddmlib.IShellOutputReceiver;

public class OutputStreamShellOutputReceiver implements IShellOutputReceiver {

	OutputStream os;

	public OutputStreamShellOutputReceiver(OutputStream os) {
		this.os = os;
	}

	public boolean isCancelled() {
		return false;
	}

	public void flush() {
		try {
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addOutput(byte[] buf, int off, int len) {
		try {
			os.write(buf, off, len);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

}
