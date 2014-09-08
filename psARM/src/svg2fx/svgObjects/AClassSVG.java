package svg2fx.svgObjects;

import java.util.StringTokenizer;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public abstract class AClassSVG {
	@XmlAttribute(name="class")
	private String clazz;
	@XmlAttribute(name="style")
	private String style;
	
	public double startX;
	public double startY;
	public double endX;
	public double endY;
	
	public String getClazz() {
		return clazz;
	}
	
	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	abstract public Node getNode(SVG svg);
	
	public Node setStyles(Shape sh, String styles, SVG svg) {
		if (styles == null) return sh;
		Marker marker = null;
		
		sh = shapeWithStyle(sh, styles);
		Group ret = new Group();
		ret.getChildren().add(sh);
		
		StringTokenizer st = new StringTokenizer(styles, ";");
		while (st.hasMoreElements()) {
			String[] command = st.nextElement().toString().toLowerCase().split(":");
			switch (command[0]) {
			case "marker-start":
			case "marker-end":
				String urlM = command[1];
				urlM = urlM.substring(urlM.indexOf("#") + 1, urlM.indexOf(")"));
				marker = svg.getDefs().getMarkerById(urlM);
				Node markerFX = getMarkerFX(marker, svg, command[0].toLowerCase().equals("marker-start"), sh.getStrokeWidth());
				
				ret.getChildren().add(markerFX);
				break;
			default:
				//System.out.println(command[0]);
				break;
			}			
		}
		
		return ret;
	}
	
	private Node getMarkerFX(Marker marker, SVG svg, boolean isStart, double markerUnits) {
		Use u = marker.getUse();
		G gM = svg.getDefs().getGById(u.getHref().substring(1));
		Shape gr = (Shape) gM.getNode(svg);
		
		double strokeWidth = gr.getStrokeWidth();
		Paint oldFill = gr.getFill();
		gr = shapeWithStyle(gr, svg.getStyleByName(marker.getClazz()));
		
		if (oldFill.equals(Color.TRANSPARENT)) {
			gr.setFill(oldFill);
		}
		
		if (strokeWidth == 0) {
			gr.setStrokeWidth(strokeWidth);
			gr.setFill(gr.getStroke());
		}

		if (isStart) {
			gr.getTransforms().add(new Translate(startX, startY));
		} else {
			gr.getTransforms().add(new Translate(endX, endY));
		}
		
		if (marker.getMarkerUnits().toLowerCase().equals("strokewidth")) {
			gr.getTransforms().add(new Scale(markerUnits, markerUnits));
		}
		gr.getTransforms().add(new Translate(-marker.getRefX(), -marker.getRefY()));
		gr = (Shape) transformed(gr, u.getTransform());
		
		return gr;
	}
	
	public void setStartEndXY(String d) {
		String[] ds = d.split(" ");
		try {
			startX = Double.parseDouble(ds[1]);
			startY = Double.parseDouble(ds[2]);
		} catch (NumberFormatException e1) {
			startX = Double.parseDouble(ds[0].substring(1));
			startY = Double.parseDouble(ds[1]);
		}
		int last = ds.length - 1;
		if (ds[last].toLowerCase().equals("z")) {
			try {
				endX = Double.parseDouble(ds[last - 2]);
			} catch (NumberFormatException e) {
				endX = Double.parseDouble(ds[last - 2].substring(1));
			}
			endY = Double.parseDouble(ds[last - 1]);
		} else {
			try {
				endX = Double.parseDouble(ds[last - 1]);
			} catch (NumberFormatException e) {
				endX = Double.parseDouble(ds[last - 1].substring(1));
			}
			endY = Double.parseDouble(ds[last]);
		}
	}
	
	public Shape shapeWithStyle(Shape sh, String styles) {
		if (styles == null) return sh;

		boolean isFiled = false;
		StringTokenizer st = new StringTokenizer(styles, ";");
		while (st.hasMoreElements()) {
			String[] command = st.nextElement().toString().toLowerCase().split(":");
			switch (command[0]) {
			case "stroke":
				if (!command[1].equals("none")) {
					sh.setStroke(Color.web(command[1]));
				} else {
					sh.setStrokeWidth(0);
				}
				break;
			case "stroke-width":
				sh.setStrokeWidth(Double.parseDouble(command[1]));
				break;
			case "stroke-linecap":
				sh.setStrokeLineCap(StrokeLineCap.valueOf(command[1].toUpperCase()));
				break;
			case "stroke-linejoin":
				sh.setStrokeLineJoin(StrokeLineJoin.valueOf(command[1].toUpperCase()));
				break;
			case "stroke-dasharray":
				double d = 0;
				StringTokenizer da = new StringTokenizer(command[1], ",");
				while (da.hasMoreElements()) {
					d = Double.parseDouble(da.nextElement().toString());
					sh.getStrokeDashArray().add(d);
				}				
				break;
			case "fill":
				if (command[1].equals("none")) {
					sh.setFill(Color.TRANSPARENT);
				} else {
					sh.setFill(Color.web(command[1]));
				}
				isFiled = true;
				break;
			}
		}
		if (!isFiled) sh.setFill(Color.TRANSPARENT);
		return sh;
	}
	
	public Node transformed(Node n, String str) {
		if (str == null) return n;
		StringTokenizer items = new StringTokenizer(str);
		
		while (items.hasMoreElements()) {
			String operation = items.nextToken();
			String command = operation.substring(operation.indexOf("(") + 1, operation.indexOf(")"));		
			String[] commands = command.split(",");
			switch (operation.substring(0, operation.indexOf("(")).toLowerCase()) {
			case "translate":		
				if (commands.length > 1) {
					n.getTransforms().add(new Translate(Double.parseDouble(commands[0]), Double.parseDouble(commands[1])));
				} else {
					n.getTransforms().add(new Translate(Double.parseDouble(commands[0]), Double.parseDouble(commands[0])));
				}
				break;
			case "rotate":
				if (commands.length > 1) {
					n.getTransforms().add(new Rotate(Double.parseDouble(commands[0]), Double.parseDouble(commands[1]), Double.parseDouble(commands[2])));
				} else {
					n.getTransforms().add(new Rotate(Double.parseDouble(commands[0])));
				}
				break;
			case "scale":
				if (commands.length > 1) {
					n.getTransforms().add(new Scale(Double.parseDouble(commands[0]), Double.parseDouble(commands[1])));
				} else {
					n.getTransforms().add(new Scale(Double.parseDouble(commands[0]), Double.parseDouble(commands[0])));
				}
				break;
			default:
				System.out.println(operation);
				break;
			}
		}
		return n;
	}
}
