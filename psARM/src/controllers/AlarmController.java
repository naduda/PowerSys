package controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import model.Alarm;
import model.TSysParam;
import model.TViewParam;
import ui.Main;
import ui.Scheme;
import ui.alarm.AlarmTableItem;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.util.Callback;

public class AlarmController implements Initializable{

	private List<TViewParam> viewParams;
	private final Map<String, TSysParam> sysParams = Main.pdb.getTSysParamMap("ALARM_PRIORITY");
	
	private final ObservableList<AlarmTableItem> data = FXCollections.observableArrayList();	
	private final FilteredList<AlarmTableItem> filteredData = new FilteredList<>(data, p -> true);
	private final SortedList<AlarmTableItem> sortedData = new SortedList<>(filteredData);
	
	@FXML TableView<AlarmTableItem> tvAlarms;
	@FXML ChoiceBox<String> cbPriority;
	
	@FXML
	private void kvitOne(ActionEvent event) {
		System.out.println("kvitOne");
	}
	
	@FXML
	private void kvitPS(ActionEvent event) {
		System.out.println("kvitPS");
	}
	
	@FXML
	private void kvitAll(ActionEvent event) {
		System.out.println("kvitAll");
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.out.println("initialize AlarmController " + tvAlarms);
		
		List<String> list = new ArrayList<>();
		sysParams.values().forEach(it -> {list.add(it.getParamdescr());});
		ObservableList<String> obList = FXCollections.observableList(list);

		cbPriority.getItems().addAll(obList);
		cbPriority.setValue(cbPriority.getItems().get(0));

		ObservableList<TableColumn<AlarmTableItem, ?>> tColumns = tvAlarms.getColumns();
		for (TableColumn<AlarmTableItem, ?> tableColumn : tColumns) {
			tableColumn.setCellValueFactory(p -> Bindings.selectString(p.getValue(), tableColumn.getId()));
		}
		
		viewParams = Main.pdb.getTViewParam("LOG_STATE", "COLOR", -1);
		tvAlarms.setRowFactory(new Callback<TableView<AlarmTableItem>, TableRow<AlarmTableItem>>() {			
			@Override
			public TableRow<AlarmTableItem> call(TableView<AlarmTableItem> param) {
				final TableRow<AlarmTableItem> row = new TableRow<AlarmTableItem>() {
	                @Override
	                protected void updateItem(AlarmTableItem alarm, boolean empty){
	                    super.updateItem(alarm, empty);
	                    String cellStyle = "-fx-control-inner-background: %s;"
	                    				 + "-fx-accent: derive(-fx-control-inner-background, -40%%);"
	                    				 + "-fx-cell-hover-color: derive(-fx-control-inner-background, -20%%);";
	                    if (alarm != null && alarm.getPConfirmDT().equals("")) {
	                    	
	                    	String col = viewParams.stream().filter(sp -> sp.getAlarmref() == alarm.getAlarmid()).
	                    			filter(sp -> Integer.parseInt(sp.getObjref()) == alarm.getLogState()).
	                    			collect(Collectors.toList()).get(0).getParamval();
	                    	col = Scheme.getColor(col).toString().substring(0, 8).replace("0x", "#");
	                    	cellStyle = String.format(cellStyle, col);	                    	
	                    } else {
	                    	cellStyle = String.format(cellStyle, "white");
	                    }
	                    setStyle(cellStyle);
	                }
	            };
	            return row;
			}
		});
		
//      Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(tvAlarms.comparatorProperty());
        tvAlarms.setItems(sortedData);
	}
	
	public void addAlarm(Alarm a) {
		data.add(new AlarmTableItem(a));
	}
	
	public void updateAlarm(Alarm a) {
		try {
			AlarmTableItem it = data.filtered(item -> item.getEventDT().equals(a.getEventdt())).get(0);
			data.remove(it);
			addAlarm(a);
		} catch (Exception e) {
			System.out.println("No " + a.getEventdt());
		}
	}
}
