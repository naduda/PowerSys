package pr.svgObjects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class Vparagraph {
	@XmlAttribute(name="horizAlign", namespace=INamespaces.VISIO)
	private Integer horizAlign;

	public Integer getHorizAlign() {
		return horizAlign;
	}
}