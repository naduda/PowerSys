package ui;

import pr.log.LogFiles;
import single.SingleObject;
import controllers.LoginController;
import controllers.interfaces.StageLoader;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {
	
	public static void main(String[] args) {
		new LogFiles();
		launch(args);
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		stage = new StageLoader("Login.xml", SingleObject.getResourceBundle().getString("key_miLogin"), false);
		LoginController controller = (LoginController) ((StageLoader)stage).getController();
		
		((StageLoader)stage).setMethod(() -> controller.btnOK());
		stage.initStyle(StageStyle.UNDECORATED);
		//stage.setResizable(false);
        stage.show();
	}
}
