package ui;

import java.util.List;

import controllers.Controller;
import javafx.application.Platform;

public class UpdateTimeOut {

	private boolean isRun = true;
	private int sec;
	private int type_;
	
	public UpdateTimeOut(int sec, int t) {
		this.sec = sec;
		this.type_ = t;
		run();
	}

	public void run() {
		try {
			while (isRun) {
				if (Main.mainScheme != null) {
					List<Integer> signals = type_ == 1 ? Main.mainScheme.getSignalsTI() : Main.mainScheme.getSignalsTS();
					for (Integer signal : signals) {
						new Thread(new Runnable() {
		    	            @Override public void run() {
		    	                Platform.runLater(new Runnable() {
		    	                    @Override public void run() {
		    	                    	Controller.updateSignal(signal, type_, sec);
		    	                    }
		    	                });
		    	            }
		    	        }, "Update signal " + signal).start();
					}
				}
				
				Thread.sleep(sec * 1000 / 2);
			}
		} catch (Exception e) {
			System.err.println("UpdateTimeOut ...");
		}
	}

}
