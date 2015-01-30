package controllers;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;
import java.util.logging.Level;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;

import controllers.interfaces.IControllerInit;
import controllers.journals.AlarmTableController;
import controllers.tree.TreeController;
import pr.javafx.SidePane;
import pr.log.LogFiles;
import single.SingleFromDB;
import single.SingleObject;
import state.ProgramSettings;
import state.SchemeSettings;
import state.WindowState;
import svg2fx.fxObjects.EShape;
import ui.MainStage;
import ui.Scheme;
import pr.model.DvalTI;
import pr.model.LinkedValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Window;

public class Controller implements IControllerInit, Initializable {
	private static final int WORK_STATUS = 1;
	
	@FXML private ToolBarController toolBarController;
	@FXML private MenuBarController menuBarController;
	@FXML private TreeController spTreeController;	
	@FXML private AlarmTableController bpAlarmsController;
	@FXML private Pane bpAlarms;
	@FXML private BorderPane bpScheme;
	@FXML private SidePane mainPane;
	@FXML private SidePane vSplitPane;
	@FXML private SidePane hSplitPane;
	@FXML private Button showAlarm;
	
	public static ResourceBundle getResourceBundle(Locale locale) {
		return ResourceBundle.getBundle("Language", locale, SingleObject.getClassLoader(), new UTF8Control());
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		showAlarm.setText(vSplitPane.getSideBar().isVisible() ? SingleObject.getResourceBundle().getString("keyHideAlarms") : 
			SingleObject.getResourceBundle().getString("keyHideAlarms"));
		vSplitPane.getSideBar().visibleProperty().addListener((observ, old, newValue) -> {
			showAlarm.setText(!newValue ? SingleObject.getResourceBundle().getString("keyShowAlarms") : 
				SingleObject.getResourceBundle().getString("keyHideAlarms"));
		});
	}
	
	@Override
	public void setElementText(ResourceBundle rb) {
		menuBarController.setElementText(rb);
		toolBarController.setElementText(rb);
		spTreeController.setElementText(rb);
		
		showAlarm.setText(vSplitPane.getSideBar().isVisible() ? rb.getString("keyShowAlarms") : rb.getString("keyHideAlarms"));
	}
	
	public static void exitProgram() {
		if (SingleObject.mainScheme == null) return;
		boolean isMaximized = SingleObject.mainStage.isMaximized();
		
		if (isMaximized) SingleObject.mainStage.setMaximized(false);
		Window w = SingleObject.mainScheme.getScene().getWindow();
		WindowState ws = new WindowState(w.getX() < 0 ? 0 : w.getX(), w.getY() < 0 ? 0 : w.getY(), 
				w.getX() < 0 ? w.getX() + w.getWidth(): w.getWidth(), 
				w.getY() < 0 ? w.getY() + w.getHeight() : w.getHeight());
		
		ws.setAlarmDividerPositions(MainStage.controller.getAlarmSplitPane().getExpandedSize());
		ws.setTreeDividerPositions(MainStage.controller.getTreeSplitPane().getExpandedSize());
		ws.setFullScreen(!MainStage.controller.getMainPane().getSideBar().isVisible());
		ws.setAlarmsShowing(MainStage.controller.getAlarmSplitPane().getSideBar().isVisible());
		ws.setTreeShowing(MainStage.controller.getTreeSplitPane().getSideBar().isVisible());
		ws.setMaximized(isMaximized);
		
		ProgramSettings ps = new ProgramSettings(ws);
		ps.setLocaleName(MainStage.controller.menuBarController.getLocaleName());
		SchemeSettings ss = new SchemeSettings();
		ps.setSchemeSettings(ss);
		ss.setSchemeName(SingleObject.mainScheme.getSchemeName());
		ss.setSchemeScale(SingleObject.mainScheme.getRoot().getScaleX());
		
		final LinkedValue lv = new LinkedValue("", "");
		MainStage.controller.getAlarmsController().getTvTable().getColumns().forEach(c ->
			lv.setVal(lv.getVal().toString() + (c.isVisible() ? 1 : 0) + ":")
		);
		ps.setShowAlarmColumns(lv.getVal().toString());
		
		ps.setIconWidth(ps.getIconWidth() == 0 ? 16 : ps.getIconWidth());
		ps.setHotkeys(SingleObject.hotkeys.values().stream().collect(Collectors.toList()));
		
		try {
			ps.saveToFile(SingleObject.FILE_SETTINGS);
		} catch (JAXBException e) {
			LogFiles.log.log(Level.SEVERE, "void exitProgram()", e);
		}
		
		LogFiles.log.log(Level.INFO, "Exit");
		System.exit(0);
	}
	
	public SidePane getMainPane() {
		return mainPane;
	}
	
	public SidePane getAlarmSplitPane() {
		return vSplitPane;
	}
	
	public SidePane getTreeSplitPane() {
		return hSplitPane;
	}
	
	@FXML protected void showAlarm() {
//		System.out.println(SingleObject.getProgramSettings().getWinState().getAlarmDividerPositions());
//		System.out.println(vSplitPane.getExpandedSize());
//		System.out.println(vSplitPane.getSideBar().getPrefWidth());
		vSplitPane.showHideSide();
//		SingleObject.getProgramSettings().getWinState().setAlarmDividerPositions(vSplitPane.getExpandedSize());
	}
	
	public void updateTI(DvalTI ti) {
		updateTI(SingleObject.mainScheme, ti);
	}
	
	public void updateTI(Scheme mainScheme, DvalTI ti) {
		mainScheme.getListSignals().stream().filter(f -> f.getId() == ti.getSignalref()).forEach(s -> {
			try {
				EShape tt = mainScheme.getDeviceById(s.getVal().toString());
				if (tt == null) return;
				
				if(tt.getDt() != null && tt.getDt().compareTo(ti.getDt()) > 0) return;
				
				tt.setDt(ti.getDt());
				tt.setValue(ti.getSignalref(), ti.getVal(), s.getDt().toString());
				tt.setRcode(ti.getRcode());
								
				toolBarController.updateLabel(ti.getServdt());
			} catch (Exception e) {
				LogFiles.log.log(Level.SEVERE, "void updateTI(...)", e);
			}
		});	
	}
	
	public void setNotConfirmed(int idSignal, boolean isConfirmed) {		
		SingleObject.mainScheme.getListSignals().stream().filter(f -> f.getId() == idSignal).forEach(s -> {
			try {
				EShape tt = SingleObject.mainScheme.getDeviceById(s.getVal().toString());
				if (tt == null) return;
				
				tt.setNotConfirmed(isConfirmed);
			} catch (Exception e) {
				LogFiles.log.log(Level.SEVERE, "void setNotConfirmed(...)", e);
			}
		});	
	}
	
	public static void updateSignal(int idSigal, int sec) {
		SingleObject.mainScheme.getListSignals().stream().filter(f -> f.getId() == idSigal).forEach(s -> {
			EShape tt = SingleObject.mainScheme.getDeviceById(s.getVal().toString());
			if (tt == null) return;
			int status = SingleFromDB.signals.get(idSigal).getStatus();
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

	public AlarmTableController getAlarmsController() {
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
	        	bundle = new PropertyResourceBundle(new InputStreamReader(stream, StandardCharsets.UTF_8));
	            stream.close();
	        }
	        return bundle;
	    }
	}
}
