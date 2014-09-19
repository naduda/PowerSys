package controllers;

import java.net.URL;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import model.Alarm;
import model.TSysParam;
import model.TViewParam;
import svg2fx.Convert;
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
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.MenuButton;
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
	
	@FXML TableView<AlarmTableItem> tvAlarms;
	@FXML ChoiceBox<String> cbPriority;
	@FXML MenuButton cbColumns;
	
	@FXML
	private void kvitOne(ActionEvent event) {
		System.out.println("kvitOne");
	}
	
	@FXML
	private void kvitPS(ActionEvent event) {
		data.filtered(f -> f.getAlarm().getLogstate() == 1).forEach(it -> {
			Convert.listSignals.forEach(lv -> {
				if (lv.getKey() == it.getAlarm().getObjref()) {
					confirmAlarm(it);
				}
			});
		});
	}
	
	@FXML
	private void kvitAll(ActionEvent event) {
		try {
			MainStage.psClient.confirmAlarmAll("", -1);
		} catch (RemoteException e) {
			System.err.println("error in confirmAlarmAll");
			e.printStackTrace();
		}
	}
	
	@FXML
	private void filterColumnClick(ActionEvent event) {
		tvAlarms.getSortOrder().clear();
		ObservableList<AlarmTableItem> copyData = FXCollections.observableArrayList();
		copyData.addAll(data);
		data.removeAll(data);
		copyData.sort(new DefaultSorting());
		data.addAll(copyData);
		System.out.println(Scheme.selectedShape.getIdSignal());
	}
	
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

		tvAlarms.getColumns().forEach(c -> { c.setCellValueFactory(p -> Bindings.selectString(p.getValue(), c.getId())); });
		
		Duration maxTimeBetweenSequentialClicks = Duration.millis(MOUSE_DURATION_MILLS);
        PauseTransition clickTimer = new PauseTransition(maxTimeBetweenSequentialClicks);
        final IntegerProperty sequentialClickCount = new SimpleIntegerProperty(0);
        clickTimer.setOnFinished(event -> {
            int count = sequentialClickCount.get();
            if (count == 2) {
            	confirmAlarm(tvAlarms.getSelectionModel().getSelectedItem());
            }

            sequentialClickCount.set(0);
        });
        
        tvAlarms.setOnMouseClicked(event -> {
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
								List<TViewParam> fVP = viewParams.stream().filter(sp -> sp.getAlarmref() == alarm.getAlarm().getAlarmid()).
										filter(sp -> Integer.parseInt(sp.getObjref()) == alarm.getAlarm().getLogstate()).
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
		tvAlarms.setItems(sortedData);
//		-------------------------------------------------------------------------------
		List<CheckMenuItem> checkItems = new ArrayList<CheckMenuItem>();
		
		tvAlarms.getColumns().forEach(c -> {
			CheckMenuItem it = new CheckMenuItem(c.getText());
			it.selectedProperty().addListener((observ, val, oldval) -> {
				c.setVisible(oldval);
			});
			it.setSelected(c.isVisible());
			it.setUserData(c);
			checkItems.add(it);
		});
		
		cbColumns.getItems().addAll(checkItems);
	}
	
	private void confirmAlarm(AlarmTableItem ati) {		
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
		data.sort(new DefaultSorting());
	}
	
	private class DefaultSorting implements Comparator<AlarmTableItem> {
		@Override
		public int compare(AlarmTableItem o1, AlarmTableItem o2) {
			int logState1 = o1.getAlarm().getLogstate();
			int logState2 = o2.getAlarm().getLogstate();
			int priority1 = o1.getAlarm().getAlarmpriority();
			int priority2 = o2.getAlarm().getAlarmpriority();
			Timestamp e1 = o1.getAlarm().getEventdt();
			Timestamp e2 = o2.getAlarm().getEventdt();
			
			if (logState1 != logState2) {
				if (logState1 != 1 && logState2 != 1) {
					return -e1.compareTo(e2);
				} else {
					return logState1 < logState2 ? -1 : 1;
				}
			} else {
				if (logState1 == 1) {
					if (priority1 != priority2) {
						return priority1 < priority2 ? -1 : 1;
					} else {
						return -e1.compareTo(e2);
					}
				} else {
					return -e1.compareTo(e2);
				}
			}
		}		
	}
	
	public void updateAlarm(Alarm a) {
		try {
			AlarmTableItem it = data.filtered(item -> item.getAlarm().getEventdt().equals(a.getEventdt())).get(0);			
			data.remove(it);
			addAlarm(a);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
