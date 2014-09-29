package controllers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.xml.bind.JAXBException;

import state.ProgramSettings;
import state.SchemeSettings;
import state.WindowState;
import svg2fx.Convert;
import svg2fx.fxObjects.EShape;
import ui.Main;
import ui.MainStage;
import ui.Scheme;
import model.DvalTI;
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

public class Controller {
	private final DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	
	@FXML private ToolBarController toolBarController;
	@FXML private MenuBarController menuBarController;
	@FXML private TreeController spTreeController;	
	@FXML private AlarmController bpAlarmsController;	
	@FXML private Pane bpAlarms;
	@FXML private BorderPane bpScheme;
	@FXML private SplitPane vSplitPane;
	@FXML private SplitPane hSplitPane;
	
	public static void exitProgram() {
		Window w = Main.mainScheme.getScene().getWindow();
		WindowState ws = new WindowState(w.getX(), w.getY(), w.getWidth(), w.getHeight());
		
		ws.setAlarmDividerPositions(MainStage.controller.getAlarmSplitPane().getDividers().get(0).getPosition());
		ws.setTreeDividerPositions(MainStage.controller.getTreeSplitPane().getDividers().get(0).getPosition());
		
		ProgramSettings ps = new ProgramSettings(ws);
		SchemeSettings ss = new SchemeSettings();
		ps.setSchemeSettings(ss);
		ss.setSchemeName(Main.mainScheme.getSchemeName());
		ss.setSchemeScale(Main.mainScheme.getRoot().getScaleX());
		
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
			EShape tt = mainScheme.getDeviceById(s.getValue());
			tt.setValue(ti.getVal(), s.getTypeSignal());
			
			toolBarController.updateLabel(df.format(ti.getServdt()));
			//toolBarController.setTsLastDate(ti.getServdt().getTime());
		});	
	}
	
	public static void updateSignal(int idSigal, int type_, int sec) {
		Convert.listSignals.stream().filter(f -> f.getKey().equals(idSigal)).forEach(s -> {
			EShape tt = Main.mainScheme.getDeviceById(s.getValue());
			if (tt == null) return;
			tt.updateSignal(sec);
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
}
