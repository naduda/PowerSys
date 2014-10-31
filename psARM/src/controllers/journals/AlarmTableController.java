package controllers.journals;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import controllers.Controller;
import controllers.TrCommentController;
import pr.common.Utils;
import pr.model.Alarm;
import pr.model.TSysParam;
import pr.model.TViewParam;
import svg2fx.Convert;
import ui.Main;
import ui.MainStage;
import ui.Scheme;
import ui.tables.AlarmTableItem;
import javafx.animation.PauseTransition;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

public class AlarmTableController extends TableController {
	
	private static final double MOUSE_DURATION_MILLS = 250;
	private List<TViewParam> viewParams;
	private Map<String, TSysParam> sysParamsPriority;
	private Map<String, TSysParam> sysParamsEvent;
	
	private final Duration maxTimeBetweenSequentialClicks = Duration.millis(MOUSE_DURATION_MILLS);
	private final PauseTransition clickTimer = new PauseTransition(maxTimeBetweenSequentialClicks);
	private final IntegerProperty sequentialClickCount = new SimpleIntegerProperty(0);
	
	@FXML ToolBar tbAlarms;
	@FXML ChoiceBox<String> cbPriority;
	@FXML ChoiceBox<String> cbEvent;
	@FXML MenuButton cbColumns;
	@FXML Label lAlarms;
	@FXML Label lPriority;
	@FXML Label lEvent;
	@FXML Label lCount;
	@FXML Text tCount;
	@FXML Button btnSorting;
	
