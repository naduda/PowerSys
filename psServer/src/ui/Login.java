package ui;

import java.io.IOException;
import java.net.URL;

import pr.common.Utils;
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
			System.err.println("Login() ...");
			e.printStackTrace();
		}
		
		setOnCloseRequest(event -> { System.exit(0); });
	}
}
