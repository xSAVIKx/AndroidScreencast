package com.github.xsavikx.androidscreencast.api.file;

import com.github.xsavikx.androidscreencast.api.AndroidDeviceImpl;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class FileInfo {
    public AndroidDeviceImpl device;
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
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public String toString() {
        return name;
    }

}
