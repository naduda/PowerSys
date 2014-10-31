package ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.xml.bind.JAXBException;

import pr.common.Utils;
import controllers.Controller;
import controllers.interfaces.StageLoader;
import state.ProgramSettings;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
	public static final String FILE_SETTINGS = Utils.getFullPath("./Settings.xml");
	private static ProgramSettings ps = getProgramSettings();
	
	public static String ipAddress = "";	
	public static ClassLoader classLoader;
	public static Scheme mainScheme;
	public static Stage mainStage;

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		try {
			File file = new File(Utils.getFullPath("./lang"));
			URL[] urls = {file.toURI().toURL()};
			classLoader = new URLClassLoader(urls);
		} catch (MalformedURLException e) {
			System.out.println("---");
		}
		
		stage = new StageLoader("Login.xml", getResourceBundle().getString("keyLogin"), false);
//		stage.initStyle(StageStyle.UNDECORATED);
		stage.setResizable(false);
        stage.show();
	}
//	--------------------------------------------------------------
	public static ProgramSettings getProgramSettings() {
		if (ps == null) {
			try {
				ps = ProgramSettings.getFromFile(FILE_SETTINGS);
			} catch (FileNotFoundException | JAXBException e) {
				e.printStackTrace();
			}
			if (ps.getLocaleName() == null) {
				ps.setLocaleName("en");
			}
		}
		return ps;
	}
	
	public static ResourceBundle getResourceBundle() {
		return Controller.getResourceBundle(new Locale(ps.getLocaleName()));
	}
}
