package controllers.tree;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.stream.Collectors;

import pr.common.Utils;
import pr.log.LogFiles;
import pr.model.LinkedValue;
import pr.model.Tscheme;
import controllers.ReportPeriodController;
import controllers.interfaces.IControllerInit;
import controllers.interfaces.StageLoader;
import single.SingleFromDB;
import single.SingleObject;
import ui.MainStage;
import ui.Scheme;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class TreeController implements Initializable, IControllerInit {
	private static final int HISTORY_DEEP = 5;
	@FXML private Accordion accordTree;
	@FXML private TreeView<Scheme> tvSchemesLocal;
	@FXML private TreeView<LinkedValue> tvSchemes;
	@FXML private TreeView<LinkedValue> tvReports;
	@FXML private TitledPane tpSchemesLocal;
	@FXML private TitledPane tpSchemes;
	@FXML private TitledPane tpReports;
	@FXML private TreeItem<Scheme> trSchemesLocal;
	@FXML private TreeItem<LinkedValue> trReports;
	@FXML private TreeItem<LinkedValue> trSchemes;
	
	private ContextMenu contextMenu;
	private final List<TreeItem<LinkedValue>> schemeHistory = new ArrayList<>();
	private boolean isPreviusNextButton = false;
	
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
				stage.setResizable(false);
				
				ReportPeriodController controller = (ReportPeriodController) stage.getController();
				controller.setIdReport(item.getValue().getId());
			    stage.show();
			}
		});
		
		getSchemeByParent(trSchemes, 0);
		tvSchemes.getSelectionModel().selectedItemProperty().addListener((observ, old, newValue) -> {
			SingleObject.mainStage.getScene().getRoot().setDisable(true);
			new Thread(() -> {
				try {
					Platform.runLater(() -> SingleObject.getProgressStage(newValue.getValue().getVal().toString() + " ...").show());
					SingleObject.mainScheme = new Scheme((Tscheme)newValue.getValue().getVal());
					
					Platform.runLater(() -> {
						MainStage.controller.getBpScheme().setCenter(SingleObject.mainScheme);
						MainStage.setSchemeParams();
						MainStage.fitToPage();
						
						if (!isPreviusNextButton()) {
							schemeHistory.add(old);
							schemeHistory.remove(old);
							schemeHistory.remove(newValue);
							schemeHistory.add(newValue);
							if (schemeHistory.size() > HISTORY_DEEP) {
								schemeHistory.remove(0);
							}
							if (schemeHistory.size() > 1) {
								MainStage.controller.getToolBarController().getPrevious().getStyleClass().remove("previous_gray");
								MainStage.controller.getToolBarController().getPrevious().getStyleClass().add("previous");
							}
						}
						setPreviusNextButton(false);
					});
				} catch (Exception e) {
					LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
				} finally {
					Platform.runLater(() -> {
						SingleObject.mainStage.getScene().getRoot().setDisable(false);
						SingleObject.getProgressStage(null).hide();
					});
				}
			}).start();
		});
		
		setContxtMenu();
		tvSchemes.setContextMenu(contextMenu);
		
		setElementText(SingleObject.getResourceBundle());
	}
	
	private void setContxtMenu() {
		try {
			FXMLLoader loader = new FXMLLoader(new File(Utils.getFullPath("./ui/tree/TreeContextMenu.xml")).toURI().toURL());
			contextMenu = loader.load();
			SchemeController schemeController = loader.getController();
			
			contextMenu.setOnShowing(e -> schemeController.setElementText(SingleObject.getResourceBundle()));
		} catch (IOException e) {
			LogFiles.log.log(Level.SEVERE, "void setContextMenu()", e);
		}
	}
	
	private void getSchemeByParent(TreeItem<LinkedValue> node, int parent) {
		List<Tscheme> nodes = SingleFromDB.schemesMap.values().stream()
				.filter(f -> f.getParentref() == parent).collect(Collectors.toList());
		if (nodes.size() > 0) {
			nodes.forEach(n -> {
				TreeItem<LinkedValue> chNode = new TreeItem<LinkedValue>(new LinkedValue(n.getIdscheme(), n, null));
				if (n.getIdscheme() == SingleObject.getProgramSettings().getSchemeSettings().getIdScheme()) {
					Platform.runLater(() -> tvSchemes.getSelectionModel().select(chNode));
				}
				node.getChildren().add(chNode);
				getSchemeByParent(chNode, n.getIdscheme());
			});
		}
	}
	
	public void setHeightTitlePanes(double height) {
		Platform.runLater(() -> {
			tpSchemesLocal.setPrefHeight(height);
			tpSchemesLocal.setMaxHeight(height);
			tpSchemes.setPrefHeight(height);
			tpSchemes.setMaxHeight(height);
			tpReports.setPrefHeight(height);
			tpReports.setMaxHeight(height);
		});
	}
	
	@Override
	public void setElementText(ResourceBundle rb) {
		tpSchemesLocal.setText(rb.getString("keySchemes"));
		tpSchemes.setText(rb.getString("keySchemesDB"));
		tpReports.setText(rb.getString("keyReports"));
	}
	
	public void addScheme(TreeItem<Scheme> ti) {
		trSchemesLocal.getChildren().add(ti);
		tvSchemesLocal.getSelectionModel().select(ti);
	}
	
	public void expandSchemes() {
		accordTree.setExpandedPane(tpSchemes);
	}
	
	final ContextMenu rootContextMenu = new ContextMenu();
	
	public void addContMenu() {
		tvSchemesLocal.setCellFactory(p -> new SchemeTreeCellImpl(tvSchemesLocal, trSchemesLocal));
	}

	public TreeView<LinkedValue> getTvSchemes() {
		return tvSchemes;
	}

	public List<TreeItem<LinkedValue>> getSchemeHistory() {
		return schemeHistory;
	}

	public boolean isPreviusNextButton() {
		return isPreviusNextButton;
	}

	public void setPreviusNextButton(boolean isPreviusNextButton) {
		this.isPreviusNextButton = isPreviusNextButton;
	}
}
