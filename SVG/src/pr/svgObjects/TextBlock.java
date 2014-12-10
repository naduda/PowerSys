package pr.svgObjects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class TextBlock {
	@XmlAttribute(name="margins", namespace="http://schemas.microsoft.com/visio/2003/SVGExtensions/")
	private String margins;
	@XmlAttribute(name="tabSpace", namespace="http://schemas.microsoft.com/visio/2003/SVGExtensions/")
	private String tabSpace;
	
	public String getMargins() {
		return margins;
	}
	
	public void setMargins(String margins) {
		this.margins = margins;
	}
	
	public String getTabSpace() {
		return tabSpace;
	}
	
	public void setTabSpace(String tabSpace) {
		this.tabSpace = tabSpace;
	}
}
