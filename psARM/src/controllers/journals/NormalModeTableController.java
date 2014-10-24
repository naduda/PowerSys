package controllers.journals;

import java.net.URL;
import java.util.ResourceBundle;

import ui.Main;
import ui.tables.NormalModeTableItem;
import controllers.interfaces.IControllerInit;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;

public class NormalModeTableController implements Initializable, IControllerInit {

	private final SimpleStringProperty countProperty = new SimpleStringProperty();
	
	@FXML TableView<NormalModeTableItem> tvControls;
	
	private final ObservableList<NormalModeTableItem> data = FXCollections.observableArrayList();
	private final FilteredList<NormalModeTableItem> filteredData = new FilteredList<>(data, p -> true);
	private final SortedList<NormalModeTableItem> sortedData = new SortedList<>(filteredData);
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		tvControls.getColumns().forEach(c -> { c.setCellValueFactory(p -> Bindings.selectString(p.getValue(), c.getId())); });
		sortedData.comparatorProperty().bind(tvControls.comparatorProperty());
		tvControls.setItems(sortedData);
		
		setElementText(Main.getResourceBundle());
	}
	
	public SimpleStringProperty getCountProperty() {
		return countProperty;
	}

	@Override
	public void setElementText(ResourceBundle rb) {
		tvControls.getColumns().forEach(c -> {
			c.setText(rb.getString("key_" + c.getId()));
		});
	}
	
}
