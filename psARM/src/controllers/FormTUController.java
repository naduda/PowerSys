package controllers;

import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.stream.Collectors;

import pr.log.LogFiles;
import pr.model.SpTuCommand;
import pr.model.Tsignal;
import single.ProgramProperty;
import single.SingleFromDB;
import single.SingleObject;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import controllers.interfaces.IControllerInit;
import controllers.interfaces.StageLoader;

public class FormTUController implements IControllerInit, Initializable {
	private final StringProperty localeName = new SimpleStringProperty();
	
	@FXML private BorderPane bpTU;
	@FXML private Button btnOK;
	@FXML private Button btnCancel;
	
	private Tsignal tuSignal;
	private String value;
	private String sendText;
	
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
		sendText = rb.getString("keySendText");
	}

	@FXML public void btnOK() {
		btnCancel();
		isOkClicked = true;
		ProgressTU ptu = new ProgressTU(tuSignal.getNamesignal(), 100);
		ptu.show();
	}
	
	@FXML private void btnCancel() {
		isOkClicked = false;
		((Stage)btnOK.getScene().getWindow()).close();
	}
	
	private boolean isOkClicked;
	public boolean isOkPressed(int typeSignal) {
		((StageLoader)btnOK.getScene().getWindow()).setMethod(() -> btnOK());
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
			Platform.runLater(() -> chBox.requestFocus());
			bpTU.setCenter(chBox);
		} else if (typeSignal == 1) {
			TextField txtField = new TextField();
			txtField.setText(SingleObject.selectedShape.getValueProp().get() + "");
			txtField.textProperty().addListener((observ, oldValue, newValue) -> {
				try {
	                Double.parseDouble(newValue);
	                value = newValue;
	            } catch (Exception e) {
	            	txtField.setText(oldValue);
	            }
				
			});
			Platform.runLater(() -> txtField.requestFocus());
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
	
	private class ProgressTU extends Stage {
		final StringProperty pr = new SimpleStringProperty("0");
		private boolean isRun = true;
		
		public ProgressTU(String title, int timeout) {
			Group root = new Group();
	        Scene scene = new Scene(root);
	        setScene(scene);
	        setTitle(title);
	        initModality(Modality.NONE);
			initOwner(SingleObject.mainStage.getScene().getWindow());
	        
	        final Label label = new Label();
	        label.textProperty().bind(pr);
	        
	        final ProgressIndicator pin = new ProgressIndicator();
	        pin.setProgress(-1);
	        
	        final HBox hb = new HBox();
	        hb.setSpacing(5);
	        hb.setAlignment(Pos.CENTER);
	        hb.getChildren().addAll(label, pin);
	        	        
	        final VBox vb = new VBox();
	        vb.setPadding(new Insets(5, 5, 5, 5));
	        vb.setSpacing(5);
	        vb.getChildren().addAll(new Label(sendText), hb);
	        scene.setRoot(vb);
	        
	        new Thread(() -> {
	        	while(isRun) {
		        	Platform.runLater(() -> {
		        		int t = Integer.parseInt(pr.get()) + 1;
		        		pr.set("" + t);
		        		
		        		if (t >= timeout) {
		        			isRun = false;
		        		}
		        		if (SingleFromDB.psClient.getSendOK(tuSignal.getIdsignal()) == 3) {
		        			pin.setProgress(1);
		        			isRun = false;
		        		}
		        	});
		        	try {
						Thread.sleep(1000);
					} catch (Exception e) {
						e.printStackTrace();
					}
	        	}
	        }).start();
	        
	        getScene().addEventHandler(KeyEvent.KEY_PRESSED, t -> {
				if (t.getCode()==KeyCode.ESCAPE) {
					LogFiles.log.log(Level.INFO, "Exit " + getTitle());
					isRun = false;
					close();
				}
			});
	        setOnCloseRequest(wc -> isRun = false);
		}
	}
}
