package ui;

import java.util.List;

import controllers.Controller;
import javafx.application.Platform;

public class UpdateTimeOut implements Runnable {
	
	private boolean isRun = true;
	private int sec;
	private int type_;
	
	public UpdateTimeOut(int sec, int t) {
		this.sec = sec;
		this.type_ = t;
		run();
	}

	@Override
	public void run() {
		try {
			while (isRun) {
				if (Main.mainScheme != null) {
					List<Integer> signals = type_ == 1 ? Main.mainScheme.getSignalsTI() : Main.mainScheme.getSignalsTS();
					
					signals.forEach(s -> {
						Platform.runLater(new Runnable() {
    	                    @Override public void run() {
    	                    	Controller.updateSignal(s, type_, sec);
    	                    }
    	                });		
					});
				}
				
				Thread.sleep(sec * 1000 / 2);
			}
		} catch (Exception e) {
			System.err.println("UpdateTimeOut ...");
		}
	}

}
