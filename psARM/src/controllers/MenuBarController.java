package controllers;

import java.awt.MouseInfo;
import java.awt.Point;
import java.io.File;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import controllers.interfaces.IControllerInit;
import controllers.interfaces.StageLoader;
import pr.common.Utils;
import ui.Main;
import ui.MainStage;
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
	
	@FXML Menu menuFile;
	@FXML MenuItem miOpenScheme;
	@FXML MenuItem miOpenProject;
	@FXML Menu menuTrend;
	@FXML MenuItem miOpenTrend;
	@FXML MenuItem miCreateTrend;
	@FXML MenuItem miLogin;
	@FXML MenuItem miExit;
	@FXML Menu menuJournals;
	@FXML MenuItem miJAlarms;
	@FXML MenuItem miJControl;
	@FXML Menu menuReports;
	@FXML Menu menuTools;
	@FXML Menu menuSettings;
	@FXML Menu menuLanguage;
	@FXML Menu menuExit;
	@FXML Menu menuAbout;
	
	@Override
	public void initialize(URL url, ResourceBundle boundle) {
		try {
			setElementText(Controller.getResourceBundle(new Locale(Main.getProgramSettings().getLocaleName())));
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
		StageLoader stage = new StageLoader("JournalAlarms.xml", Main.getResourceBundle().getString("keyJalarms"), p);
		
		stage.setX(p.getX());
		stage.setY(p.getY());
	    stage.show();
	}
	
	@FXML
	private void openJControl(ActionEvent event) {
		Point p = MouseInfo.getPointerInfo().getLocation();
		StageLoader stage = new StageLoader("JournalControl.xml", Main.getResourceBundle().getString("keyJcontrol"), p);
		
	    stage.show();
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
		Main.getProgramSettings().setLocaleName(localeName);
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
		menuReports.setText(rb.getString("keyReports"));
		menuTools.setText(rb.getString("keyTools"));
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
