package controllers;

import java.net.URL;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.List;
import java.util.ResourceBundle;

import pr.model.Alarm;
import ui.MainStage;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

public class JAlarmsController implements Initializable {
	@FXML private AlarmController bpAlarmsController;
	
	@FXML private JToolBarController tbAlarmsController;
	
	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		tbAlarmsController.dpBegin.setOnHidden(new DateHandler());
		tbAlarmsController.dpEnd.setOnHidden(new DateHandler());
	}
	
	public void setAlarms() {
		try {
			MainStage.psClient.getAlarmsCurrentDay().forEach(a -> { bpAlarmsController.addAlarm(a); });
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public AlarmController getAlarmsController() {
		return bpAlarmsController;
	}

	@SuppressWarnings("rawtypes")
	private class DateHandler implements EventHandler {
		@Override
		public void handle(Event e) {
			try {
				List<Alarm> alarms = MainStage.psClient.getAlarmsPeriod(Timestamp.valueOf(tbAlarmsController.dpBegin.getValue().atTime(0, 0)), 
						Timestamp.valueOf(tbAlarmsController.dpEnd.getValue().atTime(0, 0)));
				
				bpAlarmsController.clearTable();
				alarms.forEach(a -> { bpAlarmsController.addAlarm(a); });
			} catch (RemoteException e1) {
				e1.printStackTrace();
			}
		}
	}
}
