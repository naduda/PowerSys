package ui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pr.common.Utils;
import pr.common.WMF2PNG;
import pr.model.ConfTree;
import pr.model.SpTuCommand;
import pr.model.Transparant;
import pr.model.Tsignal;
import pr.model.Tuser;
import controllers.Controller;
import topic.ClientPowerSys;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainStage extends Stage implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final ClientPowerSys psClient = new ClientPowerSys();
	public static final Map<Integer, Tsignal> signals = psClient.getTsignalsMap();
	public static final Map<Integer, Tuser> users = psClient.getTuserMap();
	public static ListView<String> lvTree;
	public static BorderPane bpScheme;
	public static Map<Integer, Scheme> schemes = new HashMap<>();
	public static Controller controller;
	public static final Map<Integer, Transparant> transpMap = psClient.getTransparants();
	public static final List<SpTuCommand> spTuCommands = psClient.getSpTuCommand();
	public static final Map<Integer, Image> imageMap = getImageMap();
	
	public MainStage(String pathXML) {
		try {
			FXMLLoader loader = new FXMLLoader(new URL("file:/" + Utils.getFullPath("./ui/Main.xml")));
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
			
			try {
				String schemeName = Main.getProgramSettings().getSchemeSettings().getSchemeName();
				setScheme(schemeName);
			} catch (Exception e) {
				setScheme(null);
				e.printStackTrace();
			}
			
			controller.getSpTreeController().expandSchemes();
			controller.getSpTreeController().addContMenu();
			setScene(scene);
		} catch (IOException e) {
			System.err.println("MainStage(String pathXML)");
			e.printStackTrace();
		}
	}
	
	public static void setScheme(String schemeName) {
		controller.getToolBarController().updateLabel("");
		
		if (schemeName == null) {
			Main.mainScheme = new Scheme();
		} else {
			Main.mainScheme = new Scheme(Utils.getFullPath("./schemes/" + schemeName + ".svg"));

			TreeItem<Scheme> ti = new TreeItem<>(Main.mainScheme);
			controller.getSpTreeController().addScheme(ti);
	        MainStage.schemes.put(Main.mainScheme.getIdScheme(), Main.mainScheme);
		}
		
		Group root = (Group) Main.mainScheme.getRoot();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double kx = screenSize.getWidth() * 0.85 / root.getBoundsInLocal().getWidth();
		double ky = screenSize.getHeight() * 0.8 / root.getBoundsInLocal().getHeight();
		root.setScaleX(kx < ky ? kx : ky);
		root.setScaleY(kx < ky ? kx : ky);
		bpScheme.setCenter(Main.mainScheme);
	}
	
	private static Map<Integer, Image> getImageMap() {
		Map<Integer, Image> ret = new HashMap<>();
		
		transpMap.forEach((key, transp) -> {
			byte[] bytes = (byte[]) transpMap.get(key).getImg();
			InputStream is = WMF2PNG.convert(new ByteArrayInputStream(bytes), 250);
			ret.put(key, new Image(is));
		});
		return ret;
	}
}
