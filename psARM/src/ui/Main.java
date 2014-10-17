package ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.xml.bind.JAXBException;

import pr.common.Utils;
import controllers.Controller;
import state.ProgramSettings;
import state.SchemeSettings;
import state.WindowState;
import topic.ReceiveTopic;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.stage.Stage;
import javafx.stage.Window;

public class Main extends Application {

	private static final int TIMEOUT_TI_SEC = 35;
	private static final int TIMEOUT_TS_SEC = 600;
	public static final String FILE_SETTINGS = Utils.getFullPath("./Settings.xml");
	private static ProgramSettings ps = getProgramSettings();
	public static ClassLoader classLoader;
	public static Scheme mainScheme;
	public static Stage mainStage;

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		try {
			File file = new File("d:/GIT/PowerSys/psARM/lang");
			URL[] urls = {file.toURI().toURL()};
			classLoader = new URLClassLoader(urls);
		} catch (MalformedURLException e) {
			System.out.println("---");
		}
		
		stage = new MainStage("./ui/Main.xml");
		mainStage = stage;
		
        new Thread(new ReceiveTopic("127.0.0.1:7676"), "ReceiveTopic").start();

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
		MainStage.controller.getMenuBarController().setLocaleName(ps.getLocaleName());
	}
	
	public static ProgramSettings getProgramSettings() {
		if (ps == null) {
			try {
				ps = ProgramSettings.getFromFile(FILE_SETTINGS);
			} catch (FileNotFoundException | JAXBException e) {
				e.printStackTrace();
			}
			if (ps.getLocaleName() == null) {
				ps.setLocaleName("en");
			}
		}
		return ps;
	}
	
	public static ResourceBundle getResourceBundle() {
		return Controller.getResourceBundle(new Locale(ps.getLocaleName()));
	}
//	--------------------------------------------------------------
	private final class Events {
		public void exitProgram() {
			Controller.exitProgram();
		}
	}
}
