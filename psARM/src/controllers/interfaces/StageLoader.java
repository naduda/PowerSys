package controllers.interfaces;

import java.awt.Point;
import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pr.common.Utils;

public class StageLoader extends Stage {

	private Object controller;
	
	public StageLoader(String xmlPath) {
		try {
			FXMLLoader loader = new FXMLLoader(new URL("file:/" + Utils.getFullPath("./ui/" + xmlPath)));
			Parent root = loader.load();
			controller = loader.getController();
			
			Scene scene = new Scene(root);
			setScene(scene);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("------------------------------------------");
		}
	}
	
	public StageLoader(String xmlPath, String title) {
		this(xmlPath);
		setTitle(title);
	}
	
	public StageLoader(String xmlPath, String title, Point p) {
		this(xmlPath, title);
		setX(p.getX());
		setY(p.getY());
	}

	public Object getController() {
		return controller;
	}
}
