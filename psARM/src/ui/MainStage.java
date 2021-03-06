package ui;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;

import pr.common.Utils;
import controllers.Controller;
import controllers.ToolBarController;
import pr.log.LogFiles;
import pr.model.Alarm;
import pr.model.DvalTI;
import pr.model.TalarmParam;
import pr.model.Tsignal;
import single.ProgramProperty;
import single.SingleFromDB;
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
			ProgramProperty.hightPriorityAlarmProperty.addListener(a -> {
				Alarm hpa = ProgramProperty.hightPriorityAlarmProperty.get();
				if (hpa != null) {
					List<TalarmParam> params;
					params = SingleFromDB.psClient.getTalarmParams(hpa.getAlarmid());
					params.forEach(p -> {
						if ("ALARM_PARAM_SOUND".equals(p.getParamdenom())) {
							SingleObject.alarmActivities.play(p.getParamval());
						}
					});
				}
			});
			
			FXMLLoader loader = new FXMLLoader(new File(Utils.getFullPath("./ui/Main.xml")).toURI().toURL());
			Parent root = loader.load();
			controller = loader.getController();
			controller.getToolBarController().updateLabel(null);
			
			new Thread(() -> {
				String schemeName = null;
				try {
					schemeName = SingleObject.getProgramSettings().getSchemeSettings().getSchemeName();
				} catch (Exception e) {
					LogFiles.log.log(Level.SEVERE, "setScheme(schemeName);", e);
				}
				
				setScheme(schemeName);
			}).start();
			
			Scene scene = new Scene(root);
			setTitle("PowerSys ARM");
			
			controller.getSpTreeController().expandSchemes();
			controller.getSpTreeController().addContMenu();
			setScene(scene);
			
			heightProperty().addListener(e -> {
				controller.getSpTreeController().setHeightTitlePanes(heightProperty().get() / 4);
			});
			
			ProgramProperty.schemeReadyProperty.addListener((observ, old, newValue) -> {
				if (newValue) {
					ProgramProperty.schemeReadyProperty.set(false);
				} else {
					getScene().getRoot().setDisable(false);
					SingleObject.getProgressStage(null).hide();
				}
			});
		} catch (IOException e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	public static void setScheme(String schemeName) {
		if (schemeName == null) {
			SingleObject.mainScheme = new Scheme();
		} else {
			SingleObject.mainScheme = new Scheme(Utils.getFullPath("./schemes/" + schemeName + ".svg"));
			while (MainStage.schemes.get(SingleObject.mainScheme.getIdScheme()) != null) {
				SingleObject.mainScheme.setIdScheme(SingleObject.mainScheme.getIdScheme() + 1);
			}
			TreeItem<Scheme> ti = new TreeItem<>(SingleObject.mainScheme);
			Platform.runLater(() -> controller.getSpTreeController().addScheme(ti));
	        MainStage.schemes.put(SingleObject.mainScheme.getIdScheme(), SingleObject.mainScheme);
		}
		
		setSchemeParams();
		SingleObject.mainStage.getScene().getRoot().setEffect(null);
	}
	
	public static void setSchemeParams() {
		final ExecutorService service = Executors.newFixedThreadPool(4);
		List<Future<Map<Integer, DvalTI>>> futures = new ArrayList<>(2);
		
		if (SingleObject.mainScheme.getIdSignals().size() == 0) return;
		
		final StringBuilder idBuilder = new StringBuilder();
		idBuilder.append("{");
		SingleObject.mainScheme.getIdSignals().forEach(s -> idBuilder.append(s + ","));
		idBuilder.delete(idBuilder.length() - 1, idBuilder.length());
		idBuilder.append("}");
		SingleObject.activeSchemeSignals = idBuilder.toString();
		
		futures.add(service.submit(() -> SingleFromDB.psClient.getOldTI(SingleObject.activeSchemeSignals)));
		futures.add(service.submit(() -> SingleFromDB.psClient.getOldTS(SingleObject.activeSchemeSignals)));
		Future<List<Integer>> notConfirmedSignals = 
				service.submit(() -> SingleFromDB.psClient.getNotConfirmedSignals(SingleObject.activeSchemeSignals));
		Future<Alarm> hightPriorityAlarm = 
				service.submit(() -> SingleFromDB.psClient.getHightPriorityAlarm());
		
		controller.getBpScheme().setCenter(SingleObject.mainScheme);
		
		LogFiles.log.log(Level.INFO, "Start update values");
		
		final Timestamp maxOldDTimestamp = new Timestamp(0);
		futures.forEach(f -> {
			try {
				f.get().values().stream().filter(p -> SingleObject.mainScheme.getIdSignals().contains(p.getSignalref()))
					.forEach(s -> {
						try {
							Tsignal tSignal = SingleFromDB.tsignals.get(s.getSignalref());
							if (tSignal.getTypesignalref() == 1) s.setVal(s.getVal() * tSignal.getKoef());
							controller.updateTI(SingleObject.mainScheme, s);
							
							if (s.getServdt().getTime() > maxOldDTimestamp.getTime()) maxOldDTimestamp.setTime(s.getServdt().getTime());
						} catch (Exception e) {
							LogFiles.log.log(Level.WARNING, "Update oldValues", e);
						}
				});
			} catch (InterruptedException | ExecutionException e) {
				LogFiles.log.log(Level.SEVERE, "Error update old values in MainStage", e);
			}
		});
		controller.getToolBarController().updateLabel(maxOldDTimestamp);
		
		try {
			if (notConfirmedSignals.get() != null) {
				notConfirmedSignals.get().forEach(s -> controller.setNotConfirmed(s, false));
				Alarm hpa = hightPriorityAlarm.get();
				ProgramProperty.hightPriorityAlarmProperty.set(hpa);
			}
		} catch (InterruptedException | ExecutionException e) {
			LogFiles.log.log(Level.SEVERE, "void setScheme(...) Futures ...", e);
		}
		LogFiles.log.log(Level.INFO, "Finish update values");
	}
	
	public static void fitToPage() {
		Group root = SingleObject.mainScheme.getRoot();
		if (controller.getBpScheme().getWidth() > 0) {
			double kx = controller.getBpScheme().getWidth() * 0.99 / root.getBoundsInLocal().getWidth();
			double ky = controller.getBpScheme().getHeight() * 0.99 / root.getBoundsInLocal().getHeight();
			
			ToolBarController.changeZoom(kx < ky ? kx : ky);
		}
	}
}
