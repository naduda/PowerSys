package pr.svgObjects;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="textBlock", namespace="http://schemas.microsoft.com/visio/2003/SVGExtensions/")
public class TextBlock {
	@XmlAttribute(name="margins")
	private String margins;
	@XmlAttribute(name="tabSpace")
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
