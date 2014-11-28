package controllers;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import single.ProgramProperty;
import single.SingleObject;
import controllers.interfaces.IControllerInit;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class ReportController implements Initializable, IControllerInit {
	private final StringProperty localeName = new SimpleStringProperty();
	
	@FXML WebView webView;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ResourceBundle rb = Controller.getResourceBundle(new Locale(SingleObject.getProgramSettings().getLocaleName()));
		setElementText(rb);
		localeName.bind(ProgramProperty.localeName);
		localeName.addListener((observ, old, value) -> setElementText(Controller.getResourceBundle(new Locale(value))));
	}
	
	@Override
	public void setElementText(ResourceBundle rb) {
		if (webView.getScene() != null) ((Stage)webView.getScene().getWindow()).setTitle(rb.getString("keyReports"));
	}
	
	public void setContent(String content) {
		webView.getEngine().loadContent(content);
	}
}
