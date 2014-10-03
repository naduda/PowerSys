package controllers;

import java.io.IOException;
import java.net.URL;

import pr.common.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
//			TransparantController transparantController = loader.getController();
			
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.setTitle("Transparant");
			stage.initModality(Modality.NONE);
//			System.out.println(((MenuItem)event.getSource()).getParentPopup().getScene().getWindow());
//			stage.initOwner(((MenuItem)event.getSource()).getParentMenu().getParentPopup().getScene().getWindow());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	    stage.show();
	}
}
