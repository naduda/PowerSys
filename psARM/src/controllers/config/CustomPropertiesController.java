package controllers.config;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;

import pr.common.Utils;
import pr.log.LogFiles;
import pr.svgObjects.CP;
import pr.svgObjects.CustProps;
import single.ProgramProperty;
import single.SingleObject;
import controllers.interfaces.AController;
import controllers.interfaces.StageLoader;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class CustomPropertiesController extends AController implements Initializable {
	private final BooleanProperty selectedShapeChangeProperty = new SimpleBooleanProperty();
	private StageLoader tSignal = null;
	private SignalTreeController tSignalsController = null;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		init();
		selectedShapeChangeProperty.bind(ProgramProperty.selectedShapeChangeProperty);
		
		selectedShapeChangeProperty.addListener((observable, oldValue, newValue) -> {
			if (newValue) updateStage();
		});
	}
	
	public void updateStage() {
		gridPane.getChildren().clear();
		gridPaneSize = 0;
		
		CustProps cProps = SingleObject.selectedShape.getSvgGroup().getCustProps();
		if (cProps != null) {
			cProps.getCustomProps().forEach(p -> {
				setCP(p);
			});
		}
	}
	
	@FXML Button btnOK;
	@FXML Button btnCancel;
	@FXML private GridPane gridPane;
	private int gridPaneSize;
	private String edit;
	private String delete;
	
	@FXML protected void btnOK() {
		SingleObject.svgModel.setObject(SingleObject.svg.getFileName(), SingleObject.svg);
		btnCancel();
	}
	
	@FXML protected void btnCancel() {
		((Stage)gridPane.getScene().getWindow()).hide();
	}
	
	@FXML protected void add() {
		StageLoader newProp = new StageLoader("config/AddCustProp.xml", 
				SingleObject.getResourceBundle().getString("keyNewPropertyTitle"), true);
		
		newProp.showAndWait();
		updateStage();
	}
	
	@FXML protected void script() {
		
	}
	
	public void setCP(CP prop) {
		Label lbl = new Label(prop.getLbl());
		lbl.setId("lbl_" + gridPaneSize);
		gridPane.add(lbl, 0, gridPaneSize);
		
		TextField tField = new TextField(prop.getVal());
		tField.setId("txt_" + gridPaneSize);
		tField.textProperty().addListener((observ, oldValue, newVAlue) -> {
			prop.setVal(newVAlue);
		});
		gridPane.add(tField, 1, gridPaneSize);
		
		Button btnEdit = new Button(edit);
		btnEdit.setId("btnEdit_" + gridPaneSize);
		btnEdit.setOnAction(a -> {
			if (lbl.getText().toLowerCase().equals("id") || lbl.getText().toLowerCase().equals("idts")) {
				if (tSignal == null) {
					tSignal = new StageLoader("config/SignalTree.xml", 
						SingleObject.getResourceBundle().getString("keySignalTreeTitle"), true);
					tSignalsController = (SignalTreeController) tSignal.getController();
				}
				tSignalsController.setTextField(tField);
				if (prop.getVal() != null) {
					try {
						int idSignal = Integer.parseInt(prop.getVal());
						tSignalsController.selectNode(idSignal);
					} catch (Exception e) {
						LogFiles.log.log(Level.WARNING, "It is not a number!", e);
					}
				}
				tSignal.show();
			}
			if (lbl.getText().toLowerCase().equals("script")) {
				String scriptFile = Utils.getFullPath("./scripts/" + prop.getVal() + ".js");
				try {
					String editor = "d:/Install/Sublime Text/sublime_text.exe";
					File f = new File(editor);
					if (!f.exists()) {
						editor = "Notepad.exe";
					}
					ProcessBuilder pb = new ProcessBuilder(editor, scriptFile);
					pb.start();
				} catch (Exception e) {
					LogFiles.log.log(Level.SEVERE, "=== Notepad.exe start error ===", e);
				}
			}
		});
		gridPane.add(btnEdit, 2, gridPaneSize);
		
		Button btnDelete = new Button(delete);
		btnDelete.setId("btnDelete_" + gridPaneSize);
		btnDelete.setOnAction(a -> {
			CustProps сustProps = SingleObject.selectedShape.getSvgGroup().getCustProps();
			сustProps.getCustomProps().remove(prop);
			if (сustProps.getCustomProps().size() == 0) {
				сustProps.setCustomProps(null);
				SingleObject.selectedShape.getSvgGroup().setCustProps(null);
			}
			gridPane.getChildren().remove(lbl);
			gridPane.getChildren().remove(tField);
			gridPane.getChildren().remove(btnEdit);
			gridPane.getChildren().remove(btnDelete);
		});
		gridPane.add(btnDelete, 3, gridPaneSize);
		gridPaneSize++;
	}

	@Override
	public void setElementText(ResourceBundle rb) {
		if (gridPane.getScene() != null) {
			((Stage)gridPane.getScene().getWindow()).setTitle(rb.getString("keyCustPropTitle"));
		}
		edit = rb.getString("keyEdit");
		delete = rb.getString("keyDelete");
		gridPane.getChildren().filtered(f -> f instanceof Button && f.getId().contains("btnEdit")).
			forEach(b -> ((Button)b).setText(edit));
		gridPane.getChildren().filtered(f -> f instanceof Button && f.getId().contains("btnDelete")).
		forEach(b -> ((Button)b).setText(delete));
		btnOK.setText(rb.getString("keyApply"));
		btnCancel.setText(rb.getString("keyCancel"));
	}
}
