package controllers;

import java.net.URL;
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
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import javafx.util.Callback;

public class TransparantController implements Initializable {
	private static final double TRANSPARANT_SIZE = 40;
	
	@FXML ListView<Transparant> lvTransparants;
	
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
		Transparant selectedTransp = lvTransparants.getSelectionModel().getSelectedItem();
		Shape transparant = createCircle(selectedTransp.getIdtr());
		Main.mainScheme.getRoot().getChildren().add(transparant);
		
		((Stage)lvTransparants.getScene().getWindow()).close();
	}
	
	private Shape createCircle(int idTransarant) {
		Bounds shBounds = Scheme.selectedShape.getBoundsInParent();
		Point2D mp = new Point2D(shBounds.getMinX(), shBounds.getMinY());
		
		Circle n = new Circle(mp.getX(), mp.getY(), TRANSPARANT_SIZE / 2);
		n.setStroke(Color.TRANSPARENT);
		n.setFill(new ImagePattern(MainStage.imageMap.get(idTransarant)));
		
        n.setOnMouseDragged(event -> {
        	Point2D p = Main.mainScheme.getRoot().sceneToLocal(event.getSceneX(), event.getSceneY());
        	
        	double x = p.getX();
        	double y = p.getY();
        	double maxX = MainStage.bpScheme.getWidth();
        	double maxY = MainStage.bpScheme.getHeight();
        	double r = n.getRadius();
        	
            n.relocate(x < 2 * r ? r : x + r > maxX ? maxX - r : x - r, 
            		   y < 2 * r ? r : y + r > maxY ? maxY - r : y - r);
        });

        return n;
    }
}