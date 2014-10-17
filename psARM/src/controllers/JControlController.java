package controllers;

import java.net.URL;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.List;
import java.util.ResourceBundle;

import controllers.interfaces.IControllerInit;
import pr.model.ControlJournalItem;
import ui.Main;
import ui.MainStage;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

public class JControlController implements Initializable, IControllerInit {

	@FXML private JToolBarController tbJournalController;
	@FXML private ControlsTableController bpControlsController;
	@FXML Label lCount;
	@FXML Text tCount;
	
	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL url, ResourceBundle bundle) {
		tCount.textProperty().bind(bpControlsController.getCountProperty());
		tbJournalController.dpBegin.setOnHidden(new DateHandler());
		tbJournalController.dpEnd.setOnHidden(new DateHandler());
		
		try {
			MainStage.psClient.getJContrlItems(Timestamp.valueOf(tbJournalController.dpBegin.getValue().atTime(0, 0)), 
					Timestamp.valueOf(tbJournalController.dpEnd.getValue().atTime(0, 0))).forEach(bpControlsController::addConrolRow);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		setElementText(Main.getResourceBundle());
	}

	@SuppressWarnings("rawtypes")
	private class DateHandler implements EventHandler {
		@Override
		public void handle(Event e) {
			try {
				bpControlsController.clearTable();
				List<ControlJournalItem> controls = MainStage.psClient.getJContrlItems(Timestamp.valueOf(tbJournalController.dpBegin.getValue().atTime(0, 0)), 
						Timestamp.valueOf(tbJournalController.dpEnd.getValue().atTime(0, 0)));
				controls.forEach(bpControlsController::addConrolRow);
			} catch (RemoteException e1) {
				System.out.println("/");
				e1.printStackTrace();
			}
		}
	}

	@Override
	public void setElementText(ResourceBundle rb) {
		lCount.setText(rb.getString("keyCount"));
	}
}
