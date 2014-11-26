package ui.data;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import pr.model.LinkedValue;
import pr.model.VsignalView;
import single.SingleFromDB;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.StringConverter;

public class LineChartContainer extends StackPane {
	private static final double VIDSTUP = 0.05;

	private static final int MAX_COUNT_POINTS = 1000;

	private final Rectangle zoomRect = new Rectangle(0, 0, Color.LIGHTSEAGREEN.deriveColor(0, 1, 1, 0.5));
	
	private final DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
	private final DecimalFormat decimalFormat = new DecimalFormat("0.000", decimalFormatSymbols);
	
	private XYChart.Series<Number, Number> series = new XYChart.Series<>();
	private final NumberAxis xAxis = new NumberAxis(0, 0, 0);
	private final NumberAxis yAxis = new NumberAxis(0, 0, 0);
	private final LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
	private List<LinkedValue> dataListAll;
	
	private List<Integer> idSignals;
	
	private LineChartContainer() {
		decimalFormatSymbols.setDecimalSeparator('.');
		decimalFormatSymbols.setGroupingSeparator(' ');
		
		lineChart.getData().add(series);
		//lineChart.setCreateSymbols(false);
		getChildren().add(lineChart);
		
		zoomRect.setManaged(false);
		getChildren().add(zoomRect);
        
		setUpZooming(zoomRect, lineChart);
	}
	
	public LineChartContainer(String titleChart, String xLabel, List<LinkedValue> dataList, List<Integer> idSignals) {
		this();
		this.idSignals = idSignals;
		
		lineChart.setTitle(titleChart);
		xAxis.setLabel(xLabel);
		
		setData(dataList);
	}
	
