package controllers;

import java.awt.MouseInfo;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;

import javax.imageio.ImageIO;

import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.transform.Scale;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pr.common.Utils;
import pr.log.LogFiles;
import pr.model.LinkedValue;
import pr.model.NormalModeJournalItem;
import controllers.interfaces.IControllerInit;
import controllers.interfaces.StageLoader;
import controllers.journals.JAlarmsController;
import controllers.tree.TreeController;
import single.ProgramProperty;
import single.SingleFromDB;
import single.SingleObject;
import svg2fx.Convert;
import svg2fx.fxObjects.EShape;
import ui.MainStage;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class ToolBarController implements Initializable, IControllerInit {
	private final DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	
	private final BooleanProperty showInfoProperty = new SimpleBooleanProperty();
	public static BooleanProperty showHistoryProperty = new SimpleBooleanProperty(false);
	private final BooleanProperty showNavigatorProperty = new SimpleBooleanProperty();
	private final BooleanProperty showNormalMode = new SimpleBooleanProperty();
	public static final DoubleProperty zoomProperty = new SimpleDoubleProperty();
	
	private static final double ZOOM_FACTOR = 0.1;
	private static final double ZOOM_MAX = 5;
	private static StageLoader infoStage;
	private static Stage navigatorStage;
	private String stateReading1;
	private String stateReading2;
	private String stateReading3;
	
	private final List<String> normalModeShapes = new ArrayList<>();
	
	@FXML private ToolBar tbMain;
	@FXML private Slider zoomSlider;
	@FXML private Label lDataOn;
	@FXML private Label lLastDate;
	@FXML private Button info;
	@FXML private Button fit;
	@FXML private Button navigator;
	@FXML private Button normalMode;
	@FXML private Button hideLeft;
	@FXML private Button next;
	@FXML private Button previous;
	
	@Override
	public void initialize(URL url, ResourceBundle boundle) {
		tbMain.getItems().filtered(f -> f.getClass().equals(Button.class)).forEach(it -> {
			if (it.getId() != null) {
				File icon = new File(Utils.getFullPath("./Icon/" + it.getId() + ".png"));
				if (icon.exists()) {
					try {
						ImageView iw = new ImageView(new File(icon.getAbsolutePath()).toURI().toURL().toString());
						iw.setFitHeight(SingleObject.getProgramSettings().getIconWidth());
						iw.setPreserveRatio(true);
						((Button)it).setGraphic(iw);
					} catch (Exception e) {
						LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
					}
				}
			}
		});
		
		setElementText(SingleObject.getResourceBundle());
		next.setGraphic(null);
		previous.setGraphic(null);
		
		zoomSlider.valueProperty().bindBidirectional(zoomProperty);
		zoomSlider.setMin(ZOOM_FACTOR);
		zoomSlider.setMax(ZOOM_MAX);
		zoomSlider.setBlockIncrement(ZOOM_FACTOR);
		zoomSlider.valueProperty().addListener((observ, oldValue, newValue) -> zoomSlider.setTooltip(new Tooltip(newValue + "")));
		zoomProperty.addListener((observ, oldValue, newValue) -> changeZoom((double)newValue));
		
		showHistoryProperty.addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				MainStage.controller.getHistoryPane().showSide();
			} else {
				MainStage.controller.getHistoryPane().hideSide();
			}
		});
		
		showNormalMode.addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				Timestamp dtBeg = Timestamp.valueOf(LocalDate.now().atTime(0, 0, 0));
				Timestamp dtEnd = Timestamp.valueOf(LocalDate.now().plusDays(1).atTime(0, 0));
				
				List<NormalModeJournalItem> items = SingleFromDB.psClient.getListNormalModeItems(dtBeg, dtEnd, SingleObject.activeSchemeSignals);
				items.forEach(it -> {
					if (it.getDt_new() == null) {
						SingleObject.mainScheme.getListSignals().stream().filter(f -> f.getId() == it.getIdsignal()).forEach(s -> {
							try {
								EShape tt = SingleObject.mainScheme.getDeviceById(s.getVal().toString());
								tt.setNormalMode();
								normalModeShapes.add(s.getVal().toString());
							} catch (Exception e) {
								LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
							}
						});
					}
				});
				normalMode.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.THIN)));
			} else {
				normalModeShapes.forEach(s -> {
					try {
						EShape tt = SingleObject.mainScheme.getDeviceById(s);
						tt.clearNormalMode();
					} catch (Exception e) {
						LogFiles.log.log(Level.SEVERE, "void initialize(...)", e);
					}
				});
				normalMode.setBorder(new Border(new BorderStroke(Color.TRANSPARENT, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.THIN)));
			}
		});
	}
	
	@FXML 
	protected void zoomMinus() {
		changeZoom(false);
	}
	
	@FXML 
	protected void zoomPlus() {
		changeZoom(true);
	}
	
	private void changeZoom(boolean isIncrease) {
		DecimalFormat df = new DecimalFormat("0.0");
		Group root = SingleObject.mainScheme.getRoot();
		double k = Double.parseDouble(df.format(root.getScaleX()).replace(',', '.'));
		
		k = isIncrease ? k + ZOOM_FACTOR : k - ZOOM_FACTOR;
		if (k >= ZOOM_FACTOR && k <= ZOOM_MAX) {
			changeZoom(k);
		}
        zoomProperty.set(k);
	}
	
	public static void changeZoom(double zoom) {
		Group root = SingleObject.mainScheme.getRoot();
		root.setScaleX(zoom);
        root.scaleYProperty().bind(root.scaleXProperty());
        zoomProperty.set(zoom);
	}
	
	@FXML 
	protected void exitButtonAction() {
		Controller.exitProgram();
	}
	
	@FXML 
	protected void info() {
		if (SingleObject.selectedShape == null) return;
		if (infoStage == null) {
			infoStage = new StageLoader("Info.xml", SingleObject.getResourceBundle().getString("keyTooltip_info"), true);
			infoStage.setOnCloseRequest(e -> {
				showInfoProperty.set(false);
			});
			showInfoProperty.addListener(new ShowChangeListener(info, infoStage));
		}
		
		showInfoProperty.set(!showInfoProperty.get());
	}
	
	@FXML protected void hideTree() {
		MainStage.controller.getTreeSplitPane().showHideSide();
		
	}
	
	@FXML protected void previous() {
		System.out.println("previous");
		TreeController treeController = MainStage.controller.getSpTreeController();
		treeController.setPreviusNextButton(true);
		int curIndex = treeController.getSchemeHistory().indexOf(treeController.getTvSchemes().getSelectionModel().getSelectedItem());
		if (curIndex < 1) return;
		TreeItem<LinkedValue> prevItem = treeController.getSchemeHistory().get(curIndex - 1);
		treeController.getTvSchemes().getSelectionModel().select(prevItem);
		
		String curClass = curIndex == 1 ? "previous_gray" : "previous";
		if (!next.getStyleClass().get(0).equalsIgnoreCase("next")) {
			next.getStyleClass().clear();
			next.getStyleClass().add("next");
		}
		if (!previous.getStyleClass().get(0).equalsIgnoreCase(curClass)) {
			previous.getStyleClass().clear();
			previous.getStyleClass().add(curClass);
		}
	}
	
	@FXML protected void next() {
		System.out.println("next");
		TreeController treeController = MainStage.controller.getSpTreeController();
		treeController.setPreviusNextButton(true);
		int curIndex = treeController.getSchemeHistory().indexOf(treeController.getTvSchemes().getSelectionModel().getSelectedItem());
		if (curIndex == treeController.getSchemeHistory().size() - 1) return;
		TreeItem<LinkedValue> prevItem = treeController.getSchemeHistory().get(curIndex + 1);
		treeController.getTvSchemes().getSelectionModel().select(prevItem);
		
		String curClass = curIndex == treeController.getSchemeHistory().size() - 2 ? "next_gray" : "next";
		if (!previous.getStyleClass().get(0).equalsIgnoreCase("previous")) {
			previous.getStyleClass().clear();
			previous.getStyleClass().add("previous");
		}
		if (!next.getStyleClass().get(0).equalsIgnoreCase(curClass)) {
			next.getStyleClass().clear();
			next.getStyleClass().add(curClass);
		}
	}
	
	@FXML protected void navigator() {	
		if (navigatorStage == null) {
			navigatorStage = SingleObject.getNavigator();
			navigatorStage.setTitle(SingleObject.getResourceBundle().getString("keyTooltip_navigator"));
			navigatorStage.setOnCloseRequest(e -> {
				showNavigatorProperty.set(false);
			});
			showNavigatorProperty.addListener(new ShowChangeListener(navigator, navigatorStage));
		}
		
		showNavigatorProperty.set(!showNavigatorProperty.get());
	}
	
	@FXML protected void retro() {
		showHistoryProperty.set(!showHistoryProperty.get());
	}
	
	@FXML protected void fullScreen() {
		SingleObject.mainStage.setFullScreen(!SingleObject.mainStage.isFullScreen());
	}
	
	@FXML protected void save() {
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extentionFilter = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png");
		fileChooser.getExtensionFilters().add(extentionFilter);
		fileChooser.setTitle(SingleObject.getResourceBundle().getString("keyTooltip_save"));
		File file = fileChooser.showSaveDialog(SingleObject.mainStage);
		if (file != null) {
			SnapshotParameters parameters = new SnapshotParameters();
	        parameters.setFill(Convert.backgroundColor);
	        
	        double oldScale = SingleObject.mainScheme.getRoot().getScaleX();
	        double oldX = SingleObject.mainScheme.getHvalue();
	        double oldY = SingleObject.mainScheme.getVvalue();
	        SingleObject.mainScheme.getRoot().setScaleX(1);
			WritableImage writableImage = SingleObject.mainScheme.getContent().snapshot(parameters, null);
			
			try {
				ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", file);
			} catch (IOException e) {
				LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
			}
			SingleObject.mainScheme.getRoot().setScaleX(oldScale);
			SingleObject.mainScheme.setHvalue(oldX);
			SingleObject.mainScheme.setVvalue(oldY);
		}
	}

