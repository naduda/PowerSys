package ui;

import java.util.logging.Level;

import pr.log.LogFiles;
import single.SingleObject;
import controllers.LoginController;
import controllers.interfaces.StageLoader;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class Main extends Application {
	
	public static void main(String[] args) {
		new LogFiles();
		launch(args);
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		stage = new StageLoader("Login.xml", SingleObject.getResourceBundle().getString("keyLogin"), false);
		LoginController controller = (LoginController) ((StageLoader)stage).getController();
		stage.getScene().addEventHandler(KeyEvent.KEY_PRESSED, t -> {
			if (t.getCode()==KeyCode.ESCAPE) {
				LogFiles.log.log(Level.INFO, "Exit ...");
				((Stage)((Scene)t.getSource()).getWindow()).close();
			} else if (t.getCode()==KeyCode.ENTER) {
				controller.btnOK();
			}
		});
		stage.setResizable(false);
        stage.show();
	}
}
