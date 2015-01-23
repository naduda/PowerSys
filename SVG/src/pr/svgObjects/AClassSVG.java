package pr.svgObjects;

import java.util.StringTokenizer;
import java.util.logging.Level;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import pr.log.LogFiles;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

@XmlAccessorType(XmlAccessType.FIELD)
public abstract class AClassSVG {
	@XmlAttribute(name="class")
	private String clazz;
	@XmlAttribute(name="style")
	private String style;
	
	@XmlTransient
	public double startX;
	@XmlTransient
	public double startY;
	@XmlTransient
	public double endX;
	@XmlTransient
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
		
		sh = shapeWithStyle(sh, styles, svg.getDefs());
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
		gr = shapeWithStyle(gr, svg.getStyleByName(marker.getClazz()), svg.getDefs());
		
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
	
	public Shape shapeWithStyle(Shape sh, String styles, Def defs) {
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
				double d;
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
					try {
						if (command[1].startsWith("url")) {
							String gradName = command[1];
							gradName = gradName.substring(5, gradName.length() - 1);
							
							pr.svgObjects.LinearGradient lg = defs.getLinearGradientById(gradName);
							pr.svgObjects.RadialGradient rg = defs.getRadialGradientById(gradName);
							if (lg != null) {
								setGradientFill(sh, lg);
								isFiled = true;
								break;
							} else if (rg != null) {
								setGradientFill(sh, rg);
								isFiled = true;
								break;
							} else {
								Pattern pattern = defs.getPatternById(gradName);
								if (pattern != null) {
									String styleGr = pattern.getPaths().get(0).getStyle();
									styleGr = styleGr.substring(styleGr.indexOf("#") + 1, styleGr.indexOf(")"));
									lg = defs.getLinearGradientById(styleGr);
									if (lg.getX2() > 0) {
										double oldVal = lg.getY1();
										lg.setY1(lg.getX2());
										setGradientFill(sh, lg);
										lg.setY1(oldVal);
									} else {
										double oldVal = lg.getX2();
										lg.setX2(lg.getY1());
										setGradientFill(sh, lg);
										lg.setX2(oldVal);
									}
									isFiled = true;
									break;
								}
							}
						}
						
						sh.setFill(Color.web(command[1]));
					} catch (Exception e) {
						LogFiles.log.log(Level.WARNING, "Color " + command[1] + " cannot be setting ...");
						e.printStackTrace();
					}
				}
				isFiled = true;
				break;
			}
		}
		if (!isFiled) sh.setFill(Color.TRANSPARENT);
		return sh;
	}
	
	private void setGradientFill(Shape sh, Object obj) {
		if (obj instanceof pr.svgObjects.LinearGradient) {
			pr.svgObjects.LinearGradient lg = (pr.svgObjects.LinearGradient) obj;
		
			Stop[] stops = new Stop[lg.getStops().size()];
			
			for (int i = 0; i < lg.getStops().size(); i++) {
				String styleStop = lg.getStops().get(i).getStyle();
				String stopColor = styleStop.substring(styleStop.indexOf("stop-color:") + 11);
				stopColor = stopColor.substring(0, stopColor.indexOf(";"));
				String stopOpacity = styleStop.substring(styleStop.indexOf("stop-opacity:") + 13);
				double opacity = Double.parseDouble(stopOpacity);
				stops[i] = new Stop(lg.getStops().get(i).getOffset(), Color.web(stopColor, opacity));
			}
			
			LinearGradient lGrad = new LinearGradient(lg.getX1(), lg.getY1(), lg.getX2(), lg.getY2(), 
					true, CycleMethod.NO_CYCLE, stops);
			
			sh.setFill(lGrad);
		} else if (obj instanceof pr.svgObjects.RadialGradient) {
			pr.svgObjects.RadialGradient rg = (pr.svgObjects.RadialGradient) obj;
			
			Stop[] stops = new Stop[rg.getStops().size()];
			
			for (int i = 0; i < rg.getStops().size(); i++) {
				String styleStop = rg.getStops().get(i).getStyle();
				String stopColor = styleStop.substring(styleStop.indexOf("stop-color:") + 11);
				stopColor = stopColor.substring(0, stopColor.indexOf(";"));
				String stopOpacity = styleStop.substring(styleStop.indexOf("stop-opacity:") + 13);
				double opacity = Double.parseDouble(stopOpacity);
				stops[i] = new Stop(rg.getStops().get(i).getOffset(), Color.web(stopColor, opacity));
			}
			
			RadialGradient rGrad = new RadialGradient(0, 0.1, rg.getCx(), rg.getCy(), rg.getR(),
					true, CycleMethod.NO_CYCLE, stops);
			
			sh.setFill(rGrad);
		}
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
				LogFiles.log.log(Level.INFO, operation.substring(0, operation.indexOf("(")).toLowerCase());
				break;
			}
		}
		return n;
	}
}
