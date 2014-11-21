package controllers.journals;

import java.net.URL;
import java.sql.Timestamp;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;

import pr.log.LogFiles;
import single.ProgramProperty;
import single.SingleObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.text.Text;
import controllers.Controller;
import controllers.interfaces.IControllerInit;

public abstract class AJournal implements Initializable, IControllerInit {
	private final StringProperty localeName = new SimpleStringProperty();
	@FXML public JToolBarController tbJournalController;
	@FXML public TableController bpTableController;
	@FXML public Label lCount;
	@FXML public Text tCount;
	@FXML private ToolBar tbJournal;
	
	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL url, ResourceBundle bundle) {
		localeName.bind(ProgramProperty.localeName);
		localeName.addListener((observ, old, value) -> setElementText(Controller.getResourceBundle(new Locale(value))));
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

	public ToolBar getTbJournal() {
		return tbJournal;
	}
}
