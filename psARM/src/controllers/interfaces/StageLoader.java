package controllers.interfaces;

import java.awt.Point;
import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pr.common.Utils;
import ui.Main;

public class StageLoader extends Stage {

	private Object controller;
	
	public StageLoader(String xmlPath, boolean isMainStageOwner) {
		try {
			FXMLLoader loader = new FXMLLoader(new URL("file:/" + Utils.getFullPath("./ui/" + xmlPath)));
			Parent root = loader.load();
			controller = loader.getController();
			
			Scene scene = new Scene(root);
			setScene(scene);
			
			if (isMainStageOwner) {
				initModality(Modality.NONE);
				initOwner(Main.mainStage.getScene().getWindow());
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("------------------------------------------");
		}
	}
	
	public StageLoader(String xmlPath, String title, boolean isMainStageOwner) {
		this(xmlPath, isMainStageOwner);
		setTitle(title);
	}
	
	public StageLoader(String xmlPath, String title, Point p, boolean isMainStageOwner) {
		this(xmlPath, title, isMainStageOwner);
		setX(p.getX());
		setY(p.getY());
	}

	public Object getController() {
		return controller;
	}
}
