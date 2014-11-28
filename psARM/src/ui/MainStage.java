package ui;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;

import pr.common.Utils;
import controllers.Controller;
import controllers.ToolBarController;
import pr.log.LogFiles;
import pr.model.DvalTI;
import pr.model.Tsignal;
import single.SingleFromDB;
import single.SingleObject;
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
			
			heightProperty().addListener(e -> {
				controller.getSpTreeController().setHeightTitlePanes(heightProperty().get() / 4);
			});
		} catch (IOException e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	public static void setScheme(String schemeName) {
		final ExecutorService service = Executors.newFixedThreadPool(2);
		List<Future<Map<Integer, DvalTI>>> futures = new ArrayList<>();
		
		if (schemeName == null) {
			SingleObject.mainScheme = new Scheme();
		} else {
			SingleObject.mainScheme = new Scheme(Utils.getFullPath("./schemes/" + schemeName + ".svg"));

			TreeItem<Scheme> ti = new TreeItem<>(SingleObject.mainScheme);
			controller.getSpTreeController().addScheme(ti);
	        MainStage.schemes.put(SingleObject.mainScheme.getIdScheme(), SingleObject.mainScheme);
		}
		
		final StringBuilder idBuilder = new StringBuilder();
		idBuilder.append("{");
		SingleObject.mainScheme.getIdSignals().forEach(s -> idBuilder.append(s + ","));
		idBuilder.delete(idBuilder.length() - 1, idBuilder.length());
		idBuilder.append("}");
		
		futures.add(service.submit(() -> (Map<Integer, DvalTI>) SingleFromDB.psClient.getOldTI(idBuilder.toString())));
		futures.add(service.submit(() -> (Map<Integer, DvalTI>) SingleFromDB.psClient.getOldTS(idBuilder.toString())));
		
		controller.getToolBarController().updateLabel(null);
		controller.getBpScheme().setCenter(SingleObject.mainScheme);
		
		Group root = SingleObject.mainScheme.getRoot();
		
		if (MainStage.controller.getBpScheme().getWidth() > 0) {
			double kx = MainStage.controller.getBpScheme().getWidth() * 0.99 / root.getBoundsInLocal().getWidth();
			double ky = MainStage.controller.getBpScheme().getHeight() * 0.99 / root.getBoundsInLocal().getHeight();
			
			root.setScaleX(kx < ky ? kx : ky);
			root.setScaleY(kx < ky ? kx : ky);
			
			ToolBarController.zoomProperty.set(root.getScaleX());
		}
		
		LogFiles.log.log(Level.INFO, "Start update values");
		futures.forEach(f -> {
			try {
				f.get().values().stream().filter(p -> SingleObject.mainScheme.getIdSignals().contains(p.getSignalref()))
					.forEach(s -> {
						Tsignal tSignal = SingleFromDB.tsignals.get(s.getSignalref());
						if (tSignal.getTypesignalref() == 1) s.setVal(s.getVal() * tSignal.getKoef());
						MainStage.controller.updateTI(SingleObject.mainScheme, s);
				});
			} catch (Exception e) {
				LogFiles.log.log(Level.SEVERE, "Error update old values in MainStage", e);
			}
		});
		LogFiles.log.log(Level.INFO, "Finish update values");
	}
}
