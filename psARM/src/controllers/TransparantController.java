package controllers;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import pr.model.Transparant;
import ui.Main;
import ui.MainStage;
import ui.Scheme;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Callback;

public class TransparantController implements Initializable {
	@FXML ListView<Transparant> lvTransparants;
	@FXML TextArea txtArea;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		List<Transparant> transpList = new ArrayList<Transparant>(MainStage.transpMap.values());
		ObservableList<Transparant> items = FXCollections.observableArrayList(transpList);
		
		lvTransparants.setItems(items);
		
		lvTransparants.setCellFactory(new Callback<ListView<Transparant>, ListCell<Transparant>>() {
			@Override
                public ListCell<Transparant> call(ListView<Transparant> list) {
                    return new CellStyle();
                }
            }
        );
		
		lvTransparants.setOnMouseReleased(e -> {
			if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
				addTransparant();
			}
		});
	}
	
	private class CellStyle extends ListCell<Transparant> {
        private static final double IMAGE_SIZE = 30;

		@Override
        public void updateItem(Transparant item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
	            Rectangle rect = new Rectangle(IMAGE_SIZE, IMAGE_SIZE);
	            if (item != null) {
	                rect.setFill(new ImagePattern(MainStage.imageMap.get(item.getIdtr())));
	                setGraphic(rect);
	                setText(item.getDescr());
	            }
            }
        }
    }	
	
	@FXML 
	protected void btnCancel(ActionEvent event) {
		((Stage)((Button)event.getSource()).getScene().getWindow()).close();
	}
	
	@FXML 
	protected void btnOK(ActionEvent event) {
		addTransparant();
	}
	
	private void addTransparant() {
		Bounds shBounds = Scheme.selectedShape.getBoundsInParent();
		Point2D mp = new Point2D(shBounds.getMinX(), shBounds.getMinY());
		
		Transparant selectedTransp = lvTransparants.getSelectionModel().getSelectedItem();
		try {
			int idtr = MainStage.psClient.getMaxTranspID();
			MainStage.psClient.insertTtransparant(idtr, 0, "", selectedTransp.getIdtr(), Main.mainScheme.getIdScheme());
			MainStage.psClient.insertTtranspHistory(idtr, -1, txtArea.getText(), 0);
			
			Thread.sleep(1000);
			
			MainStage.psClient.deleteTtranspLocate(idtr, Main.mainScheme.getIdScheme());
			MainStage.psClient.insertTtranspLocate(idtr, Main.mainScheme.getIdScheme(), (int)mp.getX(), (int)mp.getY(), 43, 43);
		} catch (RemoteException | InterruptedException e) {
			e.printStackTrace();
		}
		((Stage)lvTransparants.getScene().getWindow()).close();
	}
}