//	==============================================================================================================
	private String activeDevices;
	@FXML protected void server() {
		List<Integer> devices = SingleFromDB.psClient.getActiveDevices(SingleObject.activeSchemeSignals);
		activeDevices = "{";
		devices.forEach(d -> activeDevices = activeDevices + d + ",");
		activeDevices = activeDevices.substring(0, activeDevices.length() - 1) + "}";
		//SingleFromDB.psClient.setDevicesState(activeDevices, 4);
//		Error in 9.4 does not work without timeout -> Thread.sleep(1000);
		devices.forEach(d -> {
			SingleFromDB.psClient.setDevicesState(String.format("{%d}",  d), 4);
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
			}
		});
		
		ProgressReading pr = new ProgressReading(stateReading1);
		pr.show();
	}
	
	private class ProgressReading extends Stage {
		final StringProperty pr = new SimpleStringProperty("0");
		final StringProperty tDeviceProperty = new SimpleStringProperty();
		private boolean isRun = true;
		private List<LinkedValue> states = new ArrayList<>();
		final private Map<Integer, ProgressIndicator> pins = new HashMap<>();
		final private Map<Integer, Label> lStates = new HashMap<>();
		
		public ProgressReading(String title) {
			tDeviceProperty.bind(ProgramProperty.tDeviceProperty);
			tDeviceProperty.addListener((observ, oldValue, newValue) -> {
				String[] pars = newValue.split(";");
				int idDevice = Integer.parseInt(pars[0]);
				int state = Integer.parseInt(pars[1]);
				switch (state) {
					case 1:
						lStates.get(idDevice).setText(stateReading1);
						break;
					case 2:
						lStates.get(idDevice).setText(stateReading2 + "  -> " + pr.get());
						pins.get(idDevice).setProgress(1);
						pins.get(idDevice).setPrefSize(50, 50);
						pr.set("0");
						activeDevices = activeDevices.replace(idDevice + "", "").replace(",,",",").replace(",}","}");
						break;
					case 3:
						lStates.get(idDevice).setText(stateReading3 + "  -> " + pr.get());
						pins.get(idDevice).setProgress(1);
						pins.get(idDevice).setPrefSize(50, 50);
						pr.set("0");
						activeDevices = activeDevices.replace(idDevice + "", "").replace(",,",",").replace(",}","}");
						break;
				}
			});
			
			Group root = new Group();
	        Scene scene = new Scene(root);
	        setScene(scene);
	        setTitle(title);
	        initModality(Modality.NONE);
			initOwner(SingleObject.mainStage.getScene().getWindow());
	        
			states = SingleFromDB.psClient.getDevicesState(activeDevices);
	        
			final VBox vb = new VBox();
			vb.setPadding(new Insets(5, 5, 5, 5));
			vb.setSpacing(5);
			
			states.forEach(d -> {
				final ProgressIndicator pin = new ProgressIndicator();
		        pin.setPrefSize(25, 25);
		        pins.put(d.getId(), pin);
		        pin.setProgress(-1);
		        
		        final Label lState = new Label();
		        lState.setAlignment(Pos.CENTER_LEFT);
		        lStates.put(d.getId(), lState);
		        
		        final HBox hb = new HBox();
		        hb.setSpacing(5);
		        hb.setAlignment(Pos.CENTER);
		        hb.getChildren().addAll(new Label(d.getDt().toString()), pin, lState);
		        
		        vb.getChildren().add(hb);
			});
			scene.setRoot(vb);
			
	        new Thread(() -> {
	        	while(isRun) {
	        		try {
						Thread.sleep(1000);
					} catch (Exception e) {
						LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
					}
	        		
	        		int t = Integer.parseInt(pr.get()) + 1;
	        		
		        	Platform.runLater(() -> {
		        		pr.set("" + t);
		        		setTitle(title + " -> " + t);
						if (getWidth() < 300) setWidth(300);
		        		
		        		isRun = activeDevices.length() > 3;
		        	});
	        	}
	        }).start();
	        
	        getScene().addEventHandler(KeyEvent.KEY_PRESSED, t -> {
				if (t.getCode()==KeyCode.ESCAPE) {
					LogFiles.log.log(Level.INFO, "Exit " + getTitle());
					isRun = false;
					close();
				}
			});
	        setOnCloseRequest(wc -> isRun = false);
		}
	}
