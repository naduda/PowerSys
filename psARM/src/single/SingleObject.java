package single;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;

import javax.xml.bind.JAXBException;

import controllers.Controller;
import pr.common.Utils;
import pr.log.LogFiles;
import state.ProgramSettings;
import svg2fx.fxObjects.EShape;
import svg2fx.svgObjects.SVG;
import ui.Scheme;
import javafx.stage.Stage;

public class SingleObject {
	public static String ipAddress = "";
	public static Stage mainStage;
	public static Scheme mainScheme;
	public static EShape selectedShape;
	public static ByteArrayInputStream schemeInputStream;
	public static SVG svg;

	private static ClassLoader classLoader;
	public static ClassLoader getClassLoader() {
		if (classLoader == null) {
			try {
				File file = new File(Utils.getFullPath("./lang"));
				URL[] urls = {file.toURI().toURL()};
				SingleObject.classLoader = new URLClassLoader(urls);
			} catch (MalformedURLException e) {
				pr.log.LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
			}
		}
		return classLoader;
	}
	
	public static final String FILE_SETTINGS = Utils.getFullPath("./Settings.xml");
	private static ProgramSettings ps;
	public static ProgramSettings getProgramSettings() {
		if (ps == null) {
			try {
				ps = ProgramSettings.getFromFile(FILE_SETTINGS);
			} catch (FileNotFoundException | JAXBException e) {
				LogFiles.log.log(Level.SEVERE, "void getProgramSettings()", e);
			}
		}
		return ps;
	}
	
	public static ResourceBundle getResourceBundle() {
		return Controller.getResourceBundle(new Locale(getProgramSettings().getLocaleName()));
	}
	
}
