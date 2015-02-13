package controllers.tree;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import pr.common.Utils;
import pr.log.LogFiles;
import single.ProgramProperty;
import single.SingleObject;
import ui.MainStage;
import ui.Scheme;
import controllers.Controller;

public class SchemeTreeCellImpl extends TreeCell<Scheme> {
	private static final double IMAGE_SIZE = 16;
    private ContextMenu addMenu = new ContextMenu();
    private final StringProperty localeName = new SimpleStringProperty();
    private MenuItem miClose = new MenuItem();
    		
    public SchemeTreeCellImpl(TreeView<Scheme> tvSchemes, TreeItem<Scheme> trSchemes) {
    	localeName.bind(ProgramProperty.localeName);
    	localeName.addListener((observ, old, value) -> {
    		ResourceBundle rb = Controller.getResourceBundle(new Locale(value));
    		miClose.setText(rb.getString("keyClose"));
    	});
    	
    	try {
			File fImage = new File(Utils.getFullPath("./Icon/close.png"));
			Image image = new Image(fImage.toURI().toURL().toExternalForm(), IMAGE_SIZE, IMAGE_SIZE, true, false);
			miClose.setText(SingleObject.getResourceBundle().getString("keyClose"));
			miClose.setGraphic(new ImageView(image));
			miClose.setOnAction(e -> {
				MainStage.schemes.remove(getItem().getIdScheme());
				SingleObject.mainScheme.getSignalsTI().clear();
				SingleObject.mainScheme.getSignalsTS().clear();
				TreeItem<Scheme> selectedItem = tvSchemes.getSelectionModel().getSelectedItem();
				trSchemes.getChildren().remove(selectedItem);
				if (trSchemes.getChildren().size() == 0) {
					SingleObject.mainScheme = null;
					MainStage.controller.getBpScheme().setCenter(new Scheme());
				} else {
					tvSchemes.getSelectionModel().selectFirst();
				}
			});
			
			addMenu.getItems().add(miClose);
		} catch (MalformedURLException e) {
			LogFiles.log.log(Level.SEVERE, "void SchemeTreeCellImpl()", e);
		}
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
			MainStage.controller.getBpScheme().setCenter(SingleObject.mainScheme);
		}
	}

	private String getString() {
        return getItem() == null ? "" : getItem().toString();
    }
}
