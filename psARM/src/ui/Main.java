package ui;

import java.util.Map;

import com.sun.messaging.ConnectionConfiguration;
import com.sun.messaging.ConnectionFactory;

import commonCommands.java.Commands;
import controllers.Controller;
import model.Tsignal;
import topic.JMSConnection;
import topic.ReceiveProtocolTopic;
import topic.ReceiveTopic;
import topic.SendCommands;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import jdbc.PostgresDB;

public class Main extends Application {

	private static final int TIMEOUT_TI_SEC = 11;
	private static final int TIMEOUT_TS_SEC = 600;
	public static SendCommands sendCommands;
	public static ReceiveProtocolTopic receiveProtocolTopic = new ReceiveProtocolTopic();
//	public static final PostgresDB pdb = new PostgresDB("10.1.3.17", "3700", "dimitrovEU");
	public static final PostgresDB pdb = new PostgresDB("193.254.232.107", "5451", "dimitrovoEU", "postgres", "askue");
	public static Map<Integer, Tsignal> signals = pdb.getTsignalsMap();

	private final Stage mainStage = new MainStage("./ui/Main.xml");
	public static Scheme mainScheme;
	public static boolean ctrlPressed;
	public static boolean shiftPressed;

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage stage) throws Exception {	
//        final Task<Void> taskProtocolTopic = new Task<Void>() {
//			@Override
//			protected Void call() throws Exception {
//				sendProtocolTopic.runCommand();
//				return null;
//			}
//        	
//        };
//        new Thread(taskProtocolTopic, "SendProtocolTopic").start();
//        
//        final Task<Void> taskReceiveProtocolTopic = new Task<Void>() {
//			@Override
//			protected Void call() throws Exception {
//				receiveProtocolTopic.run();
//				return null;
//			}
//        	
//        };
//        new Thread(taskReceiveProtocolTopic, "ReceiveProtocolTopic").start();
//        
//        while (sendProtocolTopic == null || receiveProtocolTopic == null) {
//        	System.out.println(sendProtocolTopic + "/" + receiveProtocolTopic);
//			Thread.sleep(1000);
//		}
//        Commands commands = new Commands(Main.sendProtocolTopic, Main.receiveProtocolTopic);
//		commands.getSignals();
//		
//		System.out.println("___________________");
		ConnectionFactory factory = new com.sun.messaging.ConnectionFactory();
		JMSConnection jConn = new JMSConnection("127.0.0.1", "7676", "admin", "admin");
		factory.setProperty(ConnectionConfiguration.imqAddressList, "mq://127.0.0.1:7676,mq://127.0.0.1:7676");
		sendCommands = new SendCommands(factory, jConn, "CommandTopic");
		new Thread(sendCommands, "SendCommands_Thread").start();
		
		Commands commands = new Commands(sendCommands, Main.receiveProtocolTopic);
		commands.getSignals();
		
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
