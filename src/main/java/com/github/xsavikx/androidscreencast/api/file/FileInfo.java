package com.github.xsavikx.androidscreencast.api.file;

import com.github.xsavikx.androidscreencast.api.AndroidDeviceImpl;
import com.github.xsavikx.androidscreencast.exception.IORuntimeException;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;

@Singleton
public final class FileInfo {

    public AndroidDeviceImpl device;
    public String path;
    public String attribs;
    public boolean directory;
    public String name;

    @Inject
    public FileInfo() {
    }

    public File downloadTemporary() {
        try {
            File tempFile = File.createTempFile("android", name);
            device.pullFile(path + name, tempFile);
            tempFile.deleteOnExit();
            return tempFile;
        } catch (IOException ex) {
            throw new IORuntimeException(ex);
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
