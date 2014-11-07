package controllers;

import java.net.URL;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import pr.model.Tsignal;
import ui.single.Constants;
import ui.single.ProgramProperty;
import ui.single.SingleFromDB;
import ui.single.SingleObject;
import controllers.interfaces.IControllerInit;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class InfoController implements Initializable, IControllerInit {
	private final BooleanProperty selectedShapeChangeProperty = new SimpleBooleanProperty();
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
		selectedShapeChangeProperty.addListener((observable, oldValue, newValue) -> {
			if (newValue) updateStage();
		});
		setElementText(SingleObject.getResourceBundle());
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
	
	public void updateStage() {
		if (SingleObject.selectedShape == null) return;
		Tsignal tID = SingleObject.selectedShape.gettSignalID();
		if (tID == null) {
			infoStage.getChildren().filtered(f -> f.getClass().equals(Text.class)).forEach(i -> ((Text)i).setText(""));
		} else {
			tName.setText(tID.getNamesignal());
			tCode.setText(tID.getIdsignal() + "");
			tType.setText(SingleFromDB.spTypeSignals.get(tID.getTypesignalref()).getNametypesignal());
			try {
				tMode.setText(SingleFromDB.psClient.getTSysParam("SIGNAL_STATUS").get(tID.getStatus() + "").getParamdescr());
			} catch (RemoteException e) {
				System.out.println(e.getMessage());
			}
			tValue.setText(SingleObject.selectedShape.getValue().getIdValue() + 
					(SingleObject.selectedShape.getIdTS() == -1 || SingleObject.selectedShape.getIdTS() == 0 ? "" : 
					String.format(" (TS = %s)", SingleObject.selectedShape.getValue().getIdTSValue())));
			tUnit.setText(SingleFromDB.signals.get(tID.getIdsignal()).getNameunit());
			tQuality.setText(Constants.getQuality(SingleObject.selectedShape.getRcode()));
			Timestamp dt = SingleObject.selectedShape.getDt();
			tDate.setText(dt == null ? "" : dFormat.format(SingleObject.selectedShape.getDt()));
		}
		
		resize();
	}
	
	private void resize() {
		Platform.runLater(() -> ((Stage)infoStage.getScene().getWindow()).setWidth(getLabelMax() + getTextMax() + 5 * infoStage.getInsets().getLeft()));
	}
	
	private double getLabelMax() {
		List<Double> arr = new ArrayList<>();
		infoStage.getChildren().filtered(f -> f.getClass().equals(Label.class)).forEach(i -> arr.add(((Label)i).getWidth()));
		return arr.stream().max(Double::compare).get();
	}
	
	private double getTextMax() {
		List<Double> arr = new ArrayList<>();
		infoStage.getChildren().filtered(f -> f.getClass().equals(Text.class))
			.forEach(i -> arr.add(((Text)i).getBoundsInLocal().getWidth()));
		return arr.stream().max(Double::compare).get();
	}
}
