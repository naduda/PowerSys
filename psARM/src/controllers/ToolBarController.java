package controllers;

import java.awt.MouseInfo;
import java.awt.Point;
import java.io.File;
import java.net.URL;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;

import javafx.print.*;
import javafx.scene.Node;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.FileChooser;
import pr.common.Utils;
import pr.common.WMF2PNG;
import pr.log.LogFiles;
import pr.model.NormalModeJournalItem;
import controllers.interfaces.IControllerInit;
import controllers.interfaces.StageLoader;
import controllers.journals.JAlarmsController;
import single.SingleFromDB;
import single.SingleObject;
import svg2fx.fxObjects.EShape;
import ui.MainStage;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

public class ToolBarController implements Initializable, IControllerInit {
	private final DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	
	private static final BooleanProperty showInfoProperty = new SimpleBooleanProperty();
	private static final BooleanProperty showNormalMode = new SimpleBooleanProperty();
	public static final DoubleProperty zoomProperty = new SimpleDoubleProperty();
	
	private static final double ZOOM_FACTOR = 0.1;
	private static final double ZOOM_MAX = 5;
	private static StageLoader infoStage;
	private static InfoController infoController;
	private Point2D infoStagePos;
	
	private final List<String> normalModeShapes = new ArrayList<>();
	
	@FXML private ToolBar tbMain;
	@FXML private Slider zoomSlider; 
	@FXML private Label lDataOn;
	@FXML private Label lLastDate;
	@FXML private Button info;
	@FXML private Button normalMode;
	
