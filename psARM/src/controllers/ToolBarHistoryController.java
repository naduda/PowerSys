package controllers;

import java.io.File;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;

import pr.common.Utils;
import pr.log.LogFiles;
import single.ProgramProperty;
import single.SingleFromDB;
import single.SingleObject;
import ui.MainStage;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import controllers.interfaces.IControllerInit;

public class ToolBarHistoryController implements Initializable, IControllerInit {
	private final StringProperty dateProperty = new SimpleStringProperty();
	private final BooleanProperty isSlides = new SimpleBooleanProperty();
	private final StringProperty localeName = new SimpleStringProperty();
	
	private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
	
	@FXML private Button showSlides;
	@FXML private Button previus;
	@FXML private Button next;
	@FXML TextField step;
	@FXML private Label curDateTime;
	@FXML private DatePicker dpBegin;
	@FXML private ChoiceBox<Integer> cbHour;
	@FXML private ChoiceBox<Integer> cbMinute;
	@FXML private ChoiceBox<Integer> cbSecond;
	private LocalDateTime date;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		localeName.bind(ProgramProperty.localeName);
		localeName.addListener((observ, old, value) -> {
			ResourceBundle rb = Controller.getResourceBundle(new Locale(value));
			setElementText(rb);
		});
		setElementText(SingleObject.getResourceBundle());
		
		curDateTime.textProperty().bind(dateProperty);
		if (step.getText().length() == 0) step.setText("1");
		
		try {
			ImageView iw = new ImageView(
					new Image(new File(Utils.getFullPath("./Icon/exit.png")).toURI().toURL().toString()));
			iw.setFitHeight(SingleObject.getProgramSettings().getIconWidth());
			iw.setPreserveRatio(true);
			showSlides.setGraphic(iw);
			
			iw = new ImageView(
					new Image(new File(Utils.getFullPath("./Icon/hideLeft.png")).toURI().toURL().toString()));
			iw.setFitHeight(SingleObject.getProgramSettings().getIconWidth());
			iw.setPreserveRatio(true);
			previus.setGraphic(iw);
			
			iw = new ImageView(
					new Image(new File(Utils.getFullPath("./Icon/hideRight.png")).toURI().toURL().toString()));
			iw.setFitHeight(SingleObject.getProgramSettings().getIconWidth());
			iw.setPreserveRatio(true);
			next.setGraphic(iw);
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
		}
		
		dpBegin.setValue(LocalDate.now());
		
		for (int i = 0; i < 24; i++) {
			cbHour.getItems().add(i);
		}
		cbHour.getSelectionModel().select(LocalDateTime.now().getHour());
		
		for (int i = 0; i < 60; i++) {
			cbMinute.getItems().add(i);
			cbSecond.getItems().add(i);
		}
		cbMinute.getSelectionModel().select(LocalDateTime.now().getMinute());
		
		dpBegin.valueProperty().addListener(new DateChangeListener());
		cbHour.valueProperty().addListener(new DateChangeListener());
		cbMinute.valueProperty().addListener(new DateChangeListener());
		cbSecond.valueProperty().addListener(new DateChangeListener());
		
		cbSecond.getSelectionModel().select(LocalDateTime.now().getSecond());
		
		previus.disableProperty().bind(isSlides);
		next.disableProperty().bind(isSlides);
		
		dateProperty.addListener((observ, old, newValue) -> updateSchemeOnDate(getDate()));
	}
	
	private void updateSchemeOnDate(LocalDateTime date) {
		SingleFromDB.psClient.getValuesOnDate(SingleObject.activeSchemeSignals, Timestamp.valueOf(date))
			.values().forEach(s -> MainStage.controller.updateTI(s, false));
	}
	
	public LocalDateTime getDate() {
		date = LocalDateTime.parse(curDateTime.getText(), dateFormat);
		return date;
	}
	
	public void setDate(LocalDateTime date) {
		this.date = date;
		dateProperty.set(dateFormat.format(date));
	}
	
	@FXML protected void showSlides() {
		isSlides.set(!isSlides.get());
		
		new Thread(new ShowSlidesThread()).start();
	}
	
	@FXML protected void previous() {
		setDate(getDate().plusSeconds(-Long.parseLong(step.getText())));
	}
	
	@FXML protected void next() {
		setDate(getDate().plusSeconds(Long.parseLong(step.getText())));
	}
	
	@Override
	public void setElementText(ResourceBundle rb) {
		previus.setTooltip(new Tooltip(rb.getString("keyTooltip_previous")));
		next.setTooltip(new Tooltip(rb.getString("keyTooltip_next")));
		showSlides.setTooltip(new Tooltip(rb.getString("keyTooltip_showHistoryAuto")));
	}
	
	private class DateChangeListener implements ChangeListener<Object> {
		@Override
		public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
			LocalDate d = dpBegin.getValue();
			String dd = d.getDayOfMonth() < 10 ? "0" + d.getDayOfMonth() : d.getDayOfMonth() + "";
			String mm = d.getMonthValue() < 10 ? "0" + d.getMonthValue() : d.getMonthValue() + "";
			String yy = d.getYear() < 10 ? "0" + d.getYear() : d.getYear() + "";
			
			dateProperty.set(dd + "." + mm + "." + yy + " " + 
					(cbHour.getValue() < 10 ? "0" + cbHour.getValue() : cbHour.getValue()) + ":" + 
					(cbMinute.getValue() < 10 ? "0" + cbMinute.getValue() : cbMinute.getValue()) + ":" + 
					(cbSecond.getValue() < 10 ? "0" + cbSecond.getValue() : cbSecond.getValue()));
			
		}
	}
	
	private class ShowSlidesThread implements Runnable {
		@Override
		public void run() {
			while (isSlides.get()) {
				Platform.runLater(() -> setDate(getDate().plusSeconds(Long.parseLong(step.getText()))));
				
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					LogFiles.log.log(Level.SEVERE, e.getMessage());
				}
			}
		}
	}
}
