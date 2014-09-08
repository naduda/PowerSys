package svg2fx.svgObjects;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlMixed;

import com.sun.org.apache.xerces.internal.dom.ElementNSImpl;

public class TextSVG extends AClassSVG {
	private static final double ONE_PT = 12;
	
	@XmlAttribute(name="x")
	private Double x;
	@XmlAttribute(name="y")
	private Double y;
	@XmlMixed
	@XmlAnyElement(lax=false)
	private List<Object> mixedValue = new ArrayList<>();
	private List<Object> values = new ArrayList<>();
	
	public Double getX() {
		return x;
	}
	
	public void setX(Double x) {
		this.x = x;
	}
	
	public Double getY() {
		return y;
	}
	
	public void setY(Double y) {
		this.y = y;
	}

	public List<Object> getMixedValue() {
		return mixedValue;
	}

	public void setMixedValue(List<Object> mixedValue) {
		this.mixedValue = mixedValue;
	}

	public List<Object> getValues() {
		mixedValue.forEach(e -> {
			if (e instanceof ElementNSImpl) {
				ElementNSImpl eee = (ElementNSImpl) e;
				if (eee.getLocalName().toLowerCase().equals("tspan")) {
					try {
						JAXBContext context = JAXBContext.newInstance(Tspan.class);
						values.add(context.createUnmarshaller().unmarshal((org.w3c.dom.Node)e));
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			} else if (e instanceof String) {
				values.add(e);
			}
		});
		
		return values;
	}

	@Override
	public Node getNode(SVG svg) {
		Group g = new Group();
		String styles = svg.getStyleByName(getClazz());
		Text t = new Text();
		boolean isLastTspan = false;
		
		double curX = x;
		double curY = y;
		for (Object v : getValues()) {
			if (v instanceof String) {
				t = new Text(curX, curY, (String) v);
				g.getChildren().add(textWithStyles(t, styles));
				isLastTspan = false;
			} else if (v instanceof Tspan) {
				Tspan ts = (Tspan) v;
				double tx = ts.getX() != null ? ts.getX() : isLastTspan ? ts.getDxPix(curX) : ts.getDxPix(curX) + t.getBoundsInLocal().getWidth();
				double ty = ts.getY() != null ? ts.getY() : ts.getDyPix(curY);
				t = new Text(tx, ty, ts.getValue());
				g.getChildren().add(textWithStyles(t, styles));
				curX = tx + t.getBoundsInLocal().getWidth();
				curY = ty;
				isLastTspan = true;
			}
		}
		return g;
	}
	
	private Text textWithStyles(Text t, String styles) {
		String fontName = "Arial";
		double fontSize = 8;
		FontPosture fontPosture = FontPosture.REGULAR;
		FontWeight fontWeight = FontWeight.NORMAL;
		
		StringTokenizer st = new StringTokenizer(styles, ";");
		while (st.hasMoreElements()) {
			String[] command = st.nextElement().toString().split(":");
			switch (command[0].toLowerCase()) {
				case "fill": t.setFill(Color.web(command[1])); break;
				case "font-family": fontName = command[1]; break;
				case "font-size": 
					fontSize = Double.parseDouble(command[1].substring(0, command[1].indexOf("em"))) * ONE_PT; 
					break;
				case "font-style": 
					if ("italic".equals(command[1])) {
						fontPosture = FontPosture.ITALIC;
					}
					break;
				case "font-weight": 
					if ("bold".equals(command[1])) {
						fontWeight = FontWeight.BOLD;
					}
					break;
			}
		}
		Font f = Font.font(fontName, fontWeight, fontPosture, fontSize);
		t.setFont(f);
		return t;
	}
	
}
