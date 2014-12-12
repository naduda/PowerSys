package controllers.interfaces;

import java.awt.Point;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pr.common.Utils;
import pr.log.LogFiles;
import single.SingleObject;

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
				initOwner(SingleObject.mainStage.getScene().getWindow());
			}
			
			getScene().addEventHandler(KeyEvent.KEY_PRESSED, t -> {
				if (t.getCode()==KeyCode.ESCAPE) {
					LogFiles.log.log(Level.INFO, "Exit " + getTitle());
					((Stage)((Scene)t.getSource()).getWindow()).hide();
				}
			});
		} catch (IOException e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
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
