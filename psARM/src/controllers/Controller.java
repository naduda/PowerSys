package controllers;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;

import javax.xml.bind.JAXBException;

import controllers.interfaces.IControllerInit;
import state.ProgramSettings;
import state.SchemeSettings;
import state.WindowState;
import svg2fx.Convert;
import svg2fx.fxObjects.EShape;
import ui.Main;
import ui.MainStage;
import ui.Scheme;
import pr.model.DvalTI;
import pr.model.LinkedValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Window;

public class Controller implements IControllerInit {
	private final DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	private static final int WORK_STATUS = 1;
	
	@FXML private ToolBarController toolBarController;
	@FXML private MenuBarController menuBarController;
	@FXML private TreeController spTreeController;	
	@FXML private AlarmController bpAlarmsController;
	@FXML private Pane bpAlarms;
	@FXML private BorderPane bpScheme;
	@FXML private SplitPane vSplitPane;
	@FXML private SplitPane hSplitPane;
	
	public static ResourceBundle getResourceBundle(Locale locale) {
		return ResourceBundle.getBundle("Language", locale, Main.classLoader, new UTF8Control());
	}
	
	@Override
	public void setElementText(ResourceBundle rb) {
		menuBarController.setElementText(rb);
		toolBarController.setElementText(rb);
		spTreeController.setElementText(rb);
		bpAlarmsController.setElementText(rb);
	}
	
	public static void exitProgram() {
		Window w = Main.mainScheme.getScene().getWindow();
		WindowState ws = new WindowState(w.getX(), w.getY(), w.getWidth(), w.getHeight());
		
		ws.setAlarmDividerPositions(MainStage.controller.getAlarmSplitPane().getDividers().get(0).getPosition());
		ws.setTreeDividerPositions(MainStage.controller.getTreeSplitPane().getDividers().get(0).getPosition());
		
		ProgramSettings ps = new ProgramSettings(ws);
		ps.setLocaleName(MainStage.controller.menuBarController.getLocaleName());
		SchemeSettings ss = new SchemeSettings();
		ps.setSchemeSettings(ss);
		ss.setSchemeName(Main.mainScheme.getSchemeName());
		ss.setSchemeScale(Main.mainScheme.getRoot().getScaleX());
		
		final LinkedValue lv = new LinkedValue("", "");
		MainStage.controller.getAlarmsController().tvAlarms.getColumns().forEach(c -> {
			lv.setVal(lv.getVal().toString() + (c.isVisible() ? 1 : 0) + ":");
		});
		ps.setShowAlarmColumns(lv.getVal().toString());
		
		try {
			ps.saveToFile(Main.FILE_SETTINGS);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}
	
	@FXML
	private void handleKeyInput(final InputEvent event) {
		System.out.println(event);
		if (event instanceof KeyEvent) {
			final KeyEvent keyEvent = (KeyEvent) event;
			if (keyEvent.isControlDown() && keyEvent.getCode() == KeyCode.A) {
				System.out.println("ctrl+A");
			}
		}
	}
	
	private double oldAlarmsHeight;
	private boolean isHide = false;
	
	public void showHideAlarmPanel() {
		SplitPane sp = getAlarmSplitPane();
		if (isHide) {
			sp.setDividerPositions(oldAlarmsHeight);
		} else {
			oldAlarmsHeight = sp.getDividerPositions()[0];
			MainStage.controller.getTreeSplitPane().setDividerPositions(0.1);
			sp.setDividerPositions(1);
		}
		isHide = !isHide;
	}
	
	public SplitPane getAlarmSplitPane() {
		return vSplitPane;
	}
	
	public SplitPane getTreeSplitPane() {
		return hSplitPane;
	}
	
	@FXML
	private void showAlarm(ActionEvent event) {
		showHideAlarmPanel();
		Button btn = (Button) event.getSource();
		btn.setText(isHide ? "showAlarms" : "hideAlarms");
	}
	
	public void updateTI(DvalTI ti) {
		updateTI(Main.mainScheme, ti);
	}
	
	public void updateTI(Scheme mainScheme, DvalTI ti) {		
		Convert.listSignals.stream().filter(f -> f.getKey().equals(ti.getSignalref())).forEach(s -> {
			try {
				EShape tt = mainScheme.getDeviceById(s.getValue());
				tt.setValue(ti.getVal(), s.getTypeSignal());
				
				toolBarController.updateLabel(df.format(ti.getServdt()));
			} catch (Exception e) {
				
			}
			//toolBarController.setTsLastDate(ti.getServdt().getTime());
		});	
	}
	
	public static void updateSignal(int idSigal, int type_, int sec) {
		Convert.listSignals.stream().filter(f -> f.getKey().equals(idSigal)).forEach(s -> {
			EShape tt = Main.mainScheme.getDeviceById(s.getValue());
			if (tt == null) return;
			int status = MainStage.signals.get(idSigal).getStatus();
			if (status == WORK_STATUS) {
				tt.updateSignal(sec);
			}
		});
	}

	public TreeController getSpTreeController() {
		return spTreeController;
	}

	public ToolBarController getToolBarController() {
		return toolBarController;
	}

	public MenuBarController getMenuBarController() {
		return menuBarController;
	}

	public AlarmController getAlarmsController() {
		return bpAlarmsController;
	}

	public BorderPane getBpScheme() {
		return bpScheme;
	}
	
	private static class UTF8Control extends Control {
	    public ResourceBundle newBundle (String baseName, Locale locale, 
	    		String format, ClassLoader loader, boolean reload)
	            throws IllegalAccessException, InstantiationException, IOException
	    {
	        String bundleName = toBundleName(baseName, locale);
	        String resourceName = toResourceName(bundleName, "properties");
	        ResourceBundle bundle = null;
	        InputStream stream = null;
	        if (reload) {
	            URL url = loader.getResource(resourceName);
	            if (url != null) {
	                URLConnection connection = url.openConnection();
	                if (connection != null) {
	                    connection.setUseCaches(false);
	                    stream = connection.getInputStream();
	                }
	            }
	        } else {
	            stream = loader.getResourceAsStream(resourceName);
	        }
	        if (stream != null) {
	            try {
	                bundle = new PropertyResourceBundle(new InputStreamReader(stream, "UTF-8"));
	            } finally {
	                stream.close();
	            }
	        }
	        return bundle;
	    }
	}
}
