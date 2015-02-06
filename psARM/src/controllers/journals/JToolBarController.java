package controllers.journals;

import java.net.URL;
import java.time.LocalDate;
import java.util.Locale;
import java.util.ResourceBundle;

import controllers.Controller;
import controllers.interfaces.IControllerInit;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import single.ProgramProperty;
import single.SingleObject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;

public class JToolBarController implements Initializable, IControllerInit {
	private final StringProperty localeName = new SimpleStringProperty();
	@FXML public DatePicker dpBegin;
	@FXML public DatePicker dpEnd;
	@FXML private Label lPeriodFrom;
	@FXML private Label lTo;
	
	@Override
	public void initialize(URL url, ResourceBundle bundle) {
		localeName.bind(ProgramProperty.localeName);
		localeName.addListener((observ, old, value) -> {
			ResourceBundle rb = Controller.getResourceBundle(new Locale(value));
			setElementText(rb);
		});

		dpEnd.setValue(LocalDate.now().plusDays(1));
		dpBegin.setValue(LocalDate.now());
		
//		ResourceBundle rb = Controller.getResourceBundle(new Locale(SingleObject.getProgramSettings().getLocaleName()));
//		setElementText(rb);
		setElementText(SingleObject.getResourceBundle());
	}
	
	@Override
	public void setElementText(ResourceBundle rb) {
		lPeriodFrom.setText(rb.getString("keyPeriodFrom"));
		lTo.setText(rb.getString("keyTo"));
	}
}
