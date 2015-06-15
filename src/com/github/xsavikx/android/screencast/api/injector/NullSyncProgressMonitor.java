package com.github.xsavikx.android.screencast.api.injector;

import com.android.ddmlib.SyncService.ISyncProgressMonitor;

public class NullSyncProgressMonitor implements ISyncProgressMonitor {

  @Override
  public void advance(int arg0) {
  }

  @Override
  public boolean isCanceled() {
    return false;
  }

  @Override
  public void start(int arg0) {
  }

  @Override
  public void startSubTask(String arg0) {

  }

  @Override
  public void stop() {
  }

}
