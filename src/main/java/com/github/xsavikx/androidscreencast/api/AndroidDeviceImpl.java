package com.github.xsavikx.androidscreencast.api;

import com.android.ddmlib.IDevice;
import com.android.ddmlib.SyncService;
import com.android.ddmlib.SyncService.ISyncProgressMonitor;
import com.github.xsavikx.androidscreencast.api.file.FileInfo;
import com.github.xsavikx.androidscreencast.api.injector.OutputStreamShellOutputReceiver;
import com.github.xsavikx.androidscreencast.exception.AndroidScreenCastRuntimeException;
import com.github.xsavikx.androidscreencast.exception.ExecuteCommandException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Component
public class AndroidDeviceImpl implements AndroidDevice {
    private static final Logger LOGGER = LoggerFactory.getLogger(AndroidDeviceImpl.class);
    private final IDevice device;

    @Autowired
    public AndroidDeviceImpl(IDevice device) {
        this.device = device;
    }

    @Override
    public String executeCommand(String cmd) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("executeCommand(String) - start");
        }
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();) {
            device.executeShellCommand(cmd, new OutputStreamShellOutputReceiver(bos));
            String returnString = new String(bos.toByteArray(), "UTF-8");
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("executeCommand(String) - end");
            }
            return returnString;
        } catch (Exception ex) {
            LOGGER.error("executeCommand(String)", ex);
            throw new ExecuteCommandException(cmd);
        }
    }

    @Override
    public List<FileInfo> list(String path) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("list(String) - start");
        }

        try {
            String s = executeCommand("ls -l " + path);
            String[] entries = s.split("\r\n");
            List<FileInfo> fileInfos = new ArrayList<>();
            for (String entry : entries) {
                String[] data = entry.split(" ");
                if (data.length < 4)
                    continue;
                String attributes = data[0];
                boolean directory = attributes.charAt(0) == 'd';
                String name = data[data.length - 1];

                FileInfo fi = new FileInfo();
                fi.attribs = attributes;
                fi.directory = directory;
                fi.name = name;
                fi.path = path;
                fi.device = this;

                fileInfos.add(fi);
            }

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("list(String) - end");
            }
            return fileInfos;
        } catch (Exception ex) {
            LOGGER.error("list(String)", ex);
            throw new AndroidScreenCastRuntimeException(ex);
        }
    }

    @Override
    public void openUrl(String url) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("openUrl(String) - start");
        }

        executeCommand("am start " + url);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("openUrl(String) - end");
        }
    }

    @Override
    public void pullFile(String removeFrom, File localTo) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("pullFile(String, File) - start");
        }

        // ugly hack to call the method without FileEntry
        try {
            if (device.getSyncService() == null)
                throw new AndroidScreenCastRuntimeException("SyncService is null, ADB crashed ?");
            Method m = device.getSyncService().getClass().getDeclaredMethod("doPullFile", String.class, String.class,
                    ISyncProgressMonitor.class);
            m.setAccessible(true);
            device.getSyncService();
            m.invoke(device.getSyncService(), removeFrom, localTo.getAbsolutePath(), SyncService.getNullProgressMonitor());
        } catch (Exception ex) {
            LOGGER.error("pullFile(String, File)", ex);

            throw new AndroidScreenCastRuntimeException(ex);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("pullFile(String, File) - end");
        }
    }

    @Override
    public void pushFile(File localFrom, String remoteTo) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("pushFile(File, String) - start");
        }

        try {
            if (device.getSyncService() == null)
                throw new AndroidScreenCastRuntimeException("SyncService is null, ADB crashed ?");

            device.getSyncService().pushFile(localFrom.getAbsolutePath(), remoteTo, SyncService.getNullProgressMonitor());

        } catch (Exception ex) {
            LOGGER.error("pushFile(File, String)", ex);

            throw new AndroidScreenCastRuntimeException(ex);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("pushFile(File, String) - end");
        }
    }

}
