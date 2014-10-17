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
	
	@FXML private JToolBarController tbJournalController;
	
	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL url, ResourceBundle bundle) {
		tbJournalController.dpBegin.setOnHidden(new DateHandler());
		tbJournalController.dpEnd.setOnHidden(new DateHandler());
	}
	
	public AlarmController getAlarmsController() {
		return bpAlarmsController;
	}

	@SuppressWarnings("rawtypes")
	private class DateHandler implements EventHandler {
		@Override
		public void handle(Event e) {
			try {
				List<Alarm> alarms = MainStage.psClient.getAlarmsPeriod(Timestamp.valueOf(tbJournalController.dpBegin.getValue().atTime(0, 0)), 
						Timestamp.valueOf(tbJournalController.dpEnd.getValue().atTime(0, 0)));
				
				bpAlarmsController.clearTable();
				alarms.forEach(bpAlarmsController::addAlarm);
			} catch (RemoteException e1) {
				e1.printStackTrace();
			}
		}
	}
}
