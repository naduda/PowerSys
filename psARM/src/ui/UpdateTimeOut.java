package ui;

import java.util.List;
import java.util.logging.Level;

import pr.log.LogFiles;
import single.SingleObject;
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
				if (SingleObject.mainScheme != null) {
					List<Integer> signals = type_ == 1 ? SingleObject.mainScheme.getSignalsTI() : SingleObject.mainScheme.getSignalsTS();
					
					signals.forEach(s -> Platform.runLater(() -> Controller.updateSignal(s, type_, sec)));
				}
				
				Thread.sleep(sec * 1000 / 2);
			}
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, "void run()", e);
		}
	}

}
