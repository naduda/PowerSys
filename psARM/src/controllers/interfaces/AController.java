package controllers.interfaces;

import java.util.Locale;
import java.util.ResourceBundle;

import single.ProgramProperty;
import single.SingleObject;
import controllers.Controller;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public abstract class AController {
	private final StringProperty localeName = new SimpleStringProperty();
	
	public void init() {
		setElementText(SingleObject.getResourceBundle());
		localeName.bind(ProgramProperty.localeName);
		localeName.addListener((observ, old, value) -> setElementText(Controller.getResourceBundle(new Locale(value))));
	}
	
	public abstract void setElementText(ResourceBundle rb);
	
	public void setStageLoader(StageLoader stage) {
		
	}
}
