package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ToolBarController {

	@FXML private Label lLastDate;
	
	@FXML 
	protected void testAction(ActionEvent event) {
		System.out.println("test ToolBarController " + lLastDate.hashCode());
	}
	
	@FXML 
	protected void exitButtonAction(ActionEvent event) {
		Controller.exitProgram();
	}

	public void updateLabel(String text) {
		lLastDate.setText(text);
	}

}
