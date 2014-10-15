package controllers;

import java.net.URL;
import java.time.LocalDate;
import java.util.Locale;
import java.util.ResourceBundle;

import ui.Main;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;

public class JToolBarController implements Initializable {

	@FXML DatePicker dpBegin;
	@FXML DatePicker dpEnd;
	@FXML Label lPeriodFrom;
	@FXML Label lTo;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		dpEnd.setValue(LocalDate.now().plusDays(1));
		dpBegin.setValue(LocalDate.now());
		
		ResourceBundle rb = Controller.getResourceBundle(new Locale(Main.getProgramSettings().getLocaleName()));
		setElementText(rb);
	}
	
	public void setElementText(ResourceBundle rb) {
		lPeriodFrom.setText(rb.getString("keyPeriodFrom"));
		lTo.setText(rb.getString("keyTo"));
	}
}
