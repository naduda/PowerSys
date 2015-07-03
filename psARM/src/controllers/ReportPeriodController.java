package controllers;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;

import pr.log.LogFiles;
import pr.powersys.ObjectSerializable;
import controllers.interfaces.IControllerInit;
import controllers.interfaces.StageLoader;
import controllers.journals.JToolBarController;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import single.ProgramProperty;
import single.SingleFromDB;
import single.SingleObject;

public class ReportPeriodController implements Initializable, IControllerInit {
	private final StringProperty localeName = new SimpleStringProperty();
	private int idReport;
	
	@FXML public JToolBarController tbJournalController;
	@FXML private Button btnOK;
	@FXML private Button btnCancel;
	@FXML private ToggleGroup repToggleGroup;
	@FXML private RadioButton rbHTML;
	
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
		String format = ((RadioButton)repToggleGroup.getSelectedToggle()).getId().substring(2).toLowerCase();
		ObjectSerializable os = SingleFromDB.psClient.getReportById(idReport, 
				tbJournalController.dpBegin.getValue(), tbJournalController.dpEnd.getValue(), format);
		
		switch (format) {
		case "pdf":
		case "xls":
//			If you would like to use UTF-8 fonts with your report, you would have to place them on your classpath first and include them explicitly.
			if (os == null) return;
			FileChooser fileChooser = new FileChooser();
			FileChooser.ExtensionFilter extentionFilter = format.equals("xls") ? 
					new FileChooser.ExtensionFilter("XLS files (*.xls)", "*.xls") : new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf");
			fileChooser.getExtensionFilters().add(extentionFilter);
			fileChooser.setTitle(SingleObject.getResourceBundle().getString("keyTooltip_save"));
			File file = fileChooser.showSaveDialog(SingleObject.mainStage);
			if (file != null) {
				byte[] bArray = (byte[])os.getBaosSerializable();
								
				try (FileOutputStream fos = new FileOutputStream(file);) {
					fos.write(bArray);
					Desktop.getDesktop().open(file);
				} catch (IOException e) {
					LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
				}
			}
			break;
		case "html":
			String content = null;
			if (os == null) {
				content = "Error in report";
			} else {
				byte[] bArray = (byte[])os.getBaosSerializable();
				content = new String(bArray);
			}
			
			StageLoader stage = new StageLoader("Report.xml", 
					SingleObject.getResourceBundle().getString("keyReports"), true);
			
			ReportController controller = (ReportController) stage.getController();
			controller.setContent(content);
			
			stage.show();
			break;
		}
		
		cancelAction();
	}
	
	@FXML public void cancelAction() {
		((Stage)btnOK.getScene().getWindow()).close();
	}

	public void setIdReport(int idReport) {
		this.idReport = idReport;
	}
}
