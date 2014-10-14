package controllers;

import java.rmi.RemoteException;

import ui.MainStage;
import javafx.fxml.FXML;

public class JAlarmsController {
	@FXML private AlarmController bpAlarmsController;
	
	public void setAlarms() {
		try {
			MainStage.psClient.getAlarmsCurrentDay().forEach(a -> { bpAlarmsController.addAlarm(a); });
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}
