package controllers;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;

import controllers.interfaces.IControllerInit;
import pr.common.Utils;
import pr.log.LogFiles;
import single.SingleObject;
import ui.MainStage;
import ui.Scheme;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class TreeController implements Initializable, IControllerInit {

	@FXML private Accordion accordTree;
	@FXML private TreeView<Scheme> tvSchemes;
	@FXML private TitledPane tpSchemes;
	@FXML private TitledPane tpTrends;
	@FXML private TitledPane tpReports;
	@FXML private TreeItem<Scheme> trSchemes;
	
	@Override
	public void initialize(URL url, ResourceBundle boundle) {
		setElementText(Controller.getResourceBundle(new Locale(SingleObject.getProgramSettings().getLocaleName())));
	}
	
	@Override
	public void setElementText(ResourceBundle rb) {
		tpSchemes.setText(rb.getString("keySchemes"));
		tpTrends.setText(rb.getString("keyTrend"));
		tpReports.setText(rb.getString("keyReports"));
	}
	
	public void addScheme(TreeItem<Scheme> ti) {
		trSchemes.getChildren().add(ti);
	}
	
	public void expandSchemes() {
		accordTree.setExpandedPane(tpSchemes);
	}
	
	final ContextMenu rootContextMenu = new ContextMenu();
	
	public void addContMenu() {
		tvSchemes.setCellFactory(p -> new SchemeTreeCellImpl());
	}
	
	private final class SchemeTreeCellImpl extends TreeCell<Scheme> {
		private static final double IMAGE_SIZE = 16;
		private Scheme scheme;
        private ContextMenu addMenu = new ContextMenu();
 
        public SchemeTreeCellImpl() {
        	try {
				File fImage = new File(Utils.getFullPath("./Icon/close.png"));
				Image image = new Image(fImage.toURI().toURL().toExternalForm(), IMAGE_SIZE, IMAGE_SIZE, true, false);
				MenuItem miClose = new MenuItem("Закрити", new ImageView(image));
				miClose.setOnAction(e -> {
					MainStage.schemes.remove(getItem().getIdScheme());
					SingleObject.mainScheme.getSignalsTI().clear();
					SingleObject.mainScheme.getSignalsTS().clear();
					TreeItem<Scheme> selectedItem = tvSchemes.getSelectionModel().getSelectedItem();
					trSchemes.getChildren().remove(selectedItem);
					if (trSchemes.getChildren().size() == 0) {
						SingleObject.mainScheme = null;
						MainStage.bpScheme.setCenter(new Scheme());
					}
				});
				
				addMenu.getItems().add(miClose);
			} catch (MalformedURLException e) {
				LogFiles.log.log(Level.SEVERE, "void SchemeTreeCellImpl()", e);
			}
        }
        
        @Override
        public void startEdit() {
            super.startEdit();

            setText(null);
            setGraphic(scheme);
        }
 
        @Override
        public void cancelEdit() {
            super.cancelEdit();
            setGraphic(getTreeItem().getGraphic());
        }
 
        @Override
        public void updateItem(Scheme item, boolean empty) {
            super.updateItem(item, empty);
 
            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
            	setText(getString());
                setGraphic(getTreeItem().getGraphic());
                setContextMenu(addMenu);
            }
        }

        @Override
		public void updateSelected(boolean selected) {
			super.updateSelected(selected);
			if (selected) {
				SingleObject.mainScheme = MainStage.schemes.get(getItem().getIdScheme());
				MainStage.bpScheme.setCenter(SingleObject.mainScheme);
			}
		}

		private String getString() {
            return getItem() == null ? "" : getItem().toString();
        }
	}
}
