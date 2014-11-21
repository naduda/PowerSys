package controllers;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;

import javafx.scene.input.KeyCodeCombination;
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
import javafx.application.Platform;
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
	private static final int TIMEOUT_TI_SEC = 35;
	private static final int TIMEOUT_TS_SEC = 600;
	
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
		SingleObject.mainStage = stage;
		
        new Thread(new ReceiveTopic(SingleObject.ipAddress + ":7676"), "ReceiveTopic").start();
        new Thread(() -> new UpdateTimeOut(TIMEOUT_TI_SEC, 1), "UpdateTimeOut_TI").start();
        new Thread(() -> new UpdateTimeOut(TIMEOUT_TS_SEC, 2), "UpdateTimeOut_TS").start();
        
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
        
        LogFiles.log.log(Level.INFO, "Show");
        stage.show();
	}
	
	@FXML
	protected void btnCancel() {
		System.exit(0);
	}
	
	private void setStageParams(Stage stage) {
		ProgramSettings ps = SingleObject.getProgramSettings();
		WindowState ws = ps.getWinState();
		Window w = stage.getScene().getWindow();
		w.setX(ws.getX());
		w.setY(ws.getY());
		w.setWidth(ws.getWidth());
		w.setHeight(ws.getHeight());
		
		MainStage.controller.getAlarmSplitPane().getDividers().get(0).positionProperty().addListener((observ, old, value) ->
			Platform.runLater(() -> MainStage.controller.getTreeSplitPane().setDividerPositions(ws.getTreeDividerPositions()))
		);
		
		Platform.runLater(() -> MainStage.controller.getAlarmSplitPane().setDividerPositions(ws.getAlarmDividerPositions()));
		
		SchemeSettings ss = ps.getSchemeSettings();
		SingleObject.mainScheme.getRoot().setScaleX(ss.getSchemeScale());
		SingleObject.mainScheme.getRoot().setScaleY(ss.getSchemeScale());
		MainStage.controller.getMenuBarController().setLocaleName(ps.getLocaleName());
		
		ps.getHotkeys().forEach(e -> SingleObject.hotkeys.put(e.getIdCode(), e));
	}
}
