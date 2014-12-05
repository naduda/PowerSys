package controllers;

import java.rmi.RemoteException;
import java.util.ResourceBundle;
import java.util.logging.Level;

import pr.log.LogFiles;
import controllers.interfaces.IControllerInit;
import controllers.journals.AlarmTableController;
import single.SingleFromDB;
import single.SingleObject;
import ui.MainStage;
import ui.tables.AlarmTableItem;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class TrCommentController implements IControllerInit {

	@FXML private Button btnOK;
	@FXML private Button btnCancel;
	@FXML private TextArea txtArea;
	
	private AlarmTableItem alarmTableItem;
	
	@FXML 
	protected void btnOK(ActionEvent event) {
		AlarmTableController.confirmAlarm(alarmTableItem, txtArea.getText());
		MainStage.controller.setNotConfirmed(alarmTableItem.getAlarm().getObjref(), true);
		try {
			SingleFromDB.psClient.getNotConfirmedSignals(SingleObject.activeSchemeSignals)
				.forEach(s -> MainStage.controller.setNotConfirmed(s, false));
		} catch (RemoteException e) {
			LogFiles.log.log(Level.SEVERE, "... getNotConfirmedSignals", e);
		}
		closeWindowTransparant(event);
	}
	
	@FXML 
	protected void btnCancel(ActionEvent event) {
		closeWindowTransparant(event);
	}
	
	private void closeWindowTransparant (ActionEvent event) {
		((Stage)((Button)event.getSource()).getScene().getWindow()).close();
	}
	
	@Override
	public void setElementText(ResourceBundle rb) {
		btnOK.setText(rb.getString("keySet"));
		btnCancel.setText(rb.getString("keyCancel"));
	}

	public void setAlarmTableItem(AlarmTableItem ati) {
		this.alarmTableItem = ati;
	}
}
