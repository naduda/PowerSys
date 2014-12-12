package pr.svgObjects;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Pattern {
	@XmlAttribute(name="id")
	private String id;
	@XmlAttribute(name="fillPattern", namespace="http://schemas.microsoft.com/visio/2003/SVGExtensions/")
	private String fillPattern;
	@XmlAttribute(name="foreground", namespace="http://schemas.microsoft.com/visio/2003/SVGExtensions/")
	private String foreground;
	@XmlAttribute(name="background", namespace="http://schemas.microsoft.com/visio/2003/SVGExtensions/")
	private String background;
	@XmlAttribute(name="x")
	private double x;
	@XmlAttribute(name="y")
	private double y;
	@XmlAttribute(name="width")
	private double width;
	@XmlAttribute(name="height")
	private double height;
	@XmlAttribute(name="patternContentUnits")
	private String patternContentUnits;
	@XmlElement(name="path")
	List<PathSVG> paths;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFillPattern() {
		return fillPattern;
	}

	public void setFillPattern(String fillPattern) {
		this.fillPattern = fillPattern;
	}

	public String getForeground() {
		return foreground;
	}

	public void setForeground(String foreground) {
		this.foreground = foreground;
	}

	public String getBackground() {
		return background;
	}

	public void setBackground(String background) {
		this.background = background;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public String getPatternContentUnits() {
		return patternContentUnits;
	}

	public void setPatternContentUnits(String patternContentUnits) {
		this.patternContentUnits = patternContentUnits;
	}

	public List<PathSVG> getPaths() {
		return paths;
	}

	public void setPaths(List<PathSVG> paths) {
		this.paths = paths;
	}
}
