package pr.svgObjects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class TextBlock {
	@XmlAttribute(name="margins", namespace=INamespaces.VISIO)
	private String margins;
	@XmlAttribute(name="tabSpace", namespace=INamespaces.VISIO)
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