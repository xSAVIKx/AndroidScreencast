package com.github.xsavikx.android.screencast.api.file;

import java.io.File;

import com.github.xsavikx.android.screencast.api.AndroidDevice;

public class FileInfo {

	public AndroidDevice device;
	public String path;
	public String attribs;
	public boolean directory;
	public String name;
	
	public File downloadTemporary() {
		try {
			File tempFile = File.createTempFile("android", name);
			device.pullFile(path + name, tempFile);
			tempFile.deleteOnExit();
			return tempFile;
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public String toString() {
		return name;
	}
	
}
