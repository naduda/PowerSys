package ui;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;

import pr.common.Utils;
import pr.log.LogFiles;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Login extends Stage {
	
	public Login(String pathXML) {
		try {
			FXMLLoader loader = new FXMLLoader(new URL("file:/" + Utils.getFullPath(pathXML)));
			Parent root = loader.load();

			Scene scene = new Scene(root);      
			setTitle("Login");
			setScene(scene);
		} catch (IOException e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
		}
		
		setOnCloseRequest(event -> System.exit(0));
	}
}
