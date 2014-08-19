package com.github.xsavikx.android.screencast.app;

import java.lang.Thread.UncaughtExceptionHandler;

public class Application {

	public Application() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				close();
			}
		});
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			public void uncaughtException(Thread arg0, Throwable ex) {
				try {
					handleException(arg0,ex);
				} catch(Exception ex2) {
					// ignored
					ex2.printStackTrace();
				}
			}
		});
	}

	protected void handleException(Thread thread, Throwable ex) {
	}
	
	protected void close() {
		
	}
	
}
