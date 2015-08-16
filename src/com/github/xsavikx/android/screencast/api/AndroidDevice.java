package com.github.xsavikx.android.screencast.api;

import java.io.File;
import java.util.List;

import com.github.xsavikx.android.screencast.api.file.FileInfo;

public interface AndroidDevice {
  String executeCommand(String command);

  List<FileInfo> list(String path);

  void openUrl(String url);

  void pullFile(String remoteFrom, File localTo);

  void pushFile(File localFrom, String remoteTo);
}
