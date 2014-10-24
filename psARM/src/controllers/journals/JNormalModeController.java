package controllers.journals;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import controllers.interfaces.IControllerInit;

public class JNormalModeController implements Initializable, IControllerInit {

	@FXML private JToolBarController tbJournalController;
	@FXML private NormalModeTableController bpNormalModeController;
	@FXML Label lCount;
	@FXML Text tCount;

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		tCount.textProperty().bind(bpNormalModeController.getCountProperty());
		tbJournalController.dpBegin.setOnHidden(new DateHandler());
		tbJournalController.dpEnd.setOnHidden(new DateHandler());
	}
	
	@SuppressWarnings("rawtypes")
	private class DateHandler implements EventHandler {
		@Override
		public void handle(Event e) {
			System.out.println("qqq");
		}
	}
	
	@Override
	public void setElementText(ResourceBundle rb) {
		lCount.setText(rb.getString("keyCount"));
	}

}
