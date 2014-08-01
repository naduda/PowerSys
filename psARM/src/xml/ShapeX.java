package xml;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class ShapeX {
	@XmlAttribute(name="id")
	private String id;
	@XmlAttribute(name="type")
	private String type;
	@XmlAttribute(name="x")
	private double x;
	@XmlAttribute(name="y")
	private double y;
	@XmlAttribute(name="lineWeight")
	private double lineWeight;
	@XmlAttribute(name="lineColor")
	private String lineColor;
	@XmlAttribute(name="angle")
	private double angle;
	@XmlAttribute(name="zoomX")
	private double zoomX = 1;
	@XmlAttribute(name="zoomY")
	private double zoomY = 1;
	@XmlAttribute(name="signal")
	private String signal;
	@XmlAttribute(name="signalInfo")
	private String signalInfo;
	@XmlAttribute(name="width")
	private double width;
	@XmlAttribute(name="height")
	private double height;
	@XmlAttribute(name="flipX")
	private int flipX;
	@XmlAttribute(name="flipY")
	private int flipY;
	@XmlAttribute(name="startAng")
	private double startAng;
	@XmlAttribute(name="lenAng")
	private double lenAng;
	@XmlAttribute(name="text")
	private String text;
	@XmlAttribute(name="filled")
	private boolean filled;
	@XmlAttribute(name="fillColor")
	private String fillColor;
	@XmlAttribute(name="fontSize")
	private double fontSize;
	@XmlAttribute(name="precision")
	private String precision;	
	@XmlAttribute(name="linePattern")
	private int linePattern;

	@XmlElement(name="shape")
	private List<ShapeX> shapes;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
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
	
	public double getLineWeight() {
		return lineWeight;
	}
	
	public void setLineWeight(double lineWeight) {
		this.lineWeight = lineWeight;
	}
	
	public String getLineColor() {
		return lineColor;
	}
	
	public void setLineColor(String lineColor) {
		this.lineColor = lineColor;
	}
	
	public double getAngle() {
		return angle;
	}
	
	public void setAngle(double angle) {
		this.angle = angle;
	}
	
	public double getZoomX() {
		return zoomX;
	}
	
	public void setZoomX(double zoomX) {
		this.zoomX = zoomX;
	}
	
	public double getZoomY() {
		return zoomY;
	}
	
	public void setZoomY(double zoomY) {
		this.zoomY = zoomY;
	}
	
	public String getSignal() {
		return signal;
	}
	
	public void setSignal(String signal) {
		this.signal = signal;
	}
	
	public String getSignalInfo() {
		return signalInfo;
	}
	
	public void setSignalInfo(String signalInfo) {
		this.signalInfo = signalInfo;
	}
	
	public List<ShapeX> getShapes() {
		return shapes;
	}
	
	public void setShapes(List<ShapeX> shapes) {
		this.shapes = shapes;
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

	public int getFlipX() {
		return flipX;
	}

	public void setFlipX(int flipX) {
		this.flipX = flipX;
	}

	public int getFlipY() {
		return flipY;
	}

	public void setFlipY(int flipY) {
		this.flipY = flipY;
	}

	public double getStartAng() {
		return startAng;
	}

	public void setStartAng(double startAng) {
		this.startAng = startAng;
	}

	public double getLenAng() {
		return lenAng;
	}

	public void setLenAng(double lenAng) {
		this.lenAng = lenAng;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isFilled() {
		return filled;
	}

	public void setFilled(boolean filled) {
		this.filled = filled;
	}

	public String getFillColor() {
		return fillColor;
	}

	public void setFillColor(String fillColor) {
		this.fillColor = fillColor;
	}

	public double getFontSize() {
		return fontSize * 1.2;
	}

	public void setFontSize(double fontSize) {
		this.fontSize = fontSize;
	}

	public String getPrecision() {
		return precision;
	}

	public void setPrecision(String precision) {
		this.precision = precision;
	}

	public int getLinePattern() {
		return linePattern;
	}

	public void setLinePattern(int linePattern) {
		this.linePattern = linePattern;
	}
}
