package ui;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
	
	private final Stage mainStage = new Login("./ui/Login.xml");	
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage stage) {	
		stage = mainStage;
        stage.show();  
	}

}
