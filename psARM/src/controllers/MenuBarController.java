package controllers;

import java.awt.MouseInfo;
import java.awt.Point;
import java.io.File;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import controllers.interfaces.IControllerInit;
import controllers.interfaces.StageLoader;
import controllers.journals.JAlarmsController;
import pr.common.Utils;
import pr.model.DvalTS;
import ui.MainStage;
import ui.single.SingleFromDB;
import ui.single.SingleObject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MenuBarController implements Initializable, IControllerInit {
	final FileChooser fileChooser = new FileChooser();
	private String localeName;
	private final Label lMenuExit = new Label();
	private JAlarmsController jAlarmController;
	
	@FXML private Menu menuFile;
	@FXML private MenuItem miOpenScheme;
	@FXML private MenuItem miOpenProject;
	@FXML private Menu menuTrend;
	@FXML private MenuItem miOpenTrend;
	@FXML private MenuItem miCreateTrend;
	@FXML private MenuItem miLogin;
	@FXML private MenuItem miExit;
	@FXML private Menu menuJournals;
	@FXML private MenuItem miJAlarms;
	@FXML private MenuItem miJControl;
	@FXML private MenuItem miJNormalMode;
	@FXML private MenuItem miJswitchEq;
	@FXML private MenuItem miJuserEvents;
	@FXML private Menu menuReports;
	@FXML private Menu menuTools;
	@FXML private MenuItem miSetBaseVal;
	@FXML private Menu menuSettings;
	@FXML private Menu menuLanguage;
	@FXML private Menu menuExit;
	@FXML private Menu menuAbout;
	
	@Override
	public void initialize(URL url, ResourceBundle boundle) {
		try {
			setElementText(Controller.getResourceBundle(new Locale(SingleObject.getProgramSettings().getLocaleName())));
		} catch (Exception e) {
			setElementText(Controller.getResourceBundle(new Locale("en")));
		}
		
		menuExit.setText("");
		menuExit.setGraphic(lMenuExit);
		lMenuExit.setOnMouseReleased(e -> {Controller.exitProgram();});
	}
	
	@FXML
	private void openScheme(ActionEvent event) {
		FileChooser.ExtensionFilter extentionFilter = new FileChooser.ExtensionFilter("SVG files (*.svg)", "*.svg");
		fileChooser.getExtensionFilters().add(extentionFilter);

		File userDirectory = new File(Utils.getFullPath("./schemes"));
		fileChooser.setInitialDirectory(userDirectory);
		
		File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
        	String schemeName = file.getName().split("\\.")[0];
        	
	        if (MainStage.schemes.get(schemeName) == null) {
	            MainStage.setScheme(schemeName);
        	}
        }
	}
	
	@FXML
	private void openJalarms(ActionEvent event) {
		Point p = MouseInfo.getPointerInfo().getLocation();
		StageLoader stage = new StageLoader("journals/JournalAlarms.xml", 
				SingleObject.getResourceBundle().getString("keyJalarms"), p, true);	
		jAlarmController = (JAlarmsController) stage.getController();
		
	    stage.show();
	}
	
	@FXML
	private void openJControl(ActionEvent event) {
		Point p = MouseInfo.getPointerInfo().getLocation();
		StageLoader stage = new StageLoader("journals/JournalControl.xml", 
				SingleObject.getResourceBundle().getString("keyJcontrol"), p, true);
		
	    stage.show();
	}
	
	@FXML
	private void openJNormalMode(ActionEvent event) {
		Point p = MouseInfo.getPointerInfo().getLocation();
		StageLoader stage = new StageLoader("journals/JournalNormalMode.xml", 
				SingleObject.getResourceBundle().getString("keyJNormalMode"), p, true);
		
	    stage.show();
	}
	
	@FXML
	private void openJswitchEq(ActionEvent event) {
		StageLoader stage = new StageLoader("journals/JournalSwitchEquipment.xml", 
				SingleObject.getResourceBundle().getString("keyJswitchingEquipment"), true);
		
	    stage.show();
	}
	
	@FXML
	private void openJuserEvent(ActionEvent event) {
		StageLoader stage = new StageLoader("journals/JournalUserEvents.xml", 
				SingleObject.getResourceBundle().getString("keyJuserEvent"), true);
		
	    stage.show();
	}
	
	@FXML
	private void setBaseVal(ActionEvent event) {
		try {
			final Map<Integer, DvalTS> oldTS =  SingleFromDB.psClient.getOldTS();
			final List<String> query = new ArrayList<>();
			query.add("");
			SingleObject.mainScheme.getSignalsTS().forEach(s -> {
				DvalTS ts = oldTS.get(s);
				if (ts != null) {
					SingleFromDB.signals.get(ts.getSignalref()).setBaseval(ts.getVal());
					String sq = String.format("update t_signal set baseval=%s where idSignal=%s;", ts.getVal(), s);
					query.set(0, query.get(0) + sq);
				}
			});
			SingleFromDB.psClient.update(query.get(0));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	private void exit(ActionEvent event) {
		Controller.exitProgram();
	}
	
	@FXML
	private void changeLocale(ActionEvent event) {
		MenuItem mi = (MenuItem)event.getSource();
		localeName = mi.getId();
		MainStage.controller.setElementText(Controller.getResourceBundle(new Locale(localeName)));
		SingleObject.getProgramSettings().setLocaleName(localeName);
	}
	
	@Override
	public void setElementText(ResourceBundle rb) {
		menuFile.setText(rb.getString("keyFile"));
		miOpenScheme.setText(rb.getString("keyOpenFile"));
		miOpenProject.setText(rb.getString("keyOpenProject"));
		menuTrend.setText(rb.getString("keyTrend"));
		miOpenTrend.setText(rb.getString("keyTrendOpen"));
		miCreateTrend.setText(rb.getString("keyTrendCreate"));
		miLogin.setText(rb.getString("keyLogin"));
		miExit.setText(rb.getString("keyExit"));
		menuJournals.setText(rb.getString("keyJournals"));
		miJAlarms.setText(rb.getString("keyJalarms"));
		miJControl.setText(rb.getString("keyJcontrol"));
		miJNormalMode.setText(rb.getString("keyJNormalMode"));
		miJswitchEq.setText(rb.getString("keyJswitchingEquipment"));
		miJuserEvents.setText(rb.getString("keyJuserEvent"));
		menuReports.setText(rb.getString("keyReports"));
		menuTools.setText(rb.getString("keyTools"));
		miSetBaseVal.setText(rb.getString("keySetBaseVal"));
		menuSettings.setText(rb.getString("keySettings"));
		menuLanguage.setText(rb.getString("keyLanguage"));
		lMenuExit.setText(rb.getString("keyExit"));
		menuAbout.setText(rb.getString("keyAbout"));
	}

	public String getLocaleName() {
		return localeName;
	}

	public void setLocaleName(String localeName) {
		this.localeName = localeName;
	}

	public JAlarmsController getjAlarmController() {
		return jAlarmController;
	}
}
