package controllers;

import java.net.URL;
import java.util.ResourceBundle;

import controllers.interfaces.IControllerInit;
import controllers.interfaces.StageLoader;
import ui.Main;
import ui.MainStage;
import ui.Scheme;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.stage.Modality;

public class ToolBarController implements Initializable, IControllerInit {

	@FXML private Label lDataOn;
	@FXML private Label lLastDate;
	
	@Override
	public void initialize(URL url, ResourceBundle boundle) {
		try {
			setElementText(Main.getResourceBundle());
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		StageLoader stage = new StageLoader("Data.xml", Main.getResourceBundle().getString("keyDataTitle"));
		DataController dataController = (DataController) stage.getController();
		dataController.setIdSignal(Scheme.selectedShape.getIdSignal());
		
		stage.initModality(Modality.NONE);
		stage.initOwner(((Control)event.getSource()).getScene().getWindow());
	    stage.show();
	}
	
	@Override
	public void setElementText(ResourceBundle rb) {
		lDataOn.setText(rb.getString("keyDataOn"));
	}
}
