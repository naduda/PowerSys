package controllers.tree;

import java.net.URL;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import pr.model.LinkedValue;
import controllers.Controller;
import controllers.ReportPeriodController;
import controllers.interfaces.IControllerInit;
import controllers.interfaces.StageLoader;
import single.SingleFromDB;
import single.SingleObject;
import ui.Scheme;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class TreeController implements Initializable, IControllerInit {
	@FXML private Accordion accordTree;
	@FXML private TreeView<Scheme> tvSchemes;
	@FXML private TreeView<LinkedValue> tvReports;
	@FXML private TitledPane tpSchemes;
	@FXML private TitledPane tpTrends;
	@FXML private TitledPane tpReports;
	@FXML private TreeItem<Scheme> trSchemes;
	@FXML private TreeItem<LinkedValue> trReports;
	
	@Override
	public void initialize(URL url, ResourceBundle boundle) {
		Map<Integer, String> reports = SingleFromDB.psClient.getReports();
		reports.keySet().forEach(k -> {
			LinkedValue lv = new LinkedValue(k, reports.get(k), null);
			TreeItem<LinkedValue> ti = new TreeItem<LinkedValue>();
			ti.setValue(lv);
			trReports.getChildren().add(ti);
		});
		
		tvReports.setOnMouseClicked(e -> {
			if (e.getClickCount() == 2) {
				TreeItem<LinkedValue> item = tvReports.getSelectionModel().getSelectedItem();
				
				StageLoader stage = new StageLoader("ReportPeriod.xml", 
						SingleObject.getResourceBundle().getString("keyPeriod"), true);
				
				ReportPeriodController controller = (ReportPeriodController) stage.getController();
				controller.setIdReport(item.getValue().getId());
			    stage.show();		
			}
		});
		
		setElementText(Controller.getResourceBundle(new Locale(SingleObject.getProgramSettings().getLocaleName())));
	}
	
	public void setHeightTitlePanes(double height) {
		Platform.runLater(() -> {
			tpSchemes.setPrefHeight(height);
			tpSchemes.setMaxHeight(height);
			tpTrends.setPrefHeight(height);
			tpTrends.setMaxHeight(height);
			tpReports.setPrefHeight(height);
			tpReports.setMaxHeight(height);
		});
	}
	
	@Override
	public void setElementText(ResourceBundle rb) {
		tpSchemes.setText(rb.getString("keySchemes"));
		tpTrends.setText(rb.getString("key_menuTrend"));
		tpReports.setText(rb.getString("keyReports"));
	}
	
	public void addScheme(TreeItem<Scheme> ti) {
		trSchemes.getChildren().add(ti);
		tvSchemes.getSelectionModel().select(ti);
	}
	
	public void expandSchemes() {
		accordTree.setExpandedPane(tpSchemes);
	}
	
	final ContextMenu rootContextMenu = new ContextMenu();
	
	public void addContMenu() {
		tvSchemes.setCellFactory(p -> new SchemeTreeCellImpl(tvSchemes, trSchemes));
	}
}
