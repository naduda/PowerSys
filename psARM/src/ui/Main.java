package ui;

import controllers.Controller;
import topic.ReceiveTopic;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import jdbc.PostgresDB;

public class Main extends Application {

	private static final int TIMEOUT_TI_SEC = 11;
	private static final int TIMEOUT_TS_SEC = 600;
//	public static final PostgresDB pdb = new PostgresDB("10.1.3.17", "3700", "dimitrovEU");
	public static final PostgresDB pdb = new PostgresDB("193.254.232.107", "5451", "dimitrovoEU", "postgres", "askue");

	private final Stage mainStage = new MainStage("./ui/Main.xml");
	public static Scheme mainScheme;
	public static boolean ctrlPressed;
	public static boolean shiftPressed;

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage stage) throws Exception {	

        final Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				new ReceiveTopic();
				return null;
			}
        	
        };
        new Thread(task, "ReceiveTopic").start();

        final Task<Void> taskTI = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				new UpdateTimeOut(TIMEOUT_TI_SEC, 1);
				return null;
			}
        	
        };
        new Thread(taskTI, "UpdateTimeOut_TI").start();
        
        final Task<Void> taskTS = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				new UpdateTimeOut(TIMEOUT_TS_SEC, 2);
				return null;
			}
        	
        };
        new Thread(taskTS, "UpdateTimeOut_TS").start();
        
		stage = mainStage;
        stage.show();
        
        Events events = new Events();
        stage.setOnCloseRequest(event -> { events.exitProgram(); });
        stage.getScene().setOnKeyPressed(event -> { events.setOnKeyPressedReleased(((KeyEvent)event).getCode(), true); });
        stage.getScene().setOnKeyReleased(event -> { events.setOnKeyPressedReleased(((KeyEvent)event).getCode(), false); });
	}

//	--------------------------------------------------------------
	private final class Events {
		public void exitProgram() {
			Controller.exitProgram();
		}
		
		public void setOnKeyPressedReleased(KeyCode keyCode, boolean pressedReleased) {
			switch (keyCode) {
			case CONTROL:
				ctrlPressed = pressedReleased;
				break;
			case SHIFT:
				shiftPressed = pressedReleased;
				break;
			default:
				break;
			}
		}
	}
}