	public void setData(List<LinkedValue> dataList) {
		dataListAll = dataList;		
		setData1000(dataList, true);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void setData1000(List<LinkedValue> dataList, boolean isChangeBounds) {
		lineChart.getData().clear();
		xLowerBound = -1;
		xUpperBound = -1;
	    yLowerBound = -1;
	    yUpperBound = -1;
	    
	    List<LinkedValue> dataSeries = new ArrayList<>();
		idSignals.forEach(idSignal -> {
			series = new XYChart.Series<>();
			
			VsignalView signal = SingleFromDB.signals.get(idSignal);
			series.setName(signal.getNamesignal());
			yAxis.setLabel("Value, " + signal.getNameunit());
			List<LinkedValue> dataSignal = dataList.stream().filter(f -> f.getId() == idSignal).collect(Collectors.toList());
			
			int koef = dataSignal.size() > MAX_COUNT_POINTS ? dataList.size() / MAX_COUNT_POINTS : 1;
			
			List<LinkedValue> dataList1000 = new ArrayList<>();
			dataList1000.add(dataSignal.get(0));
			
			while (dataList1000.size() * koef < dataSignal.size()) {
				dataList1000.add(dataSignal.get(dataList1000.size() * koef));
			}
			
			List<LinkedValue> data = new ArrayList<>();
	        LinkedValue prevLV = null;
	        for (LinkedValue linkedValue : dataList1000) {
				if (prevLV != null) {
					data.add(new LinkedValue(linkedValue.getDt(), prevLV.getVal()));
				}
				prevLV = linkedValue;
				data.add(linkedValue);
			}
			
			data.forEach(d -> {
	        	Timestamp ts = (Timestamp) d.getDt();
	        	series.getData().add(new XYChart.Data(ts.getTime(), d.getVal()));
	        });
			
			lineChart.getData().add(series);
			dataSeries.addAll(dataList1000);
		});
		
		setToolTips();
		if (isChangeBounds) setChartBounds(dataSeries);
	}
	
	private double xLowerBound = -1;
	private double xUpperBound = -1;
    private double yLowerBound = -1;
    private double yUpperBound = -1;
	private void setChartBounds(List<LinkedValue> dataList) {
		double xLowerBoundNew = dataList.stream().mapToDouble(e -> ((Timestamp)e.getDt()).getTime()).min().getAsDouble();
		double xUpperBoundNew = dataList.stream().mapToDouble(e -> ((Timestamp)e.getDt()).getTime()).max().getAsDouble();
		
        double delta = xUpperBoundNew - xLowerBoundNew;
        xLowerBoundNew = xLowerBoundNew - delta * VIDSTUP;
        xUpperBoundNew = xUpperBoundNew + delta * VIDSTUP;
        xLowerBound = xLowerBound == -1 ? xLowerBoundNew : xLowerBound < xLowerBoundNew ? xLowerBound : xLowerBoundNew;
        xUpperBound = xUpperBound == -1 ? xUpperBoundNew : xUpperBound > xUpperBoundNew ? xUpperBound : xUpperBoundNew;
        double yLowerBoundNew = dataList.stream().mapToDouble(e -> (Double)e.getVal()).min().getAsDouble();
        double yUpperBoundNew = dataList.stream().mapToDouble(e -> (Double)e.getVal()).max().getAsDouble();
        
        delta = yUpperBoundNew - yLowerBoundNew;
        yLowerBoundNew = delta == 0 ? yLowerBoundNew * 0.995 : yLowerBoundNew - delta * VIDSTUP;
        yUpperBoundNew = delta == 0 ? yUpperBoundNew * 1.005 : yUpperBoundNew + delta * VIDSTUP;
        yLowerBound = yLowerBound == -1 ? yLowerBoundNew : yLowerBound < yLowerBoundNew ? yLowerBound : yLowerBoundNew;
        yUpperBound = yUpperBound == -1 ? yUpperBoundNew : yUpperBound > yUpperBoundNew ? yUpperBound : yUpperBoundNew;
        
        xAxis.setLowerBound(xLowerBound);
        xAxis.setUpperBound(xUpperBound);
        xAxis.setTickUnit((xUpperBound - xLowerBound) / 10);
        
        yAxis.setLowerBound(yLowerBound);
        yAxis.setUpperBound(yUpperBound);
        yAxis.setTickUnit((yUpperBound - yLowerBound) / 10);
        
        xAxis.setTickLabelFormatter(new StringConverter<Number>() {			
			@Override
			public String toString(Number num) {
				return new SimpleDateFormat("  HH:mm:ss   \ndd.MM.yyyy").format(num);
			}
			
			@Override
			public Number fromString(String str) {
				return null;
			}
		});
        
	}
	
	private void setToolTips() {
		lineChart.getData().forEach(s ->
			s.getData().forEach(d -> {
				String textDate = new SimpleDateFormat("HH:mm:ss").format(d.getXValue());
        		String textValue = decimalFormat.format(d.getYValue());

        		Tooltip.install(d.getNode(), new Tooltip(textDate + " - " + textValue));
			})
		);
	}
	
	private void setUpZooming(final Rectangle rect, final Node zoomingNode) {
        final ObjectProperty<Point2D> mouseAnchor = new SimpleObjectProperty<>();
        
        zoomingNode.setOnMousePressed(event -> {
            mouseAnchor.set(new Point2D(event.getX(), event.getY()));
            rect.setWidth(0);
            rect.setHeight(0);
        });
        
        zoomingNode.setOnMouseDragged(event -> {
            double x = event.getX();
            double y = event.getY();
            
            rect.setTranslateX(Math.min(x, mouseAnchor.get().getX()));
            rect.setTranslateY(Math.min(y, mouseAnchor.get().getY()));
            rect.setWidth(Math.abs(x - mouseAnchor.get().getX()));
            rect.setHeight(Math.abs(y - mouseAnchor.get().getY()));
        });
        
        zoomingNode.setOnMouseReleased(event -> doZoom(rect, zoomingNode));
        
        zoomingNode.setOnMouseClicked(event -> {
        	if (event.getClickCount() == 2) {
        		setData1000(dataListAll, true);
            }
        });
    }
	
	private void doZoom(Rectangle zoomRect, Node chartNode) {
		final BooleanBinding disableControls = zoomRect.widthProperty().lessThan(5).or(zoomRect.heightProperty().lessThan(5));
		final Point2D xAxisInScene = xAxis.localToScene(0, 0);
		final Bounds rectInScene = zoomRect.localToScene(zoomRect.getBoundsInLocal());
        
        double xOffset = rectInScene.getMinX() - xAxisInScene.getX();
        double yOffset = rectInScene.getMaxY() - xAxisInScene.getY();
        double xAxisScale = xAxis.getScale();
        double yAxisScale = yAxis.getScale();
        
        double beg = xAxis.getLowerBound() + xOffset / xAxisScale;
        double end = xOffset / xAxisScale + xAxis.getLowerBound() + zoomRect.getWidth() / xAxisScale;
        
        List<LinkedValue> visibleData = dataListAll.stream().
        		filter(f -> ((Timestamp)f.getDt()).getTime() > beg && ((Timestamp)f.getDt()).getTime() < end).
        		collect(Collectors.toList());
               
        if (!disableControls.get()) {
        	setData1000(visibleData, false);
        	
        	xAxis.setLowerBound(xAxis.getLowerBound() + xOffset / xAxisScale);
	        xAxis.setUpperBound(xAxis.getLowerBound() + zoomRect.getWidth() / xAxisScale);
        	yAxis.setLowerBound(yAxis.getLowerBound() + yOffset / yAxisScale);
	        yAxis.setUpperBound(yAxis.getLowerBound() - zoomRect.getHeight() / yAxisScale);
	        
	        xAxis.setTickUnit((xAxis.getUpperBound() - xAxis.getLowerBound()) / 10);
	        yAxis.setTickUnit((yAxis.getUpperBound() - yAxis.getLowerBound()) / 10);
        }
        
        zoomRect.setWidth(0);
        zoomRect.setHeight(0);
    }
}
