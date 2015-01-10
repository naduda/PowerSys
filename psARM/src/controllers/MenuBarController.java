package controllers;

import java.awt.MouseInfo;
import java.awt.Point;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import controllers.config.CustomPropertiesController;
import controllers.interfaces.IControllerInit;
import controllers.interfaces.StageLoader;
import controllers.journals.JAlarmsController;
import javafx.collections.ObservableList;
import javafx.scene.control.MenuBar;
import pr.common.MyFormatter;
import pr.common.Utils;
import pr.model.DvalTI;
import single.ProgramProperty;
import single.SingleFromDB;
import single.SingleObject;
import ui.MainStage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MenuBarController implements Initializable, IControllerInit {
	final FileChooser fileChooser = new FileChooser();
	private String localeName;
	private final Label lMenuExit = new Label();
	private final Label lMenuAbout = new Label();
	private JAlarmsController jAlarmController;
	
	@FXML private MenuBar menuBar;
	@FXML private Menu menuFile;
	@FXML private Menu menuJournals;
	@FXML private Menu menuReports;
	@FXML private Menu menuTools;
	@FXML private Menu menuSettings;
	@FXML private Menu menuExit;
	@FXML private Menu menuAbout;
	
	@Override
	public void initialize(URL url, ResourceBundle boundle) {
		try {
			setElementText(Controller.getResourceBundle(new Locale(SingleObject.getProgramSettings().getLocaleName())));
		} catch (Exception e) {
			setElementText(Controller.getResourceBundle(new Locale("en")));
		}
		
		menuExit.setGraphic(lMenuExit);
		lMenuExit.setOnMouseReleased(e -> Controller.exitProgram());
		menuAbout.setGraphic(lMenuAbout);
		lMenuAbout.setOnMouseReleased(e -> {
			MyFormatter.isDetailsLog = !MyFormatter.isDetailsLog;
			System.out.println("MyFormatter.isDetailsLog = " + MyFormatter.isDetailsLog);
		});
	}
	
	@FXML
	private void openScheme() {
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
	private void openJalarms() {
		Point p = MouseInfo.getPointerInfo().getLocation();
		StageLoader stage = new StageLoader("journals/JournalAlarms.xml", 
				SingleObject.getResourceBundle().getString("key_miJAlarms"), p, true);	
		jAlarmController = (JAlarmsController) stage.getController();
		
	    stage.show();
	}
	
	@FXML
	private void openJControl() {
		Point p = MouseInfo.getPointerInfo().getLocation();
		StageLoader stage = new StageLoader("journals/JournalControl.xml", 
				SingleObject.getResourceBundle().getString("key_miJControl"), p, true);
		
	    stage.show();
	}
	
	@FXML
	private void openJNormalMode() {
		Point p = MouseInfo.getPointerInfo().getLocation();
		StageLoader stage = new StageLoader("journals/JournalNormalMode.xml", 
				SingleObject.getResourceBundle().getString("key_miJNormalMode"), p, true);
		
	    stage.show();
	}
	
	@FXML
	private void openJswitchEq() {
		StageLoader stage = new StageLoader("journals/JournalSwitchEquipment.xml", 
				SingleObject.getResourceBundle().getString("key_miJswitchEq"), true);
		
	    stage.show();
	}
	
	@FXML
	private void openJuserEvent() {
		StageLoader stage = new StageLoader("journals/JournalUserEvents.xml", 
				SingleObject.getResourceBundle().getString("key_miJuserEvents"), true);
		
	    stage.show();
	}
	
	@FXML private void setBaseVal() {
		final StringBuilder idBuilder = new StringBuilder();
		idBuilder.append("{");
		SingleObject.mainScheme.getIdSignals().forEach(s -> idBuilder.append(s + ","));
		idBuilder.delete(idBuilder.length() - 1, idBuilder.length());
		idBuilder.append("}");
		
		final Map<Integer, DvalTI> oldTS =  SingleFromDB.psClient.getOldTS(idBuilder.toString());
		
		final List<String> query = new ArrayList<>();
		query.add("");
		SingleObject.mainScheme.getSignalsTS().forEach(s -> {
			DvalTI ts = oldTS.get(s);
			if (ts != null) {
				SingleFromDB.signals.get(ts.getSignalref()).setBaseval(ts.getVal());
				String sq = String.format("update t_signal set baseval=%s where idSignal=%s;", ts.getVal(), s);
				query.set(0, query.get(0) + sq);
			}
		});
		SingleFromDB.psClient.update(query.get(0));
	}
	
	@FXML protected void chat() {
		SingleObject.chat.show();
	}
	
	@FXML private void exit() {
		Controller.exitProgram();
	}
	
	@FXML
	private void changeLocale(ActionEvent event) {
		MenuItem mi = (MenuItem)event.getSource();
		localeName = mi.getId();
		MainStage.controller.setElementText(Controller.getResourceBundle(new Locale(localeName)));
		SingleObject.getProgramSettings().setLocaleName(localeName);
		ProgramProperty.localeName.set(localeName);
	}
	
	@FXML protected void schemeConfig() {
		StageLoader cpStage = new StageLoader("config/CustomProperties.xml", 
				SingleObject.getResourceBundle().getString("keyCustPropTitle"), true);
		CustomPropertiesController controller = (CustomPropertiesController) cpStage.getController();
		controller.updateStage();
		
		cpStage.show();
	}
	
	@FXML
	private void hotKeys() {
		StageLoader stage = new StageLoader("HotKeys.xml",
				SingleObject.getResourceBundle().getString("key_hotkeys"), true);
		
	    stage.show();
	}
	
	@Override
	public void setElementText(ResourceBundle rb) {
		menuFile.setText(rb.getString("keyFile"));
		menuJournals.setText(rb.getString("keyJournals"));
		menuReports.setText(rb.getString("keyReports"));
		menuTools.setText(rb.getString("keyTools"));
		menuSettings.setText(rb.getString("keySettings"));
		lMenuExit.setText(rb.getString("key_miExit"));
		lMenuAbout.setText(rb.getString("keyAbout"));

		menuBar.getMenus().forEach(m -> updateMenuItem(rb, m));
	}
	
	private void updateMenuItem(ResourceBundle rb, Menu m) {
		m.getItems().forEach(it -> {
			if (it.getId() != null) {
				if (rb.containsKey("key_" + it.getId())) it.setText(rb.getString("key_" + it.getId()));
				File icon = new File(Utils.getFullPath("./Icon/" + it.getId() + ".png"));
				if (icon.exists()) {
					ImageView iw = new ImageView("file:/" + icon.getAbsolutePath());
					iw.setFitHeight(SingleObject.getProgramSettings().getIconWidth());
					iw.setPreserveRatio(true);
					it.setGraphic(iw);
				}
				
				if (it instanceof Menu) updateMenuItem(rb, (Menu) it);
			}
		});
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

	public ObservableList<Menu> getMenus() {
		return menuBar.getMenus();
	}

	public MenuBar getMenuBar() {
		return menuBar;
	}
}
