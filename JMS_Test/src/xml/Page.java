package xml;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Page {
	@XmlAttribute(name="name") 
	private String name;
	@XmlAttribute(name="width")
	private double width;
	@XmlAttribute(name="height")
	private double height;
	@XmlAttribute(name="fillColor")
	private String fillColor;
	@XmlElement(name="shape") 
	private List<ShapeX> shapes;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
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
	
	public String getFillColor() {
		return fillColor;
	}
	
	public void setFillColor(String fillColor) {
		this.fillColor = fillColor;
	}
	
	public List<ShapeX> getShapes() {
		return shapes;
	}
	
	public void setShapes(List<ShapeX> shapes) {
		this.shapes = shapes;
	}	
}
