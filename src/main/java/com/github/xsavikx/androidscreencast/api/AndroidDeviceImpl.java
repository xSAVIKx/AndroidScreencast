package com.github.xsavikx.androidscreencast.api;

import com.android.ddmlib.IDevice;
import com.android.ddmlib.SyncService;
import com.github.xsavikx.androidscreencast.api.file.FileInfo;
import com.github.xsavikx.androidscreencast.api.injector.OutputStreamShellOutputReceiver;
import com.github.xsavikx.androidscreencast.exception.AndroidScreenCastRuntimeException;
import com.github.xsavikx.androidscreencast.exception.ExecuteCommandException;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@Singleton
public final class AndroidDeviceImpl implements AndroidDevice {

    private final IDevice device;

    @Inject
    public AndroidDeviceImpl(final IDevice device) {
        this.device = device;
    }

    @Override
    public String executeCommand(final String cmd) {
        log().debug("Executing command: `{}`.", cmd);
        try (final ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            device.executeShellCommand(cmd, new OutputStreamShellOutputReceiver(bos));
            final String result = new String(bos.toByteArray(), StandardCharsets.UTF_8);
            log().debug("Command `{}` executed with result: `{}`.", cmd, result);
            return result;
        } catch (final Exception ex) {
            log().error("Unable to execute command `{}`.", cmd, ex);
            throw new ExecuteCommandException(cmd);
        }
    }

    @Override
    public List<FileInfo> list(final String path) {
        log().debug("Listing files under path: `{}`.", path);
        try {
            final String s = executeCommand("ls -l " + path);
            final String[] entries = s.split("\r\n");
            final List<FileInfo> fileInfos = new ArrayList<>();
            for (final String entry : entries) {
                String[] data = entry.split(" ");
                if (data.length < 4)
                    continue;
                String attributes = data[0];
                boolean directory = attributes.charAt(0) == 'd';
                String name = data[data.length - 1];

                final FileInfo fi = new FileInfo();
                fi.attribs = attributes;
                fi.directory = directory;
                fi.name = name;
                fi.path = path;
                fi.device = this;

                fileInfos.add(fi);
            }
            log().debug("Found `{}` files under path `{}`.", fileInfos.size(), path);
            return fileInfos;
        } catch (final Exception ex) {
            log().error("Unable to list files under path `{}`.", path, ex);
            throw new AndroidScreenCastRuntimeException(ex);
        }
    }

    @Override
    public void openUrl(final String url) {
        log().debug("Opening URL: `{}`.", url);
        executeCommand("am start " + url);
    }

    @Override
    public void pullFile(final String remote, final File local) {
        log().debug("Pulling remote file `{}` to the local destination: `{}`.", remote, local);
        // ugly hack to call the method without FileEntry
        try {
            if (device.getSyncService() == null)
                throw new AndroidScreenCastRuntimeException("SyncService is null, ADB crashed ?");
            device.getSyncService().pullFile(remote, local.getAbsolutePath(), SyncService.getNullProgressMonitor());
            log().debug("Remote file `{}` pulled to the local destination: `{}`.", remote, local);
        } catch (final Exception ex) {
            log().error("Unable to pull remote file `{}` to the local destination: `{}`.", remote, local, ex);
            throw new AndroidScreenCastRuntimeException(ex);
        }
    }

    @Override
    public void pushFile(final File local, final String remote) {
        log().debug("Pushing local file `{}` to the remote destination: `{}`.", local, remote);
        try {
            if (device.getSyncService() == null)
                throw new AndroidScreenCastRuntimeException("SyncService is null, ADB crashed ?");
            device.getSyncService().pushFile(local.getAbsolutePath(), remote, SyncService.getNullProgressMonitor());
            log().debug("Local file `{}` pushed to the remote destination: `{}`.", local, remote);
        } catch (final Exception ex) {
            log().error("Unable to push local file `{}` to the remote destination: `{}`.", local, remote, ex);
            throw new AndroidScreenCastRuntimeException(ex);
        }
    }

    private enum LogSingleton {
        INSTANCE;

        @SuppressWarnings({"NonSerializableFieldInSerializableClass", "ImmutableEnumChecker"})
        private final Logger value = getLogger(AndroidDeviceImpl.class);
    }

    private static Logger log() {
        return LogSingleton.INSTANCE.value;
    }
}
