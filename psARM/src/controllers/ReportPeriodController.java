package controllers;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.Locale;
import java.util.ResourceBundle;

import controllers.interfaces.IControllerInit;
import controllers.interfaces.StageLoader;
import controllers.journals.JToolBarController;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import single.ProgramProperty;
import single.SingleFromDB;
import single.SingleObject;

public class ReportPeriodController implements Initializable, IControllerInit {
	private final StringProperty localeName = new SimpleStringProperty();
	private int idReport;
	
	@FXML public JToolBarController tbJournalController;
	@FXML Button btnOK;
	@FXML Button btnCancel;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ResourceBundle rb = Controller.getResourceBundle(new Locale(SingleObject.getProgramSettings().getLocaleName()));
		setElementText(rb);
		localeName.bind(ProgramProperty.localeName);
		localeName.addListener((observ, old, value) -> setElementText(Controller.getResourceBundle(new Locale(value))));
	}
	
	@Override
	public void setElementText(ResourceBundle rb) {
		if (btnOK.getScene() != null) ((Stage)btnOK.getScene().getWindow()).setTitle(rb.getString("keyPeriod"));
		btnOK.setText(rb.getString("keyApply"));
		btnCancel.setText(rb.getString("keyCancel"));
	}
	
	@FXML public void okAction() {
		String content = "Error in report";
		try {
			content = SingleFromDB.psClient.getReportById(idReport, 
					tbJournalController.dpBegin.getValue(), tbJournalController.dpEnd.getValue());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		StageLoader stage = new StageLoader("Report.xml", 
				SingleObject.getResourceBundle().getString("keyReports"), true);
		
		ReportController controller = (ReportController) stage.getController();
		controller.setContent(content);
		
		stage.show();
		
		cancelAction();
	}
	
	@FXML public void cancelAction() {
		((Stage)btnOK.getScene().getWindow()).close();
	}

	public void setIdReport(int idReport) {
		this.idReport = idReport;
	}
}
