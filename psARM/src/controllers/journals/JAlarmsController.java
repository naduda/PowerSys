package controllers.journals;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;

import pr.log.LogFiles;
import pr.model.Alarm;
import single.ProgramProperty;
import single.SingleFromDB;
import single.SingleObject;
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
			
			selectedShapeChangeProperty.bind(ProgramProperty.selectedShapeChangeProperty);
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
			List<Alarm> alarms;
			if (isAlarmById) {
				alarms = SingleFromDB.psClient.getAlarmsPeriodById(dtBeg, dtEnd,
						SingleObject.selectedShape.getIdTS() > 0 ?
								SingleObject.selectedShape.getIdTS() : SingleObject.selectedShape.getIdSignal());
			} else {
				alarms = SingleFromDB.psClient.getAlarmsPeriod(dtBeg, dtEnd);
			}
			
			bpTableController.clearTable();
			if (alarms != null) alarms.forEach(it -> bpTableController.addItem(new AlarmTableItem(it)));
		} catch (RemoteException e) {
			LogFiles.log.log(Level.SEVERE, "void setItems(...)", e);
		}
	}
	
	@Override //We need kill super method. Don't remove it )))
	public void setElementText(ResourceBundle rb) {
		
	}
	
}
