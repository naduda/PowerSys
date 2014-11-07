package ui;

import ui.single.SingleObject;
import controllers.interfaces.StageLoader;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		stage = new StageLoader("Login.xml", SingleObject.getResourceBundle().getString("keyLogin"), false);
		stage.setResizable(false);
        stage.show();
	}
}
