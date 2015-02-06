package controllers;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;

import javafx.scene.image.Image;
import javafx.scene.input.KeyCodeCombination;
import pr.common.Utils;
import pr.log.LogFiles;
import single.SingleFromDB;
import single.SingleObject;
import state.ProgramSettings;
import state.SchemeSettings;
import state.WindowState;
import topic.ClientPowerSys;
import topic.ReceiveTopic;
import ui.MainStage;
import ui.UpdateTimeOut;
import controllers.interfaces.IControllerInit;
import controllers.interfaces.StageLoader;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import javafx.stage.Window;

public class LoginController implements IControllerInit, Initializable {	
	@FXML private Label lbAddress;
	@FXML private TextField txtAddress;
	@FXML private Button btnOK;
	@FXML private Button btnCancel;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setElementText(Controller.getResourceBundle(new Locale(SingleObject.getProgramSettings().getLocaleName())));
	}
	
	@Override
	public void setElementText(ResourceBundle rb) {
		lbAddress.setText(rb.getString("keyIP"));
		btnOK.setText(rb.getString("key_miLogin"));
		btnCancel.setText(rb.getString("key_miExit"));
	}
	
	@FXML public void btnOK() {
		LogFiles.log.log(Level.INFO, "Building stage");
		((Stage)btnOK.getScene().getWindow()).close();
		SingleObject.ipAddress = txtAddress.getText();
		
		new SingleFromDB(new ClientPowerSys());
		
		MainStage stage = new MainStage("./ui/Main.xml");
		try {
			stage.getIcons().add(new Image(new File(Utils.getFullPath("./Icon/PowerSyS_ARM.png")).toURI().toURL().toString()));
		} catch (MalformedURLException e) {
			LogFiles.log.log(Level.SEVERE, "Application Icon set ...", e);
		}
		SingleObject.mainStage = stage;
		
        new Thread(new ReceiveTopic(SingleObject.ipAddress + ":7676"), "ReceiveTopic").start();
        new Thread(() -> new UpdateTimeOut(SingleFromDB.validTimeOutTI, 1), "UpdateTimeOut_TI").start();
        new Thread(() -> new UpdateTimeOut(SingleFromDB.validTimeOutTS, 2), "UpdateTimeOut_TS").start();
        
        stage.setOnCloseRequest(e -> Controller.exitProgram());
        
        while (SingleObject.mainScheme == null) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
			}
		}
        
        setStageParams(stage);
        
        SingleObject.hotkeys.values().stream().filter(f -> f.getCode().length() > 0).forEach(h -> {
        	MenuItem mi = SingleObject.getMenuItemById(null, h.getIdCode());
        	KeyCodeCombination kk = new KeyCodeCombination(KeyCode.getKeyCode(h.getCode().toUpperCase()), 
        			h.isShift() ? KeyCombination.ModifierValue.DOWN : KeyCombination.ModifierValue.UP,
   					h.isCtrl() ? KeyCombination.ModifierValue.DOWN : KeyCombination.ModifierValue.UP,
   					h.isAlt() ? KeyCombination.ModifierValue.DOWN : KeyCombination.ModifierValue.UP,
					KeyCombination.ModifierValue.UP, KeyCombination.ModifierValue.UP);
        	if (mi != null) {
        		mi.setAccelerator(kk);
        		SingleObject.mainStage.getScene().getAccelerators().put(kk, () -> mi.getOnAction().handle(null));
        		
        	} else {
        		Button btn = (Button) MainStage.controller.getToolBarController().getTbMain().getItems()
						.filtered(f -> h.getIdCode().equals(f.getId())).get(0);
				SingleObject.mainStage.getScene().getAccelerators().put(kk, () -> btn.getOnAction().handle(null));
        	}
        });
        
        SingleObject.chat = new StageLoader("Chat.xml", 
				SingleObject.getResourceBundle().getString("keyChatTitle"), true);
        
        LogFiles.log.log(Level.INFO, "Show");
        stage.show();
	}
	
	@FXML
	protected void btnCancel() {
		System.exit(0);
	}
	
	private void setStageParams(Stage stage) {
		Controller ctrlr = MainStage.controller;
		ProgramSettings ps = SingleObject.getProgramSettings();
		WindowState ws = ps.getWinState();
		Window w = stage.getScene().getWindow();
		w.setX(ws.getX());
		w.setY(ws.getY());
		w.setWidth(ws.getWidth());
		w.setHeight(ws.getHeight());
		stage.setMaximized(ws.isMaximized());
		
		Platform.runLater(() -> {
			StatusListener mainSL = new StatusListener(ctrlr, ws, 1);
			ctrlr.getMainPane().getStatus().addListener(mainSL);
			
			StatusListener alarmSL = new StatusListener(ctrlr, ws, 2);
			ctrlr.getAlarmSplitPane().getStatus().addListener(alarmSL);
			
			if (ws.isFullScreen()) {
				ctrlr.getMainPane().showSide();
			} else {
				ctrlr.getMainPane().getStatus().set(true);
			}
			
			ctrlr.getAlarmSplitPane().setExpandedSize(ws.getAlarmDividerPositions());
			ctrlr.getTreeSplitPane().setExpandedSize(ws.getTreeDividerPositions());
		});
		
		ctrlr.getMainPane().isShowingProperty()
			.bind(SingleObject.mainStage.fullScreenProperty().isNotEqualTo(new SimpleBooleanProperty(true)));
		ctrlr.getvToolBarPane().isShowingProperty().bind(ctrlr.getMainPane().isShowingProperty());
		ctrlr.getMainPane().getSideBar().visibleProperty().addListener((observ, old, newValue) -> {
			if (newValue) {
				Platform.runLater(() -> ctrlr.getTreeSplitPane().showSide());
			} else {
				ctrlr.getTreeSplitPane().hideSide();
				MainStage.fitToPage();
			}
		});
		
		ctrlr.getToolBarController().getHideLeft().setGraphic(null);
		ctrlr.getTreeSplitPane().getSideBar().visibleProperty().addListener((observ, old, newValue) -> {
			if (newValue) {
				ctrlr.getToolBarController().getHideLeft().getStyleClass().add("hide-left");
				ctrlr.getToolBarController().getHideLeft().getStyleClass().remove("show-right");
			} else {
				ctrlr.getToolBarController().getHideLeft().getStyleClass().remove("hide-left");
				ctrlr.getToolBarController().getHideLeft().getStyleClass().add("show-right");
			}
		});
		
		ctrlr.getToolBarController().getFit().setGraphic(null);
		ctrlr.getToolBarController().getFit().getStyleClass().add("full-screen-off");
		SingleObject.mainStage.fullScreenProperty().addListener((observ, old, newValue) -> {
			if (newValue) {
				ctrlr.getToolBarController().getFit().getStyleClass().add("full-screen-on");
				ctrlr.getToolBarController().getFit().getStyleClass().remove("full-screen-off");
			} else {
				ctrlr.getToolBarController().getFit().getStyleClass().remove("full-screen-on");
				ctrlr.getToolBarController().getFit().getStyleClass().add("full-screen-off");
			}
		});
		
		SchemeSettings ss = ps.getSchemeSettings();
		SingleObject.mainScheme.getRoot().setScaleX(ss.getSchemeScale());
		SingleObject.mainScheme.getRoot().setScaleY(ss.getSchemeScale());
		ctrlr.getMenuBarController().setLocaleName(ps.getLocaleName());
		
		ps.getHotkeys().forEach(e -> SingleObject.hotkeys.put(e.getIdCode(), e));
	}
	
	private class StatusListener implements ChangeListener<Boolean> {
		private Controller ctrlr;
		private WindowState ws;
		private int number;
		
		public StatusListener(Controller ctrlr, WindowState ws, int number) {
			this.ctrlr = ctrlr;
			this.ws = ws;
			this.number = number;
		}
		
		@Override
		public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
			if (newValue) {
				switch (number) {
					case 1:
						double oldDuration = ctrlr.getAlarmSplitPane().getDuration();
						ctrlr.getAlarmSplitPane().setDuration(0);
						if (ws.isAlarmsShowing()) {
							ctrlr.getAlarmSplitPane().setExpandedSize(ws.getAlarmDividerPositions());
							ctrlr.getAlarmSplitPane().showSide();
						} else {
							ctrlr.getAlarmSplitPane().hideSide();
						}
						ctrlr.getAlarmSplitPane().setDuration(oldDuration);
						ctrlr.getMainPane().getStatus().removeListener(this);
						break;
	
					case 2:
						oldDuration = ctrlr.getTreeSplitPane().getDuration();
						ctrlr.getTreeSplitPane().setDuration(0);
						if (ws.isTreeShowing()) {
							ctrlr.getTreeSplitPane().setExpandedSize(ws.getTreeDividerPositions());
							ctrlr.getTreeSplitPane().showSide();
						} else {
							ctrlr.getTreeSplitPane().hideSide();
						}
						ctrlr.getTreeSplitPane().setDuration(oldDuration);
						ctrlr.getAlarmSplitPane().getStatus().removeListener(this);
						break;
				}
				
			}
		}
	}
}
