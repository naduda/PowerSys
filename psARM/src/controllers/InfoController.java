package controllers;

import java.net.URL;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;

import pr.log.LogFiles;
import pr.model.Tsignal;
import single.Constants;
import single.ProgramProperty;
import single.SingleFromDB;
import single.SingleObject;
import controllers.interfaces.IControllerInit;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class InfoController implements Initializable, IControllerInit {
	private final BooleanProperty selectedShapeChangeProperty = new SimpleBooleanProperty();
	private final DoubleProperty valueProperty = new SimpleDoubleProperty();
	
	private final SimpleDateFormat dFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
	
	@FXML private GridPane infoStage;
	@FXML private Label lName;
	@FXML private Text tName;
	@FXML private Label lCode;
	@FXML private Text tCode;
	@FXML private Label lType;
	@FXML private Text tType;
	@FXML private Label lMode;
	@FXML private Text tMode;
	@FXML private Label lValue;
	@FXML private Text tValue;
	@FXML private Label lUnit;
	@FXML private Text tUnit;
	@FXML private Label lQuality;
	@FXML private Text tQuality;
	@FXML private Label lDate;
	@FXML private Text tDate;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		selectedShapeChangeProperty.bind(ProgramProperty.selectedShapeChangeProperty);
		valueProperty.bind(SingleObject.selectedShape.getValueProp());
		
		selectedShapeChangeProperty.addListener((observable, oldValue, newValue) -> {
			if (newValue) updateStage();
		});
		
		valueProperty.addListener((observable, oldValue, newValue) -> {
			if (newValue != null) updateDateValue();
		});
		
		setElementText(SingleObject.getResourceBundle());
		updateStage();
	}
	
	@Override
	public void setElementText(ResourceBundle rb) {
		if (infoStage.getScene() != null) {
			((Stage)infoStage.getScene().getWindow()).setTitle(rb.getString("keyTooltip_info"));
		}
		lName.setText(rb.getString("keyName"));
		lCode.setText(rb.getString("keyCode"));
		lType.setText(rb.getString("keyType"));
		lMode.setText(rb.getString("keyMode"));
		lValue.setText(rb.getString("keyValue"));
		lUnit.setText(rb.getString("keyUnit"));
		lQuality.setText(rb.getString("keyQuality"));
		lDate.setText(rb.getString("keyDate"));
	}
	
	private void updateStage() {
		if (SingleObject.selectedShape == null) return;
		Tsignal tID = SingleObject.selectedShape.gettSignalID();
		Tsignal tIdTS = SingleObject.selectedShape.gettSignalIDTS();
		
		if (tID == null) {
			infoStage.getChildren().filtered(f -> f.getClass().equals(Text.class)).forEach(i -> ((Text)i).setText(""));
		} else {
			tName.setText(tID.getNamesignal());
			tCode.setText(tID.getIdsignal() + "");
			
			try {
				tType.setText(SingleFromDB.spTypeSignals.get(tID.getTypesignalref()).getNametypesignal());
				if (tIdTS != null) {
					tMode.setText(SingleFromDB.psClient.getTSysParam("SIGNAL_STATUS").get(tIdTS.getStatus() + "").getParamdescr());
				} else {
					tMode.setText(SingleFromDB.psClient.getTSysParam("SIGNAL_STATUS").get(tID.getStatus() + "").getParamdescr());
				}
			} catch (RemoteException e) {
				LogFiles.log.log(Level.INFO, "void updateStage()", e);
			}
			tUnit.setText(SingleFromDB.signals.get(tID.getIdsignal()).getNameunit());
			tQuality.setText(Constants.getQuality(SingleObject.selectedShape.getRcode()));
			
			updateDateValue();
		}
		
		resize();
	}
	
	private void updateDateValue() {
		tValue.setText(SingleObject.selectedShape.getValue().getIdValue() + 
				(SingleObject.selectedShape.getIdTS() == -1 || SingleObject.selectedShape.getIdTS() == 0 ? "" : 
				String.format(" (TS = %s)", SingleObject.selectedShape.getValue().getIdTSValue())));
		Timestamp dt = SingleObject.selectedShape.getDt();
		tDate.setText(dt == null ? "" : dFormat.format(SingleObject.selectedShape.getDt()));
	}
	
	private void resize() {
		Platform.runLater(() -> infoStage.getScene().getWindow().setWidth(getLabelMax() + getTextMax() + 5 * infoStage.getInsets().getLeft()));
	}
	
	private double getLabelMax() {
		List<Double> arr = new ArrayList<>();
		infoStage.getChildren().filtered(f -> f.getClass().equals(Label.class)).forEach(i -> arr.add(((Label)i).getWidth()));
		return arr.stream().max(Double::compare).get();
	}
	
	private double getTextMax() {
		List<Double> arr = new ArrayList<>();
		infoStage.getChildren().filtered(f -> f.getClass().equals(Text.class))
			.forEach(i -> arr.add(i.getBoundsInLocal().getWidth()));
		return arr.stream().max(Double::compare).get();
	}
}
