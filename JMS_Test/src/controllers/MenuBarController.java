package controllers;

import java.io.File;

import ua.pr.common.ToolsPrLib;
import ui.MainStage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MenuBarController {
	final FileChooser fileChooser = new FileChooser();
	
	@FXML
	private void openScheme(ActionEvent event) {
		FileChooser.ExtensionFilter extentionFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
		fileChooser.getExtensionFilters().add(extentionFilter);

		File userDirectory = new File(ToolsPrLib.getFullPath("./schemes"));
		fileChooser.setInitialDirectory(userDirectory);
		
		File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
        	String schemeName = file.getName().split("\\.")[0];
        	
	        if (MainStage.schemes.get(schemeName) == null) {
	            MainStage.setScheme(schemeName);
        	}
        }
	}
}
