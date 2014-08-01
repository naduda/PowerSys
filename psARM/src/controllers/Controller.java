package controllers;

import graphicObject.Breaker;
import graphicObject.DigitalDevice;
import graphicObject.DisConnectorGRND;
import graphicObject.Disconnector;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import ui.Main;
import ui.Scheme;
import model.DvalTI;
import model.DvalTS;
import model.LinkedValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
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
	@FXML
	private void showAlarm(ActionEvent event) {
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
	
	public void updateTI(DvalTI ti) {
		DigitalDevice tt = Main.mainScheme.getDigitalDeviceById(ti.getSignalref() + "");
		if (tt == null) return;
		
		tt.setLastDataDate(ti.getServdt());
		tt.setText(tt.getDecimalFormat().format(ti.getVal()));
		toolBarController.updateLabel(df.format(ti.getServdt()));
	}
	
	public void updateTI(Scheme mainScheme, LinkedValue lv) {
		DigitalDevice tt = mainScheme.getDigitalDeviceById(lv.getIdsignal() + "");
		if (tt == null) return;

		tt.setLastDataDate(new Date(System.currentTimeMillis() - 10000));
		tt.setText(tt.getDecimalFormat().format(lv.getVal()));
	}

	public void updateTS(DvalTS ts) {
		Group tt = Main.mainScheme.getDeviceById(ts.getSignalref() + "");		
		if (tt == null) return;
		
		if (tt.getClass().getName().toLowerCase().endsWith("disconnector")) {
			Disconnector dc = (Disconnector) tt;
			dc.changeTS((int)ts.getVal());
		} else if (tt.getClass().getName().toLowerCase().endsWith("disconnectorgrnd")) {
			DisConnectorGRND dcg = (DisConnectorGRND) tt;
			dcg.changeTS((int)ts.getVal());
		} else if (tt.getClass().getName().toLowerCase().endsWith("breaker")) {
			Breaker br = (Breaker) tt;
			br.setLastDataDate(ts.getServdt());
			br.changeTS((int)ts.getVal());
		}
		toolBarController.updateLabel(df.format(ts.getServdt()));
	}
	
	public void updateTS(Scheme mainScheme, LinkedValue lv) {
		Group tt = mainScheme.getDeviceById(lv.getIdsignal() + "");		
		if (tt == null) return;
		
		if (tt.getClass().getName().toLowerCase().endsWith("disconnector")) {
			Disconnector dc = (Disconnector) tt;
			dc.changeTS((int)lv.getVal());
		} else if (tt.getClass().getName().toLowerCase().endsWith("disconnectorgrnd")) {
			DisConnectorGRND dcg = (DisConnectorGRND) tt;
			dcg.changeTS((int)lv.getVal());
		} else if (tt.getClass().getName().toLowerCase().endsWith("breaker")) {
			Breaker br = (Breaker) tt;
			br.setLastDataDate(new Date(System.currentTimeMillis() - 10000));
			br.changeTS((int)lv.getVal());
		}
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
