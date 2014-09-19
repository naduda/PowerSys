package controllers;

import java.net.URL;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import ui.MainStage;
import ui.data.DataFX;
import ui.data.DataWrapper;
import model.LinkedValue;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import javafx.event.EventHandler;

public class DataController implements Initializable {
	private final ObservableList<DataWrapper> dataWrapp = FXCollections.observableArrayList();
	
	@FXML Tab tChart;
	@FXML TableView<DataWrapper> tvChart;
	@FXML ChoiceBox<LinkedValue> cbIntegration;
	@FXML DatePicker dpBegin;
	@FXML DatePicker dpEnd;
	@FXML ComboBox<Integer> cbHourBegin;
	@FXML ComboBox<Integer> cbHourEnd;
	
	private DataFX dataFX;
	private List<LinkedValue> data;
	private List<LinkedValue> dataChange;
	private int idSignal;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		cbIntegration.getSelectionModel().selectFirst();
		
		cbIntegration.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			try {
				int period = Integer.parseInt(cbIntegration.getSelectionModel().getSelectedItem().getDt().toString());
				changeTableData(MainStage.psClient.getDataIntegr(getIdSignal(), period));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		setDateHours();
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
		cbHourEnd.getSelectionModel().selectFirst();
		
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
				changeTableData(MainStage.psClient.getDataArc(idSignal, 
						Timestamp.valueOf(dpBegin.getValue().atTime(cbHourBegin.getValue(), 0)), 
						Timestamp.valueOf(dpEnd.getValue().atTime(cbHourEnd.getValue(), 0))));
				updateContent();
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
	
	public void changeTableData(List<LinkedValue> newData) {
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
		setData(dataFX.getData());
		tChart.setContent(dataFX.getChart());
		setDataTable();
	}
}
