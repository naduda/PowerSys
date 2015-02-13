package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ProgressStageController {
	@FXML private Label message;
	
	public Label getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message.setText(message);
	}
}
