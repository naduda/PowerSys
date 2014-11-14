package controllers.journals;

import java.net.URL;
import java.sql.Timestamp;
import java.util.ResourceBundle;
import java.util.logging.Level;

import pr.log.LogFiles;
import single.SingleObject;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import controllers.interfaces.IControllerInit;

public abstract class AJournal implements Initializable, IControllerInit {
	@FXML public JToolBarController tbJournalController;
	@FXML public TableController bpTableController;
	@FXML public Label lCount;
	@FXML public Text tCount;
	
	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL url, ResourceBundle bundle) {
		try {
			tCount.textProperty().bind(bpTableController.getCountProperty());
		} catch (Exception e) {
			LogFiles.log.log(Level.INFO, "AlarmJournal structure");
		}
		
		tbJournalController.dpBegin.setOnHidden(new DateHandler());
		tbJournalController.dpEnd.setOnHidden(new DateHandler());
		
		setItems(Timestamp.valueOf(tbJournalController.dpBegin.getValue().atTime(0, 0)),
				Timestamp.valueOf(tbJournalController.dpEnd.getValue().atTime(0, 0)));
		
		setElementText(SingleObject.getResourceBundle());
	}

	abstract void setItems(Timestamp dtBeg, Timestamp dtEnd);
	
	@SuppressWarnings("rawtypes")
	private class DateHandler implements EventHandler {
		@Override
		public void handle(Event e) {
			setItems(Timestamp.valueOf(tbJournalController.dpBegin.getValue().atTime(0, 0)),
					Timestamp.valueOf(tbJournalController.dpEnd.getValue().atTime(0, 0)));
		}
	}

	@Override
	public void setElementText(ResourceBundle rb) {
		lCount.setText(rb.getString("keyCount"));
	}

	public JToolBarController getTbJournalController() {
		return tbJournalController;
	}
}
