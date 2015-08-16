package com.github.xsavikx.android.screencast.api;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.android.ddmlib.IDevice;
import com.android.ddmlib.SyncService;
import com.android.ddmlib.SyncService.ISyncProgressMonitor;
import com.github.xsavikx.android.screencast.api.file.FileInfo;
import com.github.xsavikx.android.screencast.api.injector.OutputStreamShellOutputReceiver;

@Component
public class AndroidDeviceImpl implements AndroidDevice {
  /**
   * Logger for this class
   */
  private static final Logger logger = Logger.getLogger(AndroidDeviceImpl.class);
  @Autowired(required = false)
  private IDevice device;

  public AndroidDeviceImpl() {

  }

  public AndroidDeviceImpl(IDevice device) {
    this.device = device;
  }

  @Override
  public String executeCommand(String cmd) {
    if (logger.isDebugEnabled()) {
      logger.debug("executeCommand(String) - start");
    }

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    try {
      device.executeShellCommand(cmd, new OutputStreamShellOutputReceiver(bos));
      String returnString = new String(bos.toByteArray(), "UTF-8");
      if (logger.isDebugEnabled()) {
        logger.debug("executeCommand(String) - end");
      }
      return returnString;
    } catch (Exception ex) {
      logger.error("executeCommand(String)", ex);

      throw new RuntimeException(ex);
    }
  }

  @Override
  public List<FileInfo> list(String path) {
    if (logger.isDebugEnabled()) {
      logger.debug("list(String) - start");
    }

    try {
      String s = executeCommand("ls -l " + path);
      String[] entries = s.split("\r\n");
      Vector<FileInfo> liste = new Vector<FileInfo>();
      for (int i = 0; i < entries.length; i++) {
        String[] data = entries[i].split(" ");
        if (data.length < 4)
          continue;
        /*
         * for(int j=0; j<data.length; j++) { System.out.println(j+" = "
         * +data[j]); }
         */
        String attribs = data[0];
        boolean directory = attribs.startsWith("d");
        String name = data[data.length - 1];

        FileInfo fi = new FileInfo();
        fi.attribs = attribs;
        fi.directory = directory;
        fi.name = name;
        fi.path = path;
        fi.device = this;

        liste.add(fi);
      }

      if (logger.isDebugEnabled()) {
        logger.debug("list(String) - end");
      }
      return liste;
    } catch (Exception ex) {
      logger.error("list(String)", ex);

      throw new RuntimeException(ex);
    }
  }

  @Override
  public void openUrl(String url) {
    if (logger.isDebugEnabled()) {
      logger.debug("openUrl(String) - start");
    }

    executeCommand("am start " + url);

    if (logger.isDebugEnabled()) {
      logger.debug("openUrl(String) - end");
    }
  }

  @Override
  public void pullFile(String removeFrom, File localTo) {
    if (logger.isDebugEnabled()) {
      logger.debug("pullFile(String, File) - start");
    }

    // ugly hack to call the method without FileEntry
    try {
      if (device.getSyncService() == null)
        throw new RuntimeException("SyncService is null, ADB crashed ?");
      Method m = device.getSyncService().getClass().getDeclaredMethod("doPullFile", String.class, String.class,
          ISyncProgressMonitor.class);
      m.setAccessible(true);
      device.getSyncService();
      m.invoke(device.getSyncService(), removeFrom, localTo.getAbsolutePath(), SyncService.getNullProgressMonitor());
    } catch (Exception ex) {
      logger.error("pullFile(String, File)", ex);

      throw new RuntimeException(ex);
    }

    if (logger.isDebugEnabled()) {
      logger.debug("pullFile(String, File) - end");
    }
  }

  @Override
  public void pushFile(File localFrom, String remoteTo) {
    if (logger.isDebugEnabled()) {
      logger.debug("pushFile(File, String) - start");
    }

    try {
      if (device.getSyncService() == null)
        throw new RuntimeException("SyncService is null, ADB crashed ?");

      device.getSyncService().pushFile(localFrom.getAbsolutePath(), remoteTo, SyncService.getNullProgressMonitor());

    } catch (Exception ex) {
      logger.error("pushFile(File, String)", ex);

      throw new RuntimeException(ex);
    }

    if (logger.isDebugEnabled()) {
      logger.debug("pushFile(File, String) - end");
    }
  }

}
