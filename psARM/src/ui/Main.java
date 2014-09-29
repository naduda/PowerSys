package ui;

import java.io.FileNotFoundException;

import javax.xml.bind.JAXBException;

import controllers.Controller;
import state.ProgramSettings;
import state.SchemeSettings;
import state.WindowState;
import topic.ReceiveTopic;
import ua.pr.common.ToolsPrLib;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.stage.Stage;
import javafx.stage.Window;

public class Main extends Application {

	private static final int TIMEOUT_TI_SEC = 35;
	private static final int TIMEOUT_TS_SEC = 600;
	public static final String FILE_SETTINGS = ToolsPrLib.getFullPath("./Settings.xml");
	private static ProgramSettings ps;
	public static Scheme mainScheme;	

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage stage) throws Exception {	
		stage = new MainStage("./ui/Main.xml");
		ps = ProgramSettings.getFromFile(FILE_SETTINGS);
		
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
        
        setStageParams(stage);
        stage.show();
	}
//	--------------------------------------------------------------
	private void setStageParams(Stage stage) {
		WindowState ws = ps.getWinState();
		Window w = stage.getScene().getWindow();
		w.setX(ws.getX());
		w.setY(ws.getY());
		w.setWidth(ws.getWidth());
		w.setHeight(ws.getHeight());
		
		MainStage.controller.getAlarmSplitPane().getDividers().get(0).positionProperty().addListener((o, ov, nv) -> {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					MainStage.controller.getTreeSplitPane().setDividerPositions(ws.getTreeDividerPositions());
	            }
	        });	
		});
		
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				MainStage.controller.getAlarmSplitPane().setDividerPositions(ws.getAlarmDividerPositions());
            }
        });
		
		SchemeSettings ss = ps.getSchemeSettings();
		Main.mainScheme.getRoot().setScaleX(ss.getSchemeScale());
		Main.mainScheme.getRoot().setScaleY(ss.getSchemeScale());
	}
	
	public static ProgramSettings getProgramSettings() {
		try {
			return ProgramSettings.getFromFile(FILE_SETTINGS);
		} catch (FileNotFoundException | JAXBException e) {
			e.printStackTrace();
		}
		return null;
	}
	//	--------------------------------------------------------------
	private final class Events {
		public void exitProgram() {
			Controller.exitProgram();
		}
	}
}
