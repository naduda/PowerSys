package ui;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import model.ConfTree;
import model.Tsignal;
import controllers.Controller;
import ua.pr.common.ToolsPrLib;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainStage extends Stage {

	private static final String DEFAULT_SCHEME = "ПС-110 кВ 'Блок-4'";
	public static ListView<String> lvTree;
	public static BorderPane bpScheme;
	public static Map<Integer, Scheme> schemes = new HashMap<>();
	public static Controller controller;

	public MainStage(String pathXML) {
		try {
			FXMLLoader loader = new FXMLLoader(new URL("file:/" + ToolsPrLib.getFullPath("./ui/Main.xml")));
			Parent root = loader.load();
			controller = loader.getController();

			Scene scene = new Scene(root);      
			setTitle("PowerSys ARM");

			bpScheme = controller.getBpScheme();
			
			SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
			Timestamp dt = new Timestamp(new Date().getTime());
			try {
				dt = new Timestamp(formatter.parse(formatter.format(new Date())).getTime());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			Map<Integer, ConfTree> confTree = Main.pdb.getConfTreeMap();
			
			for (Tsignal sign : Main.signals.values()) {
				ConfTree ct = confTree.get(sign.getNoderef());
				String location = ct.getNodename();
				while (ct.getParentref() > 0) {
					ct = confTree.get(ct.getParentref());
					location = location + "/" + ct.getNodename();
				}
				sign.setLocation(location);
			}
			
			Main.pdb.getAlarms(dt).forEach(a -> { controller.getAlarmsController().addAlarm(a); });
			
			setScheme(DEFAULT_SCHEME);
			controller.getSpTreeController().expandSchemes();
			controller.getSpTreeController().addContMenu();
			setScene(scene);
		} catch (IOException e) {
			System.err.println("MainStage(String pathXML)");
		}
	}
	
	public static void setScheme(String schemeName) {
		if (schemeName == null) {
			Main.mainScheme = new Scheme();
		} else {
			Main.mainScheme = new Scheme(ToolsPrLib.getFullPath("./schemes/" + schemeName + ".xml"));

			TreeItem<Scheme> ti = new TreeItem<>(Main.mainScheme);
			controller.getSpTreeController().addScheme(ti);
	        MainStage.schemes.put(Main.mainScheme.getIdScheme(), Main.mainScheme);
		}
		bpScheme.setCenter(Main.mainScheme);
	}
}
