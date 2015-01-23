package controllers.interfaces;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
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
	private IStageLoader method;
	
	public StageLoader(String xmlPath, boolean isMainStageOwner) {
		try {
			FXMLLoader loader = new FXMLLoader(new File(Utils.getFullPath("./ui/" + xmlPath)).toURI().toURL());
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
				} else if (t.getCode()==KeyCode.ENTER) {
					runOnEnter(method);
				}
			});
		} catch (IOException e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	public void runOnEnter(IStageLoader method) {
		if (method != null) method.runOnEnter();
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

	public void setMethod(IStageLoader method) {
		this.method = method;
	}
}
