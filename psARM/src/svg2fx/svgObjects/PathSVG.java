package svg2fx.svgObjects;

import javafx.scene.Node;
import javafx.scene.shape.SVGPath;

import javax.xml.bind.annotation.XmlAttribute;

public class PathSVG extends AClassSVG {
	@XmlAttribute(name="d")
	private String d;
	
	public String getD() {
		return d;
	}
	
	@Override
	public Node getNode(SVG svg) {
		setStartEndXY(d);
		SVGPath path = new SVGPath();
		path.setContent(d);
		shapeWithStyle(path, getStyle());
		return setStyles(path, svg.getStyleByName(getClazz()), svg);
	}
}
