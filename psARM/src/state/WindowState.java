package state;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "windowState")
@XmlAccessorType(XmlAccessType.FIELD)
public class WindowState {
	@XmlAttribute(name="x")
	private double x;
	@XmlAttribute(name="y")
	private double y;
	@XmlAttribute(name="width")
	private double width;
	@XmlAttribute(name="height")
	private double height;
	
	private double alarmDividerPositions;
	private double treeDividerPositions;
	
	public WindowState() {
		
	}
	
	public WindowState(double x, double y, double width, double height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
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

	public double getAlarmDividerPositions() {
		return alarmDividerPositions;
	}

	public void setAlarmDividerPositions(double alarmDividerPositions) {
		this.alarmDividerPositions = alarmDividerPositions;
	}

	public double getTreeDividerPositions() {
		return treeDividerPositions;
	}

	public void setTreeDividerPositions(double treeDividerPositions) {
		this.treeDividerPositions = treeDividerPositions;
	}
} 