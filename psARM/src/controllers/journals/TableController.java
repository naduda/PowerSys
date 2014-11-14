package controllers.journals;

import java.net.URL;
import java.util.ResourceBundle;

import controllers.interfaces.IControllerInit;
import single.SingleObject;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;

public class TableController implements Initializable, IControllerInit {
	private final SimpleStringProperty countProperty = new SimpleStringProperty();
	
	public final ObservableList<Object> data = FXCollections.observableArrayList();
	public final FilteredList<Object> filteredData = new FilteredList<>(data, p -> true);
	public final SortedList<Object> sortedData = new SortedList<>(filteredData);
	
	@FXML public TableView<Object> tvTable;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		tvTable.getColumns().forEach(c -> c.setCellValueFactory(p -> Bindings.selectString(p.getValue(), c.getId())));
		sortedData.comparatorProperty().bind(tvTable.comparatorProperty());
		tvTable.setItems(sortedData);
		
		setElementText(SingleObject.getResourceBundle());
	}
	
	@Override
	public void setElementText(ResourceBundle rb) {
		tvTable.getColumns().forEach(c -> c.setText(rb.getString("key_" + c.getId())));
	}
	
	public SimpleStringProperty getCountProperty() {
		return countProperty;
	}
	
	public void clearTable() {
		data.clear();
		countProperty.setValue(data.size() + "");
	}
	
	public void addItem(Object e) {
		data.add(e);
		countProperty.setValue(data.size() + "");
	}
}
