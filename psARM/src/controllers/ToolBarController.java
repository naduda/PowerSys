package controllers;

import java.awt.MouseInfo;
import java.awt.Point;
import java.net.URL;
import java.util.ResourceBundle;

import controllers.interfaces.IControllerInit;
import controllers.interfaces.StageLoader;
import controllers.journals.JAlarmsController;
import ui.Main;
import ui.MainStage;
import ui.Scheme;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

public class ToolBarController implements Initializable, IControllerInit {
	private static final BooleanProperty showInfoProperty = new SimpleBooleanProperty();
	private static StageLoader infoStage;
	private static InfoController infoController;
	private Point2D infoStagePos;
	
	@FXML private Label lDataOn;
	@FXML private Label lLastDate;
	@FXML Button btnInfo;
	
	@Override
	public void initialize(URL url, ResourceBundle boundle) {
		setElementText(Main.getResourceBundle());
		
		showInfoProperty.addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				btnInfo.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.THIN)));
				if (infoStagePos != null) {
					infoStage.setX(infoStagePos.getX());
					infoStage.setY(infoStagePos.getY());
				}
				infoStage.show();
			} else {
				infoStagePos = new Point2D(infoStage.getX(), infoStage.getY());
				infoStage.hide();
				btnInfo.setBorder(new Border(new BorderStroke(Color.TRANSPARENT, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.THIN)));
			}
		});
	}
	
	@FXML 
	protected void testAction(ActionEvent event) {
		System.out.println("test ToolBarController " + lLastDate.hashCode());
	}
	
	@FXML 
	protected void exitButtonAction(ActionEvent event) {
		Controller.exitProgram();
	}
	
	@FXML 
	protected void info(ActionEvent event) {
		if (infoStage == null) {
			infoStage = new StageLoader("Info.xml", Main.getResourceBundle().getString("keyInfoTitle"), true);
			infoStage.setOnCloseRequest(e -> {
				infoStagePos = new Point2D(infoStage.getX(), infoStage.getY());
				showInfoProperty.set(false);
			});
			infoController = (InfoController) infoStage.getController();
		}
		infoController.updateStage();
		showInfoProperty.set(showInfoProperty.get() ? false : true);
	}
	
	@FXML 
	protected void showAlarms(ActionEvent event) {
		Point p = MouseInfo.getPointerInfo().getLocation();
		StageLoader stage = new StageLoader("journals/JournalAlarms.xml", 
				Main.getResourceBundle().getString("keyJalarms"), p, true);
		
		JAlarmsController controller = (JAlarmsController) stage.getController();
		System.out.println("=====================");
		controller.setAlarmById(true);
		
	    stage.show();
	}

	public void updateLabel(String text) {
		lLastDate.setText(text);
	}
	
	@FXML 
	protected void fitVertical(ActionEvent event) {
		fitSchemeVertical();
	}
	
	public void fitSchemeVertical() {
		Group root = (Group) Main.mainScheme.getRoot();
		double k = MainStage.bpScheme.getHeight() * 0.95 / root.getBoundsInLocal().getHeight();
		root.setScaleY(k);
		root.setScaleX(k);
	}
	
	@FXML 
	protected void fitHorizontal(ActionEvent event) {
		fitSchemeHorizontal();
	}
	
	public void fitSchemeHorizontal() {
		Group root = (Group) Main.mainScheme.getRoot();
		double k = MainStage.bpScheme.getWidth() * 0.95 / root.getBoundsInLocal().getWidth();
		root.setScaleY(k);
		root.setScaleX(k);
	}
	
	@FXML 
	protected void showChart(ActionEvent event) {
		StageLoader stage = new StageLoader("Data.xml", Main.getResourceBundle().getString("keyDataTitle"), true);
		DataController dataController = (DataController) stage.getController();
		dataController.setIdSignal(Scheme.selectedShape.getIdSignal());
		
	    stage.show();
	}
	
	@Override
	public void setElementText(ResourceBundle rb) {
		lDataOn.setText(rb.getString("keyDataOn"));
		if (infoController != null ) infoController.setElementText(rb);
	}
}
