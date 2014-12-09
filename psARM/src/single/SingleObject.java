package single;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.logging.Level;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.xml.bind.JAXBException;

import controllers.Controller;
import pr.common.Utils;
import pr.log.LogFiles;
import pr.svgObjects.SVG;
import state.HotKeyClass;
import state.ProgramSettings;
import svg2fx.fxObjects.EShape;
import ui.MainStage;
import ui.Scheme;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public class SingleObject {
	public static String ipAddress = "";
	public static Stage mainStage;
	public static Scheme mainScheme;
	public static String activeSchemeSignals = "";
	public static EShape selectedShape;
	public static ByteArrayInputStream schemeInputStream;
	public static SVG svg;
	public static Map<String, HotKeyClass> hotkeys = new HashMap<>();
	public static AlarmActivities alarmActivities = new AlarmActivities();
	
	public static final ScriptEngineManager manager = new ScriptEngineManager();
	public static final ScriptEngine engine = manager.getEngineByName("nashorn");
	public static final Invocable invokeEngine = (Invocable) SingleObject.engine;
	public static final Map<String, Map<String, String>> readedScripts = new HashMap<>();
	
	private static ClassLoader classLoader;
	public static ClassLoader getClassLoader() {
		if (classLoader == null) {
			try {
				File file = new File(Utils.getFullPath("./lang"));
				URL[] urls = {file.toURI().toURL()};
				SingleObject.classLoader = new URLClassLoader(urls);
			} catch (MalformedURLException e) {
				LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
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
	
	public static MenuItem getMenuItemById(Object obj, String id) {
		if (obj == null) {
			obj = MainStage.controller.getMenuBarController().getMenuBar();
			for(Menu m : ((MenuBar)obj).getMenus()) {
				MenuItem mi = getMenuItemById(m, id);
				if (mi != null) return mi;
			}
		} else if (obj instanceof Menu) {
			for (MenuItem it : ((Menu)obj).getItems()) {
				if (it instanceof Menu) {
					MenuItem mi = getMenuItemById(it, id);
					if (mi != null) return mi;
				}
				if (id.equals(it.getId())) return it;
			}
		}
		return null;
	}
	
	public static List<Integer> getActiveSchemeSignals() {
		List<Integer> ret = new ArrayList<>();
		String str = activeSchemeSignals.substring(1, activeSchemeSignals.length() - 1);
		
		StringTokenizer st = new StringTokenizer(str, ",");
		while (st.hasMoreElements()) {
			ret.add(Integer.parseInt(st.nextElement().toString()));
		}
		return ret;
	}
}
