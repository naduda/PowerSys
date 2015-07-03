package pr.svgObjects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class TextRect {
	@XmlAttribute(name="cx")
	private Double cx;
	@XmlAttribute(name="cy")
	private Double cy;
	@XmlAttribute(name="width")
	private Double width;
	@XmlAttribute(name="height")
	private Double height;
	
	public double getCx() {
		return cx;
	}
	
	public void setCx(double cx) {
		this.cx = cx;
	}
	
	public double getCy() {
		return cy;
	}
	
	public void setCy(double cy) {
		this.cy = cy;
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
}