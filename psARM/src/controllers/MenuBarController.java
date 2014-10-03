package controllers;

import java.io.File;

import pr.common.Utils;
import ui.MainStage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MenuBarController {
	final FileChooser fileChooser = new FileChooser();
	
	@FXML
	private void openScheme(ActionEvent event) {
		FileChooser.ExtensionFilter extentionFilter = new FileChooser.ExtensionFilter("SVG files (*.svg)", "*.svg");
		fileChooser.getExtensionFilters().add(extentionFilter);

		File userDirectory = new File(Utils.getFullPath("./schemes"));
		fileChooser.setInitialDirectory(userDirectory);
		
		File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
        	String schemeName = file.getName().split("\\.")[0];
        	
	        if (MainStage.schemes.get(schemeName) == null) {
	            MainStage.setScheme(schemeName);
        	}
        }
	}
	
	@FXML
	private void exit(ActionEvent event) {
		Controller.exitProgram();
	}
}
