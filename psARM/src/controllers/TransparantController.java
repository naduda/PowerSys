package controllers;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;

import controllers.interfaces.IControllerInit;
import pr.log.LogFiles;
import pr.model.Transparant;
import single.SingleFromDB;
import single.SingleObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

public class TransparantController implements Initializable, IControllerInit {
	@FXML private ListView<Transparant> lvTransparants;
	@FXML private TextArea txtArea;
	@FXML private Button btnOK;
	@FXML private Button btnCancel;
	
	@FXML private Label lListTransp;
	@FXML private Label lReason;
	@FXML private Label lImportance;
	
	private boolean edit = false;
	private int trref;
	private Shape transparant;
	
	@Override
	public void initialize(URL url, ResourceBundle bundle) {
		List<Transparant> transpList = new ArrayList<>(SingleFromDB.transpMap.values());
		ObservableList<Transparant> items = FXCollections.observableArrayList(transpList);
		
		lvTransparants.setItems(items);		
		lvTransparants.setCellFactory(list -> new CellStyle());
		
		lvTransparants.setOnMouseReleased(e -> {
			if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
				addTransparant();
			}
		});
	}
	
	@Override
	public void setElementText(ResourceBundle rb) {
		lListTransp.setText(rb.getString("keyListTransp"));
		lReason.setText(rb.getString("keyReason"));
		lImportance.setText(rb.getString("keyImportance"));
		btnOK.setText(rb.getString("keySet"));
		btnCancel.setText(rb.getString("keyCancel"));
	}
	
	private class CellStyle extends ListCell<Transparant> {
        private static final double IMAGE_SIZE = 30;

		@Override
        public void updateItem(Transparant item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
	            Rectangle rect = new Rectangle(IMAGE_SIZE, IMAGE_SIZE);
	            Image image = new Image(new ByteArrayInputStream(item.getImageByteArray()));
				rect.setFill(new ImagePattern(image));
				setGraphic(rect);
				setText(item.getDescr());
			}
        }
    }
	
	@FXML 
	protected void btnCancel(ActionEvent event) {
		closeWindowTransparant(event);
	}
	
	private void closeWindowTransparant (ActionEvent event) {
		((Stage)((Button)event.getSource()).getScene().getWindow()).close();
	}
	
	@FXML 
	protected void btnOK(ActionEvent event) {
		if (edit) {
			updateTransparant();
		} else {
			addTransparant();
		}
		closeWindowTransparant(event);
	}
	
	private void updateTransparant() {
		Circle c = (Circle) transparant;
		double r = c.getRadius();
		
		Group root = SingleObject.mainScheme.getRoot();
		double maxX = root.getBoundsInLocal().getMaxX();
		double maxY = root.getBoundsInLocal().getMaxY();
		Bounds bounds = c.boundsInParentProperty().getValue();
    	double x = bounds.getMinX() + r;
    	x = x < r ? r : x - r > maxX ? maxX - r : x - r;
    	double y = bounds.getMinY() + r;
    	y = y < r ? r : y - r > maxY ? maxY - r : y - r;
    	
		try {
			SingleFromDB.psClient.updateTtranspLocate(trref, SingleObject.mainScheme.getIdScheme(), 
					(int)x, (int)y, (int)r * 2, (int)r * 2);
			
			SingleFromDB.psClient.updateTtransparantLastUpdate(trref);
			SingleFromDB.psClient.updateTtranspHistory(trref, txtArea.getText());
		} catch (RemoteException e) {
			LogFiles.log.log(Level.SEVERE, "void updateTransparant()", e);
		}
	}
	
	private void addTransparant() {
		Bounds shBounds = SingleObject.selectedShape.getBoundsInParent();
		Point2D mp = new Point2D(shBounds.getMinX(), shBounds.getMinY());
		
		Transparant selectedTransp = lvTransparants.getSelectionModel().getSelectedItem();
		try {
			int idtr = SingleFromDB.psClient.getMaxTranspID();
			SingleFromDB.psClient.insertTtransparant(idtr, 0, "", selectedTransp.getIdtr(), SingleObject.mainScheme.getIdScheme());
			SingleFromDB.psClient.insertTtranspHistory(idtr, -1, txtArea.getText(), 0);
			
			Thread.sleep(1000);
			
			SingleFromDB.psClient.deleteTtranspLocate(idtr, SingleObject.mainScheme.getIdScheme());
			SingleFromDB.psClient.insertTtranspLocate(idtr, SingleObject.mainScheme.getIdScheme(), (int)shBounds.getMaxX(), (int)mp.getY(), 43, 43);
		} catch (RemoteException | InterruptedException e) {
			LogFiles.log.log(Level.SEVERE, "void addTransparant()", e);
		}
	}

	public void setTxtArea(String text) {
		txtArea.setText(text);
	}

	public void setEdit(boolean edit) {
		this.edit = edit;
	}

	public void setTrref(int trref) {
		this.trref = trref;
	}

	public void setTransparant(Shape transparant) {
		this.transparant = transparant;
	}
	
	public void setOKtext(String text) {
		btnOK.setText(text);
	}
	
	public void selectTransparantById(int id) {
		lvTransparants.getSelectionModel().select(id);
	}
	
	public void disableListView() {
		lvTransparants.setDisable(true);
	}
}
