package pr.svgObjects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class Stop {
	@XmlAttribute(name="offset")
	private Double offset;
	@XmlAttribute(name="style")
	private String style;
	
	public Double getOffset() {
		return offset;
	}
	
	public void setOffset(Double offset) {
		this.offset = offset;
	}
	
	public String getStyle() {
		return style;
	}
	
	public void setStyle(String style) {
		this.style = style;
	}
}