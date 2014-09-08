package svg2fx.svgObjects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class Use {
	@XmlAttribute(name="href", namespace="http://www.w3.org/1999/xlink")
	private String href;
	@XmlAttribute(name="transform")
	private String transform;
	
	public String getHref() {
		return href;
	}
	
	public void setHref(String href) {
		this.href = href;
	}
	
	public String getTransform() {
		return transform;
	}
	
	public void setTransform(String transform) {
		this.transform = transform;
	}
}
