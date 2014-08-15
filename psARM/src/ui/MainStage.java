package ui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import model.ConfTree;
import model.Tsignal;
import controllers.Controller;
import topic.ClientPowerSys;
import ua.pr.common.ToolsPrLib;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainStage extends Stage {

	private static final String DEFAULT_SCHEME = "ПС-110 кВ 'Блок-4'";
	public static ClientPowerSys psClient = new ClientPowerSys();
	public static Map<Integer, Tsignal> signals = psClient.getTsignalsMap();
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

			Map<Integer, ConfTree> confTree = psClient.getConfTreeMap();			
			signals.values().forEach(s -> {
				ConfTree ct = confTree.get(s.getNoderef());
				String location = ct.getNodename();
				while (ct.getParentref() > 0) {
					ct = confTree.get(ct.getParentref());
					location = location + "/" + ct.getNodename();
				}
				s.setLocation(location);
			});

			psClient.getAlarmsCurrentDay().forEach(a -> { controller.getAlarmsController().addAlarm(a); });

			setScheme(DEFAULT_SCHEME);
			controller.getSpTreeController().expandSchemes();
			controller.getSpTreeController().addContMenu();
			setScene(scene);
		} catch (IOException e) {
			System.err.println("MainStage(String pathXML)");
		}
	}
	
	public static void setScheme(String schemeName) {
		controller.getToolBarController().setTsLastDate(0);
		controller.getToolBarController().updateLabel("");
		
		if (schemeName == null) {
			Main.mainScheme = new Scheme();
		} else {
			Main.mainScheme = new Scheme(ToolsPrLib.getFullPath("./schemes/" + schemeName + ".xml"));

			TreeItem<Scheme> ti = new TreeItem<>(Main.mainScheme);
			controller.getSpTreeController().addScheme(ti);
	        MainStage.schemes.put(Main.mainScheme.getIdScheme(), Main.mainScheme);
		}
		
		Group root = Main.mainScheme.getRoot();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double kx = screenSize.getWidth() * 0.85 / root.getBoundsInLocal().getWidth();
		double ky = screenSize.getHeight() * 0.8 / root.getBoundsInLocal().getHeight();
		root.setScaleX(kx < ky ? kx : ky);
		root.setScaleY(kx < ky ? kx : ky);
		bpScheme.setCenter(Main.mainScheme);
	}
}
