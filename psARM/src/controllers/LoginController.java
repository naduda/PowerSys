package controllers;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import state.ProgramSettings;
import state.SchemeSettings;
import state.WindowState;
import topic.ReceiveTopic;
import ui.Main;
import ui.MainStage;
import ui.UpdateTimeOut;
import controllers.interfaces.IControllerInit;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;

public class LoginController implements IControllerInit, Initializable {
	private static final int TIMEOUT_TI_SEC = 35;
	private static final int TIMEOUT_TS_SEC = 600;
	
	@FXML Label lbAddress;
	@FXML TextField txtAddress;
	@FXML Button btnOK;
	@FXML Button btnCancel;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setElementText(Controller.getResourceBundle(new Locale(Main.getProgramSettings().getLocaleName())));
	}
	
	@Override
	public void setElementText(ResourceBundle rb) {
		lbAddress.setText(rb.getString("keyIP"));
		btnOK.setText(rb.getString("keyLogin"));
		btnCancel.setText(rb.getString("keyExit"));
	}
	
	@FXML
	private void btnOK(ActionEvent event) {
		((Stage)btnOK.getScene().getWindow()).close();
		Main.ipAddress = txtAddress.getText();
		
		MainStage stage = new MainStage("./ui/Main.xml");
		Main.mainStage = stage;
		
        new Thread(new ReceiveTopic(Main.ipAddress + ":7676"), "ReceiveTopic").start();

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
        
        stage.setOnCloseRequest(e -> Controller.exitProgram());
        
        setStageParams(stage);
        stage.show();
	}
	
	@FXML
	private void btnCancel(ActionEvent event) {
		System.exit(0);
	}
	
	private void setStageParams(Stage stage) {
		ProgramSettings ps = Main.getProgramSettings();
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
}
