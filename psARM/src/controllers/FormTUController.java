package controllers;

import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import pr.model.SpTuCommand;
import pr.model.Tsignal;
import single.ProgramProperty;
import single.SingleFromDB;
import single.SingleObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import controllers.interfaces.IControllerInit;

public class FormTUController implements IControllerInit, Initializable {
	private final StringProperty localeName = new SimpleStringProperty();
	
	@FXML private BorderPane bpTU;
	@FXML private Button btnOK;
	@FXML private Button btnCancel;
	
	private Tsignal tuSignal;
	private String value;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		localeName.bind(ProgramProperty.localeName);
		localeName.addListener((observ, old, value) -> setElementText(Controller.getResourceBundle(new Locale(value))));
		
		setElementText(Controller.getResourceBundle(new Locale(SingleObject.getProgramSettings().getLocaleName())));
	}

	@Override
	public void setElementText(ResourceBundle rb) {
		if (btnOK.getScene() != null) ((Stage)btnOK.getScene().getWindow()).setTitle(rb.getString("keyFormTUTitle"));
		
		btnOK.setText(rb.getString("keyApply"));
		btnCancel.setText(rb.getString("keyCancel"));
	}

	@FXML private void btnOK() {
		btnCancel();
		isOkClicked = true;
	}
	
	@FXML private void btnCancel() {
		isOkClicked = false;
		((Stage)btnOK.getScene().getWindow()).close();
	}
	
	private boolean isOkClicked;
	public boolean isOkPressed(int typeSignal) {
		if (tuSignal == null) return false;
		Stage w = (Stage)btnOK.getScene().getWindow();
		if (typeSignal == 3) {
			List<SpTuCommand> tuCommands = SingleFromDB.spTuCommands.stream()
					.filter(f -> f.getObjref() == tuSignal.getStateref()).collect(Collectors.toList());
			ChoiceBox<SpTuCommand> chBox = new ChoiceBox<>();
			tuCommands.forEach(chBox.getItems()::add);
			chBox.getSelectionModel().selectedItemProperty().addListener((observ, oldValue, newValue) -> {
				value = newValue.getVal() + "";
			});
			if (chBox.getItems().size() > 0) chBox.getSelectionModel().selectFirst();
			if (SingleObject.selectedShape.getValueProp().get() == chBox.getSelectionModel().getSelectedItem().getVal()) {
				chBox.getSelectionModel().selectNext();
			}
			bpTU.setCenter(chBox);
		} else if (typeSignal == 1) {
			TextField txtField = new TextField();
			txtField.textProperty().addListener((observ, oldValue, newValue) -> {
				value = newValue;
			});
			bpTU.setCenter(txtField);
		} else {
			System.out.println("=====   This is not TU or TI   =====");
		}
		w.showAndWait();
		
		return isOkClicked;
	}

	public void setTuSignal(Tsignal tuSignal) {
		this.tuSignal = tuSignal;
	}

	public Tsignal getTuSignal() {
		return tuSignal;
	}

	public String getValue() {
		return value;
	}
}
