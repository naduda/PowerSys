package ui.data;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import pr.model.LinkedValue;
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
	
	private final XYChart.Series<Number, Number> series = new XYChart.Series<>();
	private final NumberAxis xAxis = new NumberAxis(0, 0, 0);
	private final NumberAxis yAxis = new NumberAxis(0, 0, 0);
	private final LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
	private List<LinkedValue> dataListAll;
	
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
	
	public LineChartContainer(String titleChart, String titleLegend, String xLabel, String yLabel, List<LinkedValue> dataList) {
		this();
		
		series.setName(titleLegend);
		xAxis.setLabel(xLabel);
		yAxis.setLabel(yLabel);
		lineChart.setTitle(titleChart);
        
        setData(dataList);
	}
	
	public void setData(List<LinkedValue> dataList) {
		dataListAll = dataList;		
		setData1000(dataList);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void setData1000(List<LinkedValue> dataList) {
		series.getData().clear();
		
		int koef = dataList.size() > MAX_COUNT_POINTS ? (int) dataList.size() / MAX_COUNT_POINTS : 1;
		
		List<LinkedValue> dataList1000 = new ArrayList<LinkedValue>();
		dataList1000.add(dataList.get(0));
		
		while (dataList1000.size() * koef < dataList.size()) {
			dataList1000.add(dataList.get(dataList1000.size() * koef));
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
		
		setChartBounds(dataList1000);
		setToolTips();
	}
	
	private void setChartBounds(List<LinkedValue> dataList) {
		double xLowerBound = dataList.stream().mapToDouble(e -> ((Timestamp)e.getDt()).getTime()).min().getAsDouble();
		double xUpperBound = dataList.stream().mapToDouble(e -> ((Timestamp)e.getDt()).getTime()).max().getAsDouble();
        double delta = xUpperBound - xLowerBound;
        xLowerBound = xLowerBound - delta * VIDSTUP;
        xUpperBound = xUpperBound + delta * VIDSTUP;
        double yLowerBound = dataList.stream().mapToDouble(e -> (Double)e.getVal()).min().getAsDouble();
        double yUpperBound = dataList.stream().mapToDouble(e -> (Double)e.getVal()).max().getAsDouble();
        delta = yUpperBound - yLowerBound;
        yLowerBound = delta == 0 ? yLowerBound * 0.995 : yLowerBound - delta * VIDSTUP;
        yUpperBound = delta == 0 ? yUpperBound * 1.005 : yUpperBound + delta * VIDSTUP;
        
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
		lineChart.getData().forEach(s -> {
			List<Boolean> bool = new ArrayList<>();
			bool.add(true);
			s.getData().forEach(d -> {
				String textDate = new SimpleDateFormat("HH:mm:ss").format(d.getXValue());
        		String textValue = decimalFormat.format(d.getYValue());
        		
        		if (bool.get(0)) {
        			Tooltip.install(d.getNode(), new Tooltip(textDate + " - " + textValue));
        		}
        		boolean b = !bool.get(0);
        		bool.clear();
        		bool.add(b);
			});
		});
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
        
        zoomingNode.setOnMouseReleased(event -> {
        	doZoom(rect, zoomingNode);
        });
        
        zoomingNode.setOnMouseClicked(event -> {
        	if (event.getClickCount() == 2) {
        		setData1000(dataListAll);
            }
        });
    }
	
	private void doZoom(Rectangle zoomRect, Node chartNode) {
        Point2D xAxisInScene = xAxis.localToScene(0, 0);
        
        Bounds rectInScene = zoomRect.localToScene(zoomRect.getBoundsInLocal());
        double xOffset = rectInScene.getMinX() - xAxisInScene.getX();
        double xAxisScale = xAxis.getScale();
        
        double beg = xAxis.getLowerBound() + xOffset / xAxisScale;
        double end = xOffset / xAxisScale + xAxis.getLowerBound() + zoomRect.getWidth() / xAxisScale;
        
        List<LinkedValue> visibleData = dataListAll.stream().
        		filter(f -> ((Timestamp)f.getDt()).getTime() > beg && ((Timestamp)f.getDt()).getTime() < end).
        		collect(Collectors.toList());
        
        final BooleanBinding disableControls = zoomRect.widthProperty().lessThan(5).or(zoomRect.heightProperty().lessThan(5));
        if (!disableControls.get()) {
        	setData1000(visibleData);
        }
        
        zoomRect.setWidth(0);
        zoomRect.setHeight(0);        
    }
}