	@Override
	public void initialize(URL url, ResourceBundle boundle) {
		setElementText(SingleObject.getResourceBundle());
		zoomSlider.valueProperty().bindBidirectional(zoomProperty);
		zoomSlider.setMin(ZOOM_FACTOR);
		zoomSlider.setMax(ZOOM_MAX);
		zoomSlider.setBlockIncrement(ZOOM_FACTOR);
		zoomSlider.valueProperty().addListener((observ, oldValue, newValue) ->
				zoomSlider.setTooltip(new Tooltip(newValue + ""))
		);
		zoomProperty.addListener((observ, oldValue, newValue) -> changeZoom((double)newValue));
		
		showInfoProperty.addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				info.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.THIN)));
				if (infoStagePos != null) {
					infoStage.setX(infoStagePos.getX());
					infoStage.setY(infoStagePos.getY());
				}
				infoStage.show();
			} else {
				infoStagePos = new Point2D(infoStage.getX(), infoStage.getY());
				infoStage.hide();
				info.setBorder(new Border(new BorderStroke(Color.TRANSPARENT, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.THIN)));
			}
		});
		
		showNormalMode.addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				try {
					Timestamp dtBeg = Timestamp.valueOf(LocalDate.now().atTime(0, 0, 0));
					Timestamp dtEnd = Timestamp.valueOf(LocalDate.now().plusDays(1).atTime(0, 0));
					
					List<String> signalsArr = new ArrayList<>(1);
					signalsArr.add("");
					SingleObject.mainScheme.getSignalsTS().forEach(s -> signalsArr.set(0, signalsArr.get(0) + s + ","));
					String signals = signalsArr.get(0).substring(0, signalsArr.get(0).length() - 1);
					
					String query = String.format("select path, nameSignal, idsignal, val, dt, (select min(dt) from d_arcvalts "
							+ "where signalref = idsignal and dt > d.dt and val <> d.val) dt_new "
							+ "from (select idSignal, signalpath(idSignal) as path, nameSignal, "
							+ "coalesce(getval_ts(idSignal, '%s'::timestamp with time zone), baseval) as val, "
							+ "getdt_ts(idSignal, '%s'::timestamp with time zone) as dt, baseval from t_signal s "
							+ "where idSignal in (0, %s) union all select idSignal, signalpath(idSignal) as path, nameSignal, val, dt, baseval "
							+ "from d_arcvalts join t_signal on signalref = idsignal "
							+ "where idSignal in (0, %s) and dt > '%s' and dt < '%s') d where val <> baseval order by dt desc",
							dtBeg, dtBeg, signals, signals, dtBeg, dtEnd);
					
					List<NormalModeJournalItem> items = SingleFromDB.psClient.getListNormalModeItems(query);
					items.forEach(it -> {
						if (it.getDt_new() == null) {
							SingleObject.mainScheme.getListSignals().stream().filter(f -> f.getId() == it.getIdsignal()).forEach(s -> {
								try {
									EShape tt = SingleObject.mainScheme.getDeviceById(s.getVal().toString());
									tt.setNormalMode();
									normalModeShapes.add(s.getVal().toString());
								} catch (Exception e) {
									e.printStackTrace();
								}
							});
						}
					});
				} catch (RemoteException e) {
					LogFiles.log.log(Level.SEVERE, "void initialize(...)", e);
				}
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
	
	private void changeZoom(double zoom) {
		Group root = SingleObject.mainScheme.getRoot();
		root.setScaleX(zoom);
        root.setScaleY(zoom);
	}
	
	@FXML 
	protected void exitButtonAction() {
		Controller.exitProgram();
	}
	
	@FXML 
	protected void info() {
		if (infoStage == null) {
			infoStage = new StageLoader("Info.xml", SingleObject.getResourceBundle().getString("keyTooltip_info"), true);
			infoStage.setOnCloseRequest(e -> {
				infoStagePos = new Point2D(infoStage.getX(), infoStage.getY());
				showInfoProperty.set(false);
			});
			infoController = (InfoController) infoStage.getController();
		}
		//infoController.updateStage();
		showInfoProperty.set(!showInfoProperty.get());
	}

	@FXML
	protected void save(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extentionFilter = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png");
		fileChooser.getExtensionFilters().add(extentionFilter);
		fileChooser.setTitle(SingleObject.getResourceBundle().getString("keyTooltip_save"));
		File file = fileChooser.showSaveDialog(SingleObject.mainStage);
		if (file != null) {
			WMF2PNG.svg2png(SingleObject.schemeInputStream,
					Math.max(Float.parseFloat(SingleObject.svg.getHeight()), Float.parseFloat(SingleObject.svg.getWidth())),
					file.getAbsolutePath());
		}
	}

	@FXML
	protected void print(ActionEvent event) {
		Node node = SingleObject.mainScheme.getContent();
		System.out.println("============================");
		Printer.getAllPrinters().forEach(System.out::println);
		Printer printer = Printer.getDefaultPrinter();
		System.out.println("***********************");
		PageLayout pageLayout = printer.createPageLayout(Paper.A4, PageOrientation.LANDSCAPE, Printer.MarginType.DEFAULT);
		double scaleX = pageLayout.getPrintableWidth() / node.getBoundsInLocal().getWidth();
		double scaleY = pageLayout.getPrintableHeight() / node.getBoundsInLocal().getHeight();
		System.out.println(node.getBoundsInParent());
		System.out.println(node.getBoundsInLocal());
		scaleX = scaleX < scaleY ? scaleX : scaleY;
//		scaleY = node.getScaleX();
		node.getTransforms().add(new Scale(scaleX, scaleX));
		node.getTransforms().add(new Translate(-node.getBoundsInParent().getMinX(), -node.getBoundsInParent().getMinY()));
		System.out.println(node.getBoundsInParent());
		System.out.println(node.getBoundsInLocal());
		PrinterJob job = PrinterJob.createPrinterJob();
		if (job.showPrintDialog(SingleObject.mainStage)) {
			boolean success = job.printPage(node);
			if (success) {
				job.endJob();
			}
		}
//		node.setScaleX(scaleY);
//		node.setScaleY(scaleY);
		node.getTransforms().clear();
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
					if (dt.compareTo(df.parse(lLastDate.getText())) > 0)
						Platform.runLater(() -> lLastDate.setText(df.format(dt)));
				} catch (ParseException e) {
					e.printStackTrace();
					LogFiles.log.log(Level.SEVERE, "void updateLabel(Timestamp dt)", e);
				}
			}
		}
	}
	
	@FXML 
	protected void fitVertical(ActionEvent event) {
		Group root = SingleObject.mainScheme.getRoot();
		double k = MainStage.controller.getBpScheme().getHeight() * 0.99 / root.getBoundsInLocal().getHeight();
		root.setScaleY(k);
		root.setScaleX(k);
		zoomProperty.set(k);
	}
	
	@FXML 
	protected void fitHorizontal(ActionEvent event) {
		Group root = SingleObject.mainScheme.getRoot();
		double k = MainStage.controller.getBpScheme().getWidth() * 0.99 / root.getBoundsInLocal().getWidth();
		root.setScaleY(k);
		root.setScaleX(k);
		zoomProperty.set(k);
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
		if (infoController != null ) infoController.setElementText(rb);
		
		tbMain.getItems().filtered(f -> f.getClass().equals(Button.class)).forEach(it -> {
			if (it.getId() != null) {
				((Button)it).setTooltip(new Tooltip(rb.getString("keyTooltip_" + it.getId())));
				File icon = new File(Utils.getFullPath("./Icon/" + it.getId() + ".png"));
				if (icon.exists()) {
					ImageView iw = new ImageView("file:/" + icon.getAbsolutePath());
					iw.setFitHeight(SingleObject.getProgramSettings().getIconWidth());
					iw.setPreserveRatio(true);
					((Button)it).setGraphic(iw);
				}
			}
		});
	}

	public ToolBar getTbMain() {
		return tbMain;
	}
	
	
}
