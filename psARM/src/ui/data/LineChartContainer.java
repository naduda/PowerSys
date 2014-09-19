package ui.data;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import model.LinkedValue;
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
	final Rectangle zoomRect = new Rectangle(0, 0, Color.LIGHTSEAGREEN.deriveColor(0, 1, 1, 0.5));
		
	private LineChart<Number, Number> lineChart;
	private double xLowerBound;
	private double xUpperBound;
	private double yLowerBound;
	private double yUpperBound;
	
	public LineChartContainer(String titleChart, String titleLegend, String xLabel, String yLabel, List<LinkedValue> dataList) {
		final XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(titleLegend);
        
        List<LinkedValue> data = new ArrayList<>();
        LinkedValue prevLV = null;
        for (LinkedValue linkedValue : dataList) {
			if (prevLV != null) {
				data.add(new LinkedValue(linkedValue.getDt(), prevLV.getVal()));
			}
			prevLV = linkedValue;
			data.add(linkedValue);
		}
        
        setData(series, data);
        
        xLowerBound = data.stream().mapToDouble(e -> ((Timestamp)e.getDt()).getTime()).min().getAsDouble();
        xUpperBound = data.stream().mapToDouble(e -> ((Timestamp)e.getDt()).getTime()).max().getAsDouble();
        double delta = xUpperBound - xLowerBound;
        xLowerBound = xLowerBound - delta * 0.01;
        xUpperBound = xUpperBound + delta * 0.01;
        yLowerBound = data.stream().mapToDouble(e -> (Double)e.getVal()).min().getAsDouble();
        yUpperBound = data.stream().mapToDouble(e -> (Double)e.getVal()).max().getAsDouble();
        delta = yUpperBound - yLowerBound;
        yLowerBound = delta == 0 ? yLowerBound * 0.995 : yLowerBound - delta * 0.01;
        yUpperBound = delta == 0 ? yUpperBound * 1.005 : yUpperBound + delta * 0.01;
        
        final NumberAxis yAxis = new NumberAxis(yLowerBound, yUpperBound, (yUpperBound - yLowerBound) / 10);
        yAxis.setLabel(yLabel);
        final NumberAxis xAxis = new NumberAxis(xLowerBound, xUpperBound, (xUpperBound - xLowerBound) / 10);
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
        xAxis.setLabel(xLabel);
                
        lineChart = new LineChart<>(xAxis, yAxis);                
        lineChart.setTitle(titleChart);
        //lineChart.setCreateSymbols(false);
        
        lineChart.getData().add(series);        
        getChildren().add(lineChart);
				
		zoomRect.setManaged(false);
		getChildren().add(zoomRect);
        
		setUpZooming(zoomRect, lineChart);
		
		
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
		decimalFormatSymbols.setDecimalSeparator('.');
		decimalFormatSymbols.setGroupingSeparator(' ');		
		DecimalFormat decimalFormat = new DecimalFormat("0.000", decimalFormatSymbols);
		
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
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void setData(XYChart.Series<Number, Number> series, List<LinkedValue> data) {
		data.forEach(d -> {
        	Timestamp ts = (Timestamp) d.getDt();
        	series.getData().add(new XYChart.Data(ts.getTime(), d.getVal()));
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
        		final NumberAxis xAxis = (NumberAxis)lineChart.getXAxis();
                xAxis.setLowerBound(xLowerBound);
                xAxis.setUpperBound(xUpperBound);
                final NumberAxis yAxis = (NumberAxis)lineChart.getYAxis();
                yAxis.setLowerBound(yLowerBound);
                yAxis.setUpperBound(yUpperBound);
                
                xAxis.setTickUnit((xAxis.getUpperBound() - xAxis.getLowerBound()) / 10);
                yAxis.setTickUnit((yAxis.getUpperBound() - yAxis.getLowerBound()) / 10);
                
                zoomRect.setWidth(0);
                zoomRect.setHeight(0);
            }
        });
    }
	
	@SuppressWarnings("unchecked")
	private void doZoom(Rectangle zoomRect, Node chartNode) {
		LineChart<Number, Number> chart = (LineChart<Number, Number>) chartNode;
        
        final NumberAxis yAxis = (NumberAxis) chart.getYAxis();
        final NumberAxis xAxis = (NumberAxis) chart.getXAxis();
        Point2D xAxisInScene = xAxis.localToScene(0, 0);
        
        Bounds rectInScene = zoomRect.localToScene(zoomRect.getBoundsInLocal());
        double xOffset = rectInScene.getMinX() - xAxisInScene.getX();
        double yOffset = rectInScene.getMaxY() - xAxisInScene.getY();
        
        double xAxisScale = xAxis.getScale();
        double yAxisScale = yAxis.getScale();
        
        final BooleanBinding disableControls = zoomRect.widthProperty().lessThan(5).or(zoomRect.heightProperty().lessThan(5));
        if (!disableControls.get()) {
	        xAxis.setLowerBound(xAxis.getLowerBound() + xOffset / xAxisScale);
	        xAxis.setUpperBound(xAxis.getLowerBound() + zoomRect.getWidth() / xAxisScale);	        
	        yAxis.setLowerBound(yAxis.getLowerBound() + yOffset / yAxisScale);
	        yAxis.setUpperBound(yAxis.getLowerBound() - zoomRect.getHeight() / yAxisScale);
        }
        
        xAxis.setTickUnit((xAxis.getUpperBound() - xAxis.getLowerBound()) / 10);
        yAxis.setTickUnit((yAxis.getUpperBound() - yAxis.getLowerBound()) / 10);
        
        zoomRect.setWidth(0);
        zoomRect.setHeight(0);        
    }
}
