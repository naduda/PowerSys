package ui;

import java.io.FileNotFoundException;

import javax.xml.bind.JAXBException;

import controllers.Controller;
import state.ProgramSettings;
import state.WindowState;
import topic.ReceiveTopic;
import ua.pr.common.ToolsPrLib;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.stage.Stage;
import javafx.stage.Window;

public class Main extends Application {

	private static final int TIMEOUT_TI_SEC = 35;
	private static final int TIMEOUT_TS_SEC = 600;
	public static final String FILE_SETTINGS = ToolsPrLib.getFullPath("./Settings.xml");
	public static Scheme mainScheme;

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage stage) throws Exception {	
		stage = new MainStage("./ui/Main.xml");
		
        stage.show();
        setStageParams(stage);
        
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
        
        Events events = new Events();
        stage.setOnCloseRequest(event -> { events.exitProgram(); });
        
	}
//	--------------------------------------------------------------
	private void setStageParams(Stage stage) {
		try {
			ProgramSettings ps = ProgramSettings.getFromFile(FILE_SETTINGS);
			WindowState ws = ps.getWinState();
			Window w = stage.getScene().getWindow();
			w.setX(ws.getX());
			w.setY(ws.getY());
			w.setWidth(ws.getWidth());
			w.setHeight(ws.getHeight());
//			System.out.println(MainStage.controller.getAlarmSplitPane().getDividers().get(0).getPosition());
//			MainStage.controller.getAlarmSplitPane().setDividerPositions(1);
//			//MainStage.controller.getAlarmSplitPane().setDividerPosition(0, ws.getAlarmDividerPositions());
//			System.out.println(ws.getAlarmDividerPositions());
//			System.out.println(MainStage.controller.getAlarmSplitPane().getDividers().get(0).getPosition());
		} catch (FileNotFoundException | JAXBException e) {
			e.printStackTrace();
		}
	}
//	--------------------------------------------------------------
	private final class Events {
		public void exitProgram() {
			Controller.exitProgram();
		}
	}
}
