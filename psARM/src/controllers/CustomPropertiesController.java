package controllers;

import java.net.URL;
import java.util.ResourceBundle;

import pr.svgObjects.CP;
import pr.svgObjects.CustProps;
import single.ProgramProperty;
import single.SingleObject;
import controllers.interfaces.AController;
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
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
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
	
	@FXML private GridPane gridPane;
	private int gridPaneSize;
	private String edit;
	private String delete;
	
	@FXML 
	protected void btnOK() {
		SingleObject.svgModel.setObject(SingleObject.svg.getFileName(), SingleObject.svg);
		btnCancel();
	}
	
	@FXML 
	protected void btnCancel() {
		((Stage)gridPane.getScene().getWindow()).hide();
	}
	
	@FXML 
	protected void preset() {
		
	}
	
	public void setCP(CP prop) {
		Label lbl = new Label(prop.getLbl());
		lbl.setId("lbl_" + gridPaneSize);
		gridPane.add(lbl, 0, gridPaneSize);
		
		TextField tField = new TextField(prop.getVal());
		tField.setId("txt_" + gridPaneSize);
		tField.setOnKeyReleased(e -> {
			prop.setVal(tField.getText());
		});
		gridPane.add(tField, 1, gridPaneSize);
		
		Button btn = new Button(edit);
		btn.setId("btnEdit_" + gridPaneSize);
		gridPane.add(btn, 2, gridPaneSize);
		
		Button btnDelete = new Button(delete);
		btnDelete.setId("btnDelete_" + gridPaneSize);
		btnDelete.setOnAction(a -> {
			SingleObject.selectedShape.getSvgGroup().getCustProps().getCustomProps().
				remove(prop);
			gridPane.getChildren().remove(lbl);
			gridPane.getChildren().remove(tField);
			gridPane.getChildren().remove(btn);
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
	}
}
