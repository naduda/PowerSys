package pr.svgObjects;

import javax.xml.bind.annotation.XmlAttribute;

public class PageProperties {
	@XmlAttribute(name="width")
	private Double width;
	@XmlAttribute(name="height")
	private Double height;
	@XmlAttribute(name="drawingScale", namespace="http://schemas.microsoft.com/visio/2003/SVGExtensions/")
	private Double drawingScale;
	@XmlAttribute(name="pageScale", namespace="http://schemas.microsoft.com/visio/2003/SVGExtensions/")
	private Double pageScale;
	@XmlAttribute(name="drawingUnits", namespace="http://schemas.microsoft.com/visio/2003/SVGExtensions/")
	private Double drawingUnits;
	@XmlAttribute(name="shadowOffsetX", namespace="http://schemas.microsoft.com/visio/2003/SVGExtensions/")
	private Double shadowOffsetX;
	@XmlAttribute(name="shadowOffsetY", namespace="http://schemas.microsoft.com/visio/2003/SVGExtensions/")
	private Double shadowOffsetY;
}
