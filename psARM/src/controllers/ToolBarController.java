package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import pr.common.Utils;
import ui.Main;
import ui.MainStage;
import ui.Scheme;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ToolBarController implements Initializable {

	@FXML private Label lDataOn;
	@FXML private Label lLastDate;
	
	@Override
	public void initialize(URL url, ResourceBundle boundle) {
		try {
			setElementText(Controller.getResourceBundle(new Locale(Main.getProgramSettings().getLocaleName())));
		} catch (Exception e) {
			setElementText(Controller.getResourceBundle(new Locale("en")));
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
		Stage stage = new Stage();
		
		try {
			FXMLLoader loader = new FXMLLoader(new URL("file:/" + Utils.getFullPath("./ui/Data.xml")));
			Parent root = loader.load();
			DataController dataController = loader.getController();
			
			dataController.setIdSignal(Scheme.selectedShape.getIdSignal());
			
			Scene scene = new Scene(root);
			stage.setScene(scene);
			ResourceBundle rb = Controller.getResourceBundle(new Locale(Main.getProgramSettings().getLocaleName()));
			stage.setTitle(rb.getString("keyDataTitle"));
			stage.initModality(Modality.NONE);
			stage.initOwner(((Control)event.getSource()).getScene().getWindow());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	    stage.show();
	}
	
	public void setElementText(ResourceBundle rb) {
		lDataOn.setText(rb.getString("keyDataOn"));
	}
}
