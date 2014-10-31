package controllers.journals;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.List;
import java.util.ResourceBundle;

import pr.model.Alarm;
import ui.MainStage;
import ui.Scheme;
import ui.tables.AlarmTableItem;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;

public class JAlarmsController extends AJournal {
	private final BooleanProperty selectedShapeChangeProperty = new SimpleBooleanProperty();
	
	private boolean isAlarmById = false;
	
	@FXML private AlarmTableController bpTableController;
	
	public AlarmTableController getAlarmsController() {
		return bpTableController;
	}

	public void setAlarmById(boolean isAlarmById) {
		this.isAlarmById = isAlarmById;
		
		if (isAlarmById) {
			setItems(Timestamp.valueOf(getTbJournalController().dpBegin.getValue().atTime(0, 0)), 
					Timestamp.valueOf(getTbJournalController().dpEnd.getValue().atTime(0, 0)));
			
			selectedShapeChangeProperty.bind(Scheme.selectedShapeChangeProperty);
			selectedShapeChangeProperty.addListener((observable, oldValue, newValue) -> {
				if (newValue) {
					setItems(Timestamp.valueOf(getTbJournalController().dpBegin.getValue().atTime(0, 0)), 
							Timestamp.valueOf(getTbJournalController().dpEnd.getValue().atTime(0, 0)));
				}
			});
		} else {
			setItems(Timestamp.valueOf(getTbJournalController().dpBegin.getValue().atTime(0, 0)), 
					Timestamp.valueOf(getTbJournalController().dpEnd.getValue().atTime(0, 0)));
		}
	}
	
	@Override
	public void setItems(Timestamp dtBeg, Timestamp dtEnd) {
		try {
			List<Alarm> alarms = null;
			if (isAlarmById) {
				alarms = MainStage.psClient.getAlarmsPeriodById(dtBeg, dtEnd, 
						Scheme.selectedShape.getIdTS() > 0 ? Scheme.selectedShape.getIdTS() : Scheme.selectedShape.getIdSignal());
			} else {
				alarms = MainStage.psClient.getAlarmsPeriod(dtBeg, dtEnd);
			}
			
			bpTableController.clearTable();
			if (alarms != null) alarms.forEach(it -> bpTableController.addItem(new AlarmTableItem(it)));
		} catch (RemoteException e1) {
			e1.printStackTrace();
		}
	}
	
	@Override
	public void setElementText(ResourceBundle rb) {
		
	}
}
