package controllers;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;

import pr.common.Utils;
import ui.Main;
import ui.MainStage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.shape.Shape;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ShapeController {

	@FXML
	protected void add(ActionEvent event) {
		System.out.println("test ShapeController add");
	}
	
	@FXML
	protected void addTransparant(ActionEvent event) {
		Stage stage = new Stage();
		
		try {
			FXMLLoader loader = new FXMLLoader(new URL("file:/" + Utils.getFullPath("./ui/Transparants.xml")));
			Parent root = loader.load();
			
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.setTitle("Transparant");
			stage.initModality(Modality.WINDOW_MODAL);
			stage.initOwner(Main.mainStage);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	    stage.show();
	}
	
	@FXML
	protected void deleteTransparant(ActionEvent event) {
		String id = ((MenuItem)event.getSource()).getParentPopup().getId();
		Shape transp = (Shape) Main.mainScheme.getRoot().lookup("#" + id);
		
		id = id.substring(id.indexOf("_") + 1);
		int trref = Integer.parseInt(id);

		try {
			MainStage.psClient.updateTtransparantCloseTime(trref);
			MainStage.psClient.deleteTtranspLocate(trref, Main.mainScheme.getIdScheme());
			
			Main.mainScheme.getRoot().getChildren().remove(transp);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	protected void editTransparant(ActionEvent event) {
		System.out.println("editTransparant clicked");
	}
}
