package com.github.xsavikx.android.screencast.api.injector;

import com.android.ddmlib.SyncService.ISyncProgressMonitor;


public class NullSyncProgressMonitor implements ISyncProgressMonitor {

	public void advance(int arg0) {
	}

	public boolean isCanceled() {
		return false;
	}

	public void start(int arg0) {
	}

	public void startSubTask(String arg0) {
		
	}

	public void stop() {
	}

}
