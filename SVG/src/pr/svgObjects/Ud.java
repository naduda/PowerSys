package pr.svgObjects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class Ud {
	@XmlAttribute(name="nameU", namespace=INamespaces.VISIO)
	private String nameU;
	@XmlAttribute(name="prompt", namespace=INamespaces.VISIO)
	private String prompt;
	@XmlAttribute(name="val", namespace=INamespaces.VISIO)
	private String val;
}