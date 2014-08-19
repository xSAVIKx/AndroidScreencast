package com.github.xsavikx.android.screencast.app;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.github.xsavikx.android.screencast.ui.JDialogError;

public class SwingApplication extends Application {

	JDialogError jd = null;

	public SwingApplication(boolean nativeLook) {
		try {
			if(nativeLook)
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	@Override
	protected void close() {
		super.close();
	}

	@Override
	protected void handleException(Thread thread, Throwable ex) {
		try {
			StringWriter sw = new StringWriter();
			ex.printStackTrace(new PrintWriter(sw));
			if(sw.toString().contains("SynthTreeUI"))
				return;
			ex.printStackTrace(System.err);
			if(jd != null && jd.isVisible())
				return;
			jd = new JDialogError(ex);
			SwingUtilities.invokeLater(new Runnable() {
				
				public void run() {
					jd.setVisible(true);
					
				}
			});
		} catch(Exception ex2) {
			// ignored
		}
	}
	
	

}
