package controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import pr.common.Utils;
import ui.Main;
import ui.MainStage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MenuBarController implements Initializable {
	final FileChooser fileChooser = new FileChooser();
	private String localeName;
	private final Label lMenuExit = new Label();
	
	@FXML Menu menuFile;
	@FXML MenuItem miOpenScheme;
	@FXML MenuItem miOpenProject;
	@FXML Menu menuTrend;
	@FXML MenuItem miOpenTrend;
	@FXML MenuItem miCreateTrend;
	@FXML MenuItem miLogin;
	@FXML MenuItem miExit;
	@FXML Menu menuJournals;
	@FXML MenuItem miOpenJAlarms;
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
		Stage stage = new Stage();
		
		try {
			FXMLLoader loader = new FXMLLoader(new URL("file:/" + Utils.getFullPath("./ui/JournalAlarms.xml")));
			Parent root = loader.load();
//			JAlarmsController jAlarmController = loader.getController();
//			jAlarmController.setAlarms();
			
			Scene scene = new Scene(root);
			stage.setScene(scene);
//			ResourceBundle rb = Controller.getResourceBundle(new Locale(Main.getProgramSettings().getLocaleName()));
//			stage.setTitle(rb.getString("keyDataTitle"));
//			stage.initModality(Modality.NONE);
//			stage.initOwner(((Control)event.getSource()).getScene().getWindow());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
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
		miOpenJAlarms.setText(rb.getString("keyJalarms"));
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
}
