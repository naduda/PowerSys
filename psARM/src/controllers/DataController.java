package controllers;

import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;

import controllers.interfaces.IControllerInit;
import pr.log.LogFiles;
import single.ProgramProperty;
import single.SingleFromDB;
import single.SingleObject;
import ui.data.DataFX;
import ui.data.DataWrapper;
import ui.data.LineChartContainer;
import pr.model.LinkedValue;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

@SuppressWarnings("rawtypes")
public class DataController implements Initializable, IControllerInit {
	private final StringProperty localeName = new SimpleStringProperty();
	private static final String DATE_FORMAT = "dd.MM.yyyy HH:mm:ss";
	private static final String NUMBER_FORMAT = "0.000";
	
	@FXML private Tab tTable;
	@FXML private Tab tChart;
	@FXML private TableView tvChart;
	@FXML private ChoiceBox<LinkedValue> cbIntegration;
	@FXML private DatePicker dpBegin;
	@FXML private DatePicker dpEnd;
	@FXML private ComboBox<Integer> cbHourBegin;
	@FXML private ComboBox<Integer> cbHourEnd;
	
	@FXML Label lPeriodFrom;
	@FXML Label lTo;
	
	private DataFX dataFX;
	private TableColumn<Map, String> columnDate = new TableColumn<>();
	
	@Override
	public void initialize(URL url, ResourceBundle boundle) {
		ResourceBundle rb = Controller.getResourceBundle(new Locale(SingleObject.getProgramSettings().getLocaleName()));
		setElementText(rb);
		
		localeName.bind(ProgramProperty.localeName);
		localeName.addListener((observ, old, value) -> setElementText(Controller.getResourceBundle(new Locale(value))));
		
		cbIntegration.getSelectionModel().selectFirst();
		
		cbIntegration.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) changeTableData(getDataFromDB());
		});
		
		setDateHours();
	}
	
	@Override
	public void setElementText(ResourceBundle rb) {
		if (lTo.getScene() != null) ((Stage)lTo.getScene().getWindow()).setTitle(rb.getString("keyDataTitle"));
		
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
		
		columnDate.setText(rb.getString("key_pDate"));
	}
	
	private void setDateHours() {
		dpEnd.setValue(LocalDate.now().atTime(LocalTime.now().plusHours(1)).toLocalDate());
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
		cbHourEnd.getSelectionModel().select(LocalTime.now().getHour() + 1);
		
		dpBegin.setOnAction(e -> changeTableData(getDataFromDB()));
		dpEnd.setOnAction(e -> changeTableData(getDataFromDB()));
		cbHourBegin.setOnAction(e -> changeTableData(getDataFromDB()));
		cbHourEnd.setOnAction(e -> changeTableData(getDataFromDB()));
	}
	
	private List<LinkedValue> getDataFromDB() {
		List<LinkedValue> data = new ArrayList<>();
		dataFX.getIdSignals().forEach(idSignal -> data.addAll(getDataFromDB(idSignal)));
		return data;
	}
	
	private List<LinkedValue> getDataFromDB(int idSignal) {
		List<LinkedValue> data = new ArrayList<>();
		try {
			int period = Integer.parseInt(cbIntegration.getSelectionModel().getSelectedItem().getDt().toString());
			data.addAll(SingleFromDB.psClient.getDataIntegrArc(idSignal, Timestamp.valueOf(dpBegin.getValue().atTime(cbHourBegin.getValue(), 0)), 
					Timestamp.valueOf(dpEnd.getValue().atTime(cbHourEnd.getValue(), 0)), period));
		} catch (Exception e) {
			e.printStackTrace();
			LogFiles.log.log(Level.SEVERE, "List<LinkedValue> getDataFromDB(...)", e);
		}
		return data;
	}
	
	public void addData(int idSignal) {
		List<LinkedValue> newData = getDataFromDB(idSignal);
		
		dataFX.getData().forEach(d -> d.setVal((double)d.getVal() / SingleFromDB.getSignals().get(d.getId()).getKoef()));
		dataFX.getData().addAll(newData);
		
		changeTableData(dataFX.getData());
	}
	
	private void changeTableData(List<LinkedValue> newData) {
		newData.forEach(v -> v.setVal((double)v.getVal() * SingleFromDB.getSignals().get(v.getId()).getKoef()));
		dataFX.setData(newData);
		
		updateContent();
	}
	
	public void setDataTable() {
		DataWrapper dw = new DataWrapper(DATE_FORMAT, NUMBER_FORMAT);
		dw.wrapData(dataFX, tvChart, columnDate);
	}

	public void setIdSignals(List<Integer> idSignals) {
		dataFX = new DataFX(idSignals);
		List<LinkedValue> data = getDataFromDB();
		data.forEach(d -> d.setVal((double)d.getVal() * SingleFromDB.getSignals().get(d.getId()).getKoef()));
		dataFX.setData(data);
		updateContent();
	}
	
	private void updateContent() {
		try {
			if (tChart.getContent() == null) {
				tChart.setContent(dataFX.getChart());
			} else {
				LineChartContainer chartContainer = (LineChartContainer) tChart.getContent();
				chartContainer.setData(dataFX.getData());
			}
			
			if (cbIntegration.getSelectionModel().getSelectedIndex() == 0 && dataFX.getIdSignals().size() > 1) {
				tvChart.getItems().clear();
				tvChart.getColumns().clear();
			} else {
				setDataTable();
			}
		} catch (Exception e) {
			LogFiles.log.log(Level.WARNING, "void updateContent()", e);
		}
	}
}
