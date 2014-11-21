package ui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import pr.common.Utils;
import controllers.Controller;
import controllers.ToolBarController;
import pr.log.LogFiles;
import single.SingleObject;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.stage.Stage;

public class MainStage extends Stage implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static Map<Integer, Scheme> schemes = new HashMap<>();
	public static Controller controller;	
	
	public MainStage(String pathXML) {
		try {
			FXMLLoader loader = new FXMLLoader(new URL("file:/" + Utils.getFullPath("./ui/Main.xml")));
			
			new Thread(() -> {
				String schemeName = null;
				try {
					schemeName = SingleObject.getProgramSettings().getSchemeSettings().getSchemeName();
				} catch (Exception e) {
					LogFiles.log.log(Level.SEVERE, "setScheme(schemeName);", e);
				}
				
				setScheme(schemeName);
			}).start();
			
			Parent root = loader.load();
			controller = loader.getController();
			
			Scene scene = new Scene(root);
			setTitle("PowerSys ARM");
			
			controller.getSpTreeController().expandSchemes();
			controller.getSpTreeController().addContMenu();
			setScene(scene);
		} catch (IOException e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	public static void setScheme(String schemeName) {
		if (schemeName == null) {
			SingleObject.mainScheme = new Scheme();
		} else {
			SingleObject.mainScheme = new Scheme(Utils.getFullPath("./schemes/" + schemeName + ".svg"));

			TreeItem<Scheme> ti = new TreeItem<>(SingleObject.mainScheme);
			controller.getSpTreeController().addScheme(ti);
	        MainStage.schemes.put(SingleObject.mainScheme.getIdScheme(), SingleObject.mainScheme);
		}
		
		while (controller == null) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		Group root = SingleObject.mainScheme.getRoot();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double kx = screenSize.getWidth() * 0.85 / root.getBoundsInLocal().getWidth();
		double ky = screenSize.getHeight() * 0.8 / root.getBoundsInLocal().getHeight();
		root.setScaleX(kx < ky ? kx : ky);
		root.setScaleY(kx < ky ? kx : ky);
		
		controller.getToolBarController().updateLabel("");
		controller.getBpScheme().setCenter(SingleObject.mainScheme);
		Platform.runLater(() -> ToolBarController.zoomProperty.set(root.getScaleX()));
	}
}