//	==============================================================================================================
	
	@FXML protected void print() {
		ImageView node = SingleObject.getSchemeImage(1000);
		Image image = node.getImage();
		image = SingleObject.invertImage(image);
		node = new ImageView(image);
		
		Printer printer = Printer.getDefaultPrinter();
		
		PageLayout pageLayout = printer.createPageLayout(Paper.A4, 
				image.getWidth() > image.getHeight() ? PageOrientation.LANDSCAPE : PageOrientation.PORTRAIT, 
				Printer.MarginType.DEFAULT);
		
		double scaleX = pageLayout.getPrintableWidth() / node.getBoundsInParent().getWidth();
		double scaleY = pageLayout.getPrintableHeight() / node.getBoundsInParent().getHeight();
		
		scaleX = scaleX < scaleY ? scaleX : scaleY;
		node.getTransforms().add(new Scale(scaleX, scaleX));
		
		PrinterJob job = PrinterJob.createPrinterJob();
		job.getJobSettings().setPageLayout(pageLayout);
		if (job.showPrintDialog(SingleObject.mainStage)) {
			boolean success = job.printPage(node);
			if (success) job.endJob();
		}
	}
	
	@FXML 
	protected void showNormalMode() {
		showNormalMode.set(!showNormalMode.get());
	}
	
	@FXML 
	protected void showAlarms() {
		Point p = MouseInfo.getPointerInfo().getLocation();
		StageLoader stage = new StageLoader("journals/JournalAlarms.xml", 
				SingleObject.getResourceBundle().getString("key_miJAlarms"), p, true);
		
		JAlarmsController controller = (JAlarmsController) stage.getController();
		controller.setAlarmById(true);
		
	    stage.show();
	}

	public void updateLabel(Timestamp dt) {
		if (dt == null) {
			Platform.runLater(() -> lLastDate.setText(""));
		} else {
			if (lLastDate.getText().length() == 0) {
				Platform.runLater(() -> lLastDate.setText(df.format(dt)));
			} else {
				try {
					if (dt.compareTo(df.parse(lLastDate.getText())) > 0) {
						Platform.runLater(() -> lLastDate.setText(df.format(dt)));
					}
				} catch (ParseException e) {
					LogFiles.log.log(Level.SEVERE, "void updateLabel(Timestamp dt)", e);
				}
			}
		}
	}
	
	@FXML 
	protected void fitVertical(ActionEvent event) {
		Group root = SingleObject.mainScheme.getRoot();
		double k = MainStage.controller.getBpScheme().getHeight() * 0.99 / root.getBoundsInLocal().getHeight();
		changeZoom(k);
	}
	
	@FXML 
	protected void fitHorizontal(ActionEvent event) {
		Group root = SingleObject.mainScheme.getRoot();
		double k = MainStage.controller.getBpScheme().getWidth() * 0.99 / root.getBoundsInLocal().getWidth();
		changeZoom(k);
	}
	
	@FXML 
	protected void showChart(ActionEvent event) {
		if (SingleObject.selectedShape == null) {
			return;
		}
		StageLoader stage = new StageLoader("Data.xml", SingleObject.getResourceBundle().getString("keyDataTitle"), true);
		DataController dataController = (DataController) stage.getController();
		List<Integer> idSignals = new ArrayList<>();
		idSignals.add(SingleObject.selectedShape.getIdSignal());
		dataController.setIdSignals(idSignals);
		
		stage.getScene().setOnDragOver(e -> {
			if (e.getGestureSource() != stage.getScene() && e.getDragboard().hasString()) {
                e.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
			e.consume();
		});
		
		stage.getScene().setOnDragDropped(e -> {
            Dragboard db = e.getDragboard();
            int id = Integer.parseInt(db.getString());
            
            Platform.runLater(() -> dataController.addData(id));
            
            boolean success = false;
            success = db.hasString();
            e.setDropCompleted(success);
            
            event.consume();
		});
		
	    stage.show();
	}
	
	@Override
	public void setElementText(ResourceBundle rb) {
		lDataOn.setText(rb.getString("keyDataOn"));
		stateReading1 = rb.getString("key_stateReading1");
		stateReading2 = rb.getString("key_stateReading2");
		stateReading3 = rb.getString("key_stateReading3");
		
		tbMain.getItems().filtered(f -> f.getClass().equals(Button.class) && f.getId() != null)
			.forEach(it -> ((Button)it).setTooltip(new Tooltip(rb.getString("keyTooltip_" + it.getId()))));
		
		if (navigatorStage != null) navigatorStage.setTitle(rb.getString("keyTooltip_navigator"));
	}

	public ToolBar getTbMain() {
		return tbMain;
	}

	public Button getHideLeft() {
		return hideLeft;
	}
	
	public Button getFit() {
		return fit;
	}
	
	public Button getNext() {
		return next;
	}

	public Button getPrevious() {
		return previous;
	}
	
	private class ShowChangeListener implements ChangeListener<Boolean> {
		private Button btn;
		private Stage stage;
		private Point2D statePoint;
		
		public ShowChangeListener(Button btn, Stage stage) {
			this.stage = stage;
			this.btn = btn;
		}

		@Override
		public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
			if (newValue) {
				btn.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.THIN)));
				if (statePoint != null) {
					stage.setX(statePoint.getX());
					stage.setY(statePoint.getY());
				}
				stage.show();
			} else {
				statePoint = new Point2D(stage.getX(), stage.getY());
				stage.hide();
				btn.setBorder(new Border(new BorderStroke(Color.TRANSPARENT, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.THIN)));
			}
		}
	}
}
