package controllers;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import controllers.interfaces.IControllerInit;
import pr.common.Utils;
import ui.Main;
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
import javafx.util.Callback;

public class TreeController implements Initializable, IControllerInit {

	@FXML private Accordion accordTree;
	@FXML private TreeView<Scheme> tvSchemes;
	@FXML private TitledPane tpSchemes;
	@FXML private TitledPane tpTrends;
	@FXML private TitledPane tpReports;
	@FXML private TreeItem<Scheme> trSchemes;
	
	@Override
	public void initialize(URL url, ResourceBundle boundle) {
		try {
			setElementText(Controller.getResourceBundle(new Locale(Main.getProgramSettings().getLocaleName())));
		} catch (Exception e) {
			setElementText(Controller.getResourceBundle(new Locale("en")));
		}
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
		tvSchemes.setCellFactory(new Callback<TreeView<Scheme>, TreeCell<Scheme>>(){
            @Override
            public TreeCell<Scheme> call(TreeView<Scheme> p) {
                return new SchemeTreeCellImpl();
            }
        });
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
					MainStage.schemes.remove(((Scheme)getItem()).getIdScheme());
					Main.mainScheme.getSignalsTI().clear();
					Main.mainScheme.getSignalsTS().clear();
					TreeItem<Scheme> selectedItem = (TreeItem<Scheme>)tvSchemes.getSelectionModel().getSelectedItem();
					trSchemes.getChildren().remove(selectedItem);
					if (trSchemes.getChildren().size() == 0) {
						Main.mainScheme = null;
						MainStage.bpScheme.setCenter(new Scheme());
					}
				});
				
				addMenu.getItems().add(miClose);
			} catch (MalformedURLException e) {
				System.err.println("Error in SchemeTreeCellImpl - class: TreeController");
			}
        }
        
        @Override
        public void startEdit() {
        	System.out.println("startEdit");
            super.startEdit();
 
            if (scheme == null) {
                //createTextField();
            }
            setText(null);
            setGraphic(scheme);
        }
 
        @Override
        public void cancelEdit() {
        	System.out.println("cancelEdit");
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
				Main.mainScheme = MainStage.schemes.get(((Scheme)getItem()).getIdScheme());
				MainStage.bpScheme.setCenter(Main.mainScheme);
			}
		}

		private String getString() {
            return getItem() == null ? "" : getItem().toString();
        }
	}
}
