package controllers;

import java.net.URL;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import pr.model.Tsignal;
import ui.Main;
import ui.MainStage;
import ui.Scheme;
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
	
	@FXML GridPane infoStage;
	@FXML Label lName;
	@FXML Text tName;
	@FXML Label lCode;
	@FXML Text tCode;
	@FXML Label lType;
	@FXML Text tType;
	@FXML Label lMode;
	@FXML Text tMode;
	@FXML Label lValue;
	@FXML Text tValue;
	@FXML Label lUnit;
	@FXML Text tUnit;
	@FXML Label lQuality;
	@FXML Text tQuality;
	@FXML Label lDate;
	@FXML Text tDate;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		selectedShapeChangeProperty.bind(Scheme.selectedShapeChangeProperty);
		selectedShapeChangeProperty.addListener((observable, oldValue, newValue) -> {
			if (newValue) updateStage();
		});
		setElementText(Main.getResourceBundle());
	}
	
	@Override
	public void setElementText(ResourceBundle rb) {
		if (infoStage.getScene() != null) {
			((Stage)infoStage.getScene().getWindow()).setTitle(rb.getString("keyInfoTitle"));
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
		if (Scheme.selectedShape == null) return;
		Tsignal tID = Scheme.selectedShape.gettSignalID();
		if (tID == null) {
			infoStage.getChildren().filtered(f -> f.getClass().equals(Text.class)).forEach(i -> ((Text)i).setText(""));
		} else {
			tName.setText(tID.getNamesignal());
			tCode.setText(tID.getIdsignal() + "");
			tType.setText(MainStage.spTypeSignals.get(tID.getTypesignalref()).getNametypesignal());
			try {
				tMode.setText(MainStage.psClient.getTSysParam("SIGNAL_STATUS").get(tID.getStatus() + "").getParamdescr());
			} catch (RemoteException e) {
				System.out.println(e.getMessage());
			}
			tValue.setText(Scheme.selectedShape.getValue().getIdValue() + 
					(Scheme.selectedShape.getIdTS() == -1 || Scheme.selectedShape.getIdTS() == 0 ? "" : 
					String.format(" (TS = %s)", Scheme.selectedShape.getValue().getIdTSValue())));
			tUnit.setText(MainStage.signals.get(tID.getIdsignal()).getNameunit());
			tQuality.setText(MainStage.getQuality(Scheme.selectedShape.getRcode()));
			Timestamp dt = Scheme.selectedShape.getDt();
			tDate.setText(dt == null ? "" : dFormat.format(Scheme.selectedShape.getDt()));
		}
		
		resize();
	}
	
	private void resize() {
		Platform.runLater(new Runnable() {
            @Override public void run() {
            	((Stage)infoStage.getScene().getWindow()).setWidth(getLabelMax() + getTextMax() + 4 * infoStage.getInsets().getLeft());
            }
		});
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
