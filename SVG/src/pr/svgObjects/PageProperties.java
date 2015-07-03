package pr.svgObjects;

import javax.xml.bind.annotation.XmlAttribute;

public class PageProperties {
	@XmlAttribute(name="width")
	private Double width;
	@XmlAttribute(name="height")
	private Double height;
	@XmlAttribute(name="drawingScale", namespace=INamespaces.VISIO)
	private Double drawingScale;
	@XmlAttribute(name="pageScale", namespace=INamespaces.VISIO)
	private Double pageScale;
	@XmlAttribute(name="drawingUnits", namespace=INamespaces.VISIO)
	private Double drawingUnits;
	@XmlAttribute(name="shadowOffsetX", namespace=INamespaces.VISIO)
	private Double shadowOffsetX;
	@XmlAttribute(name="shadowOffsetY", namespace=INamespaces.VISIO)
	private Double shadowOffsetY;
}