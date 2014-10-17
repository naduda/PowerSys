package controllers;

import java.net.URL;
import java.util.ResourceBundle;

import pr.model.ControlJournalItem;
import ui.Main;
import ui.tables.ControlTableItem;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import controllers.interfaces.IControllerInit;

public class ControlsTableController implements Initializable, IControllerInit {

	@FXML TableView<ControlTableItem> tvControls;
	
	
	private final ObservableList<ControlTableItem> data = FXCollections.observableArrayList();
	private final FilteredList<ControlTableItem> filteredData = new FilteredList<>(data, p -> true);
	private final SortedList<ControlTableItem> sortedData = new SortedList<>(filteredData);
	private final SimpleStringProperty countProperty = new SimpleStringProperty();
	
	@Override
	public void initialize(URL url, ResourceBundle bundle) {
		tvControls.getColumns().forEach(c -> { c.setCellValueFactory(p -> Bindings.selectString(p.getValue(), c.getId())); });
		sortedData.comparatorProperty().bind(tvControls.comparatorProperty());
		tvControls.setItems(sortedData);
		
		setElementText(Main.getResourceBundle());
	}
	
	@Override
	public void setElementText(ResourceBundle rb) {
		tvControls.getColumns().forEach(c -> {
			c.setText(rb.getString("key_" + c.getId()));
		});
	}
	
	public void addConrolRow(ControlJournalItem a) {
		data.add(new ControlTableItem(a));
		countProperty.setValue(data.size() + "");
	}
	
	public void clearTable() {
		data.clear();
	}

	public SimpleStringProperty getCountProperty() {
		return countProperty;
	}
}
