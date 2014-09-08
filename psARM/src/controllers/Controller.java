package controllers;

import graphicObject.Breaker;
import graphicObject.DigitalDevice;
import graphicObject.DisConnectorGRND;
import graphicObject.Disconnector;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import svg2fx.Convert;
import svg2fx.fxObjects.EShape;
import ui.Main;
import ui.Scheme;
import model.DvalTI;
import model.DvalTS;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

public class Controller {
	
	public static final DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	
	@FXML private ToolBarController toolBarController;
	@FXML private MenuBarController menuBarController;
	@FXML private TreeController spTreeController;	
	@FXML private AlarmController bpAlarmsController;	
	@FXML private Pane bpAlarms;
	@FXML private BorderPane bpScheme;
	
	public static void exitProgram() {
		System.out.println("exit");
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
		SplitPane sp = ((SplitPane)bpAlarms.getParent().getParent());
		System.out.println(sp.getDividerPositions()[0] + " - " + isHide);
		if (isHide) {
			sp.setDividerPositions(oldAlarmsHeight);
		} else {
			oldAlarmsHeight = sp.getDividerPositions()[0];
			sp.setDividerPositions(1);
		}
		isHide = !isHide;
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
//		DigitalDevice tt = mainScheme.getDigitalDeviceById(ti.getSignalref() + "");
//		if (tt == null) return;
//
//		tt.setLastDataDate(new Date(System.currentTimeMillis() - 10000));
//		tt.setText(tt.getDecimalFormat().format(ti.getVal()));
//		if (ti.getServdt().getTime() > toolBarController.getTsLastDate()) {
//			toolBarController.updateLabel(df.format(ti.getServdt()));
//			toolBarController.setTsLastDate(ti.getServdt().getTime());
//		}
		
		Convert.listSignals.stream().filter(f -> f.getKey().equals(ti.getSignalref())).forEach(s -> {
			Group tt = mainScheme.getDeviceById(s.getValue());
			System.out.println(s.getValue());
		});
		
	}

	public void updateTS(DvalTS ts) {
		updateTS(Main.mainScheme, ts);
	}
	
	public void updateTS(Scheme mainScheme, DvalTS ts) {
		Convert.listSignals.stream().filter(f -> f.getKey().equals(ts.getSignalref())).forEach(s -> {
			EShape tt = mainScheme.getDeviceById(s.getValue());
			tt.setVal(ts.getVal());	
		});
	}
	
	public static void updateSignal(int idSigal, int type_, int sec) {
		Object tt = type_ == 1 ? Main.mainScheme.getDigitalDeviceById(idSigal + "") : Main.mainScheme.getDeviceById(idSigal + "");
		if (tt == null) return;

		if (tt.getClass().getName().toLowerCase().endsWith("disconnector")) {

		} else if (tt.getClass().getName().toLowerCase().endsWith("disconnectorgrnd")) {

		} else if (tt.getClass().getName().toLowerCase().endsWith("breaker")) {
			Breaker dd = (Breaker) tt;
			dd.updateSignal(sec);
		} else if (tt.getClass().getName().toLowerCase().endsWith("digitaldevice")) {
			DigitalDevice dd = (DigitalDevice) tt;
			dd.updateSignal(sec);
		}
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
