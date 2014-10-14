package controllers;

import java.util.ResourceBundle;

import ui.alarm.AlarmTableItem;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class TrCommentController {

	@FXML Button btnOK;
	@FXML Button btnCancel;
	@FXML TextArea txtArea;
	
	private AlarmTableItem alarmTableItem;
	
	@FXML 
	protected void btnOK(ActionEvent event) {
		AlarmController.confirmAlarm(alarmTableItem, txtArea.getText());
		closeWindowTransparant(event);
	}
	
	@FXML 
	protected void btnCancel(ActionEvent event) {
		closeWindowTransparant(event);
	}
	
	private void closeWindowTransparant (ActionEvent event) {
		((Stage)((Button)event.getSource()).getScene().getWindow()).close();
	}
	
	public void setElementText(ResourceBundle rb) {
		btnOK.setText(rb.getString("keySet"));
		btnCancel.setText(rb.getString("keyCancel"));
	}

	public void setAlarmTableItem(AlarmTableItem ati) {
		this.alarmTableItem = ati;
	}
}