	@FXML
	private void kvitOne(ActionEvent event) {
		Stage stage = new Stage();
		
		try {
			FXMLLoader loader = new FXMLLoader(new URL("file:/" + Utils.getFullPath("./ui/TransparantComment.xml")));
			Parent root = loader.load();
			TrCommentController trController = loader.getController();
			
			Scene scene = new Scene(root);
			stage.setScene(scene);
			ResourceBundle rb = Controller.getResourceBundle(new Locale(Main.getProgramSettings().getLocaleName()));
			trController.setElementText(rb);
			trController.setAlarmTableItem((AlarmTableItem) tvTable.getSelectionModel().getSelectedItem());
			stage.initModality(Modality.NONE);
			stage.initOwner(((Control)event.getSource()).getScene().getWindow());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	    stage.show();
	}
	
	@FXML
	private void kvitPS(ActionEvent event) {
		
		data.filtered(f -> ((AlarmTableItem)f).getAlarm().getLogstate() == 1).forEach(it -> {
			Convert.listSignals.forEach(lv -> {
				if (lv.getKey() == ((AlarmTableItem)it).getAlarm().getObjref()) {
					confirmAlarm((AlarmTableItem)it, "");
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
		tvTable.getSortOrder().clear();
		ObservableList<Object> copyData = FXCollections.observableArrayList();
		copyData.addAll(data);
		data.removeAll(data);
		copyData.sort(new DefaultSorting());
		data.addAll(copyData);
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		super.initialize(location, resources);
		tCount.textProperty().bind(getCountProperty());
		
		setChoiceBoxes();		
		setOnMouseClicked();		
		changeRowFactory();
//		-------------------------------------------------------------------------------
		setVisibleAlarmColumns();
		setCbColumns();		
		setCurrentDayItems();
	}
	
	private void setOnMouseClicked() {
		clickTimer.setOnFinished(event -> {
            int count = sequentialClickCount.get();
            if (count == 2) {
            	confirmAlarm((AlarmTableItem)tvTable.getSelectionModel().getSelectedItem(), "");
            }
            sequentialClickCount.set(0);
        });
        
        tvTable.setOnMouseClicked(event -> {
	    	sequentialClickCount.set(sequentialClickCount.get() + 1);
            clickTimer.playFromStart();
		});
	}
	
	private void setChoiceBoxes() {
		try {
			sysParamsPriority = MainStage.psClient.getTSysParam("ALARM_PRIORITY");
			sysParamsEvent = MainStage.psClient.getTSysParam("ALARM_EVENT");
			viewParams = MainStage.psClient.getTViewParam("LOG_STATE", "COLOR", -1);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		cbPriority = setChoiceBoxItems(sysParamsPriority, cbPriority);
		cbEvent = setChoiceBoxItems(sysParamsEvent, cbEvent);
	}
	
	private void changeRowFactory() {
		tvTable.setRowFactory(new Callback<TableView<Object>, TableRow<Object>>() {			
			@Override
			public TableRow<Object> call(TableView<Object> param) {
				final TableRow<Object> row = new TableRow<Object>() {
	                @Override
	                protected void updateItem(Object alarm, boolean empty){
	                    super.updateItem(alarm, empty);
	                    String cellStyle = "-fx-control-inner-background: %s;"
	                    				 + "-fx-accent: derive(-fx-control-inner-background, -40%%);"
	                    				 + "-fx-cell-hover-color: derive(-fx-control-inner-background, -20%%);";
	                    if (alarm != null && ((AlarmTableItem)alarm).getPConfirmDT().equals("")) {
							try {
								List<TViewParam> fVP = viewParams.stream().
										filter(sp -> sp.getAlarmref() == ((AlarmTableItem)alarm).getAlarm().getAlarmid()).
										filter(sp -> Integer.parseInt(sp.getObjref()) == ((AlarmTableItem)alarm).getAlarm().getLogstate()).
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
	}
	
	private void setCurrentDayItems() {
		try {
			MainStage.psClient.getAlarmsCurrentDay().forEach(it -> addItem(new AlarmTableItem(it)));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	private void setVisibleAlarmColumns() {
		String showColumnsString = Main.getProgramSettings().getShowAlarmColumns();
		StringTokenizer st = new StringTokenizer(showColumnsString, ":");
		int i = 0;
		while (st.hasMoreElements()) {
			boolean isShow = st.nextElement().toString().equals("1") ? true : false;
			tvTable.getColumns().get(i).setVisible(isShow);
			i++;
		}
	}
	
	private void setCbColumns() {
		cbColumns.getItems().clear();
		List<CheckMenuItem> checkItems = new ArrayList<CheckMenuItem>();
		
		tvTable.getColumns().forEach(c -> {
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
	
	@Override
	public void setElementText(ResourceBundle rb) {
		super.setElementText(rb);
		lAlarms.setText(rb.getString("keyAlarms"));
		lPriority.setText(rb.getString("keyPriority"));
		lEvent.setText(rb.getString("keyLevent"));
		lCount.setText(rb.getString("keyCount"));
		
		cbPriority.getItems().replaceAll(s -> {			
			if (s.equals(cbPriority.getItems().get(0))) {
				return rb.getString("keyPriorityAny");
			}
			return s;
		});
		cbPriority.setValue(cbPriority.getItems().get(0));
		
		cbEvent.getItems().replaceAll(s -> {			
			if (s.equals(cbEvent.getItems().get(0))) {
				return rb.getString("keyPriorityAny");
			}
			return s;
		});
		cbEvent.setValue(cbEvent.getItems().get(0));
		
		btnSorting.setText(rb.getString("keySorting"));
		cbColumns.setText(rb.getString("keyColumns"));
		setCbColumns();
	}
	
	@Override
	public void addItem(Object e) {
		super.addItem(e);
		data.sort(new DefaultSorting());
	}
	
	private ChoiceBox<String> setChoiceBoxItems(Map<String, TSysParam> map, ChoiceBox<String> cb) {
		List<String> list = new ArrayList<>();
		map.values().forEach(it -> list.add(it.getParamdescr()));
		ObservableList<String> obList = FXCollections.observableList(list);
		
		cb.getItems().addAll(obList);
		cb.setValue(cb.getItems().get(0));
		cb.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(a -> {
            	String lowerCaseFilterPriority = cbPriority.getSelectionModel().getSelectedItem().toLowerCase();
            	String lowerCaseFilterEvent = cbEvent.getSelectionModel().getSelectedItem().toLowerCase();
            	
            	boolean isPriority = cbPriority.getSelectionModel().getSelectedIndex() == 0 ? true :
            							((AlarmTableItem)a).getPAlarmPriority().toLowerCase().equals(lowerCaseFilterPriority);
            	
            	boolean isEvent = cbEvent.getSelectionModel().getSelectedIndex() == 0 ? true :
            							((AlarmTableItem)a).getPEventType().toLowerCase().equals(lowerCaseFilterEvent);
            	
            	if (isPriority && isEvent) {
        			return true;
        		} else {
        			return false;
        		}
            });
            getCountProperty().setValue(tvTable.getItems().size() + "");
        });
		
		return cb;
	}
	
	public static void confirmAlarm(AlarmTableItem ati, String comment) {		
		if (ati != null) {
			try {
				MainStage.psClient.confirmAlarm(ati.getAlarm().getRecorddt(), ati.getAlarm().getEventdt(), 
						ati.getAlarm().getObjref(), new Timestamp(System.currentTimeMillis()), comment,
						ati.getAlarm().getUserref() == 0 ? -1 : ati.getAlarm().getUserref());
			} catch (RemoteException e) {
				System.err.println("error in confirmAlarm");
				e.printStackTrace();
			}
		}
	}
	
	private class DefaultSorting implements Comparator<Object> {
		@Override
		public int compare(Object obj1, Object obj2) {
			AlarmTableItem o1 = (AlarmTableItem) obj1;
			AlarmTableItem o2 = (AlarmTableItem) obj2;
			
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
			AlarmTableItem it = (AlarmTableItem) data.
					filtered(item -> ((AlarmTableItem)item).getAlarm().getEventdt().equals(a.getEventdt())).get(0);			
			data.remove(it);
			addItem(new AlarmTableItem(a));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public TableView<Object> getTvTable() {
		return tvTable;
	}
}
