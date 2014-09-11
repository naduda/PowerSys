package controllers;

import java.net.URL;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import model.Alarm;
import model.TSysParam;
import model.TViewParam;
import ui.MainStage;
import ui.Scheme;
import ui.alarm.AlarmTableItem;
import javafx.animation.PauseTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.SortType;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import javafx.util.Duration;

public class AlarmController implements Initializable {

	private static final double MOUSE_DURATION_MILLS = 250;
	private List<TViewParam> viewParams;
	private Map<String, TSysParam> sysParams;
	
	private final ObservableList<AlarmTableItem> data = FXCollections.observableArrayList();	
	private final FilteredList<AlarmTableItem> filteredData = new FilteredList<>(data, p -> true);
	private final SortedList<AlarmTableItem> sortedData = new SortedList<>(filteredData);
    private TableColumn<AlarmTableItem, String> sortColumnState = null;
    private TableColumn<AlarmTableItem, String> sortColumnPriority = null;
    private TableColumn<AlarmTableItem, String> sortColumnEventDT = null;
	
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
	
	@FXML
	private void filterColumnClick(ActionEvent event) {
		System.out.println("filterColumnClick");
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			sysParams = MainStage.psClient.getTSysParam("ALARM_PRIORITY");
			viewParams = MainStage.psClient.getTViewParam("LOG_STATE", "COLOR", -1);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		List<String> list = new ArrayList<>();
		sysParams.values().forEach(it -> {list.add(it.getParamdescr());});
		ObservableList<String> obList = FXCollections.observableList(list);

		cbPriority.getItems().addAll(obList);
		cbPriority.setValue(cbPriority.getItems().get(0));
		cbPriority.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(a -> {
            	if (cbPriority.getItems().indexOf(newValue) == 0) { //Priority = Any
            		return true;
            	} else {
            		String lowerCaseFilter = newValue.toString().toLowerCase();
            		if (a.getPAlarmPriority().toLowerCase().equals(lowerCaseFilter)) {
            			return true;
            		} else {
            			return false;
            		}
            	}
            });
        });

		tvAlarms.getColumns().forEach(c -> {
			c.setCellValueFactory(p -> Bindings.selectString(p.getValue(), c.getId()));
			if (c.getId().equals("pLogState")) {
				sortColumnState = (TableColumn<AlarmTableItem, String>) c;
			}
			if (c.getId().equals("pAlarmPriority")) {
				sortColumnPriority = (TableColumn<AlarmTableItem, String>) c;
			}
			if (c.getId().equals("pEventDT")) {
				sortColumnEventDT = (TableColumn<AlarmTableItem, String>) c;
			}
		});
		
		Duration maxTimeBetweenSequentialClicks = Duration.millis(MOUSE_DURATION_MILLS);
        PauseTransition clickTimer = new PauseTransition(maxTimeBetweenSequentialClicks);
        final IntegerProperty sequentialClickCount = new SimpleIntegerProperty(0);
        clickTimer.setOnFinished(event -> {
            int count = sequentialClickCount.get();
            if (count == 2) {
            	confirmAlarm();
            }

            sequentialClickCount.set(0);
        });
        
        tvAlarms.setOnMouseClicked(event -> {
        	System.out.println(event.getTarget());
	    	sequentialClickCount.set(sequentialClickCount.get() + 1);
            clickTimer.playFromStart();
		});
		
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
							try {
								List<TViewParam> fVP = viewParams.stream().filter(sp -> sp.getAlarmref() == alarm.getAlarmid()).
										filter(sp -> Integer.parseInt(sp.getObjref()) == alarm.getLogState()).
										collect(Collectors.toList());
								String col = fVP.size() > 0 ? fVP.get(0).getParamval() : "0x00000000";
								col = Scheme.getColor(col).toString().substring(0, 8).replace("0x", "#");
								cellStyle = String.format(cellStyle, col);
							} catch (NumberFormatException e) {
								cellStyle = String.format(cellStyle, "white");
							}	
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
        
        sortColumnState.setSortType(SortType.DESCENDING);
		sortColumnPriority.setSortType(SortType.ASCENDING);
		sortColumnEventDT.setSortType(SortType.DESCENDING);
		
		tvAlarms.getSortOrder().addAll(sortColumnState, sortColumnPriority, sortColumnEventDT);
		tvAlarms.setItems(sortedData);
        
	}
	
	private void confirmAlarm() {
		AlarmTableItem ati = tvAlarms.getSelectionModel().getSelectedItem();
		if (ati != null) {
			try {
				MainStage.psClient.confirmAlarm(ati.getAlarm().getRecorddt(), ati.getAlarm().getEventdt(), 
						ati.getAlarm().getObjref(), new Timestamp(System.currentTimeMillis()), "",
						ati.getAlarm().getUserref() == 0 ? -1 : ati.getAlarm().getUserref());
			} catch (RemoteException e) {
				System.err.println("error in confirmAlarm");
				e.printStackTrace();
			}
		}
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
			e.printStackTrace();
			System.out.println("No " + a.getEventdt());
		}
	}

}
