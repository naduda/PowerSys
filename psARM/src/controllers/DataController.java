package controllers;

import java.net.URL;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import controllers.interfaces.IControllerInit;
import ui.Main;
import ui.MainStage;
import ui.data.DataFX;
import ui.data.DataWrapper;
import ui.data.LineChartContainer;
import pr.model.LinkedValue;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import javafx.event.EventHandler;

public class DataController implements Initializable, IControllerInit {
	private final ObservableList<DataWrapper> dataWrapp = FXCollections.observableArrayList();
	
	@FXML Tab tTable;
	@FXML Tab tChart;
	@FXML TableView<DataWrapper> tvChart;
	@FXML ChoiceBox<LinkedValue> cbIntegration;
	@FXML DatePicker dpBegin;
	@FXML DatePicker dpEnd;
	@FXML ComboBox<Integer> cbHourBegin;
	@FXML ComboBox<Integer> cbHourEnd;
	
	@FXML Label lPeriodFrom;
	@FXML Label lTo;
	
	private DataFX dataFX;
	private List<LinkedValue> data;
	private List<LinkedValue> dataChange;
	private int idSignal;
	
	@Override
	public void initialize(URL url, ResourceBundle boundle) {
		cbIntegration.getSelectionModel().selectFirst();
		
		cbIntegration.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			try {
				int period = Integer.parseInt(cbIntegration.getSelectionModel().getSelectedItem().getDt().toString());
				if (tChart.getContent() == null) {
					changeTableData(MainStage.psClient.getDataIntegr(getIdSignal(), new Timestamp(System.currentTimeMillis() - 24*60*60*1000), 
							new Timestamp(System.currentTimeMillis()), period));
				} else {
					changeTableData(MainStage.psClient.getDataIntegrArc(getIdSignal(), Timestamp.valueOf(dpBegin.getValue().atTime(cbHourBegin.getValue(), 0)), 
							Timestamp.valueOf(dpEnd.getValue().atTime(cbHourEnd.getValue(), 0)), period));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		setDateHours();
		
		ResourceBundle rb = Controller.getResourceBundle(new Locale(Main.getProgramSettings().getLocaleName()));
		setElementText(rb);
	}
	
	@Override
	public void setElementText(ResourceBundle rb) {
		cbIntegration.getItems().forEach(lv -> {
			if (lv.getDt().toString().equals("0")) {
				lv.setVal(rb.getString("keyInstantaneous"));
			} else {
				lv.setVal(lv.getDt() + " " + rb.getString("keyMinute"));
			}
		});
		lPeriodFrom.setText(rb.getString("keyPeriodFrom"));
		lTo.setText(rb.getString("keyTo"));
		tTable.setText(rb.getString("keyTable"));
		tChart.setText(rb.getString("keyChart"));
		
		tvChart.getColumns().forEach(c -> {
			c.setText(rb.getString("key_" + c.getId()));
		});
	}
	
	@SuppressWarnings("unchecked")
	private void setDateHours() {
		dpEnd.setValue(LocalDate.now().plusDays(1));
		dpBegin.setValue(LocalDate.now());
		
		List<Integer> lHours = new ArrayList<>();
		for (int i = 0; i < 24; i++) {
			lHours.add(i);
		}
		ObservableList<Integer> lHoursObs = FXCollections.observableArrayList();
		lHoursObs.addAll(lHours);
		cbHourBegin.setItems(lHoursObs);
		cbHourBegin.getSelectionModel().selectFirst();
		cbHourEnd.setItems(lHoursObs);
		cbHourEnd.getSelectionModel().select(LocalTime.now().getHour());
		
		dpBegin.setOnAction(new DateHandler());
		dpEnd.setOnAction(new DateHandler());
		cbHourBegin.setOnAction(new DateHandler());
		cbHourEnd.setOnAction(new DateHandler());
	}
	
	@SuppressWarnings("rawtypes")
	private class DateHandler implements EventHandler {
		@Override
		public void handle(Event e) {
			try {
				List<LinkedValue> newData = cbIntegration.getSelectionModel().getSelectedIndex() == 0 ? 
						MainStage.psClient.getDataArc(idSignal, 
								Timestamp.valueOf(dpBegin.getValue().atTime(cbHourBegin.getValue(), 0)),
								Timestamp.valueOf(dpEnd.getValue().atTime(cbHourEnd.getValue(), 0))) :
						MainStage.psClient.getDataIntegrArc(getIdSignal(), Timestamp.valueOf(dpBegin.getValue().atTime(cbHourBegin.getValue(), 0)), 
								Timestamp.valueOf(dpEnd.getValue().atTime(cbHourEnd.getValue(), 0)), 
								Integer.parseInt(cbIntegration.getSelectionModel().getSelectedItem().getDt().toString()));
				
				changeTableData(newData);
			} catch (RemoteException re) {
				re.printStackTrace();
			}
		}
	}
	
	public Tab gettChart() {
		return tChart;
	}

	public TableView<?> getTvChart() {
		return tvChart;
	}
	
	private void changeTableData(List<LinkedValue> newData) {		
		dataChange = newData;
		dataChange.forEach(v -> {v.setVal((double)v.getVal() * dataFX.getSignal().getKoef());});		
		dataFX.setData(dataChange);
		
		updateContent();
	}
	
	public void setDataTable() {
		if (dataChange != null) {
			dataWrapp.clear();
			dataChange.forEach(e -> { dataWrapp.add(new DataWrapper(e, "dd.MM.yyyy HH:mm:ss", "0.000")); });
			
			tvChart.getColumns().forEach(c -> { c.setCellValueFactory(p -> Bindings.selectString(p.getValue(), c.getId())); });
			tvChart.setItems(dataWrapp);
		}
	}
	
	public List<LinkedValue> getData() {
		return data;
	}

	public void setData(List<LinkedValue> data) {
		dataChange = data;
		this.data = data;
	}

	public int getIdSignal() {
		return idSignal;
	}

	public void setIdSignal(int idSignal) {
		dataFX = new DataFX(idSignal);			
		updateContent();
		this.idSignal = idSignal;
	}
	
	private void updateContent() {
		try {
			setData(dataFX.getData());
			if (tChart.getContent() == null) {
				tChart.setContent(dataFX.getChart());
			} else {
				LineChartContainer chartContainer = (LineChartContainer) tChart.getContent();
				chartContainer.setData(data);
			}
			setDataTable();
		} catch (Exception e) {
			System.out.println("CATCH");
		}
	}
}
