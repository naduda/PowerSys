package pr.svgObjects;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class DocumentProperties {
	@XmlElement(name="userDefs", namespace=INamespaces.VISIO)
	private UserDefs userDefs;
	@XmlAttribute(name="langID", namespace=INamespaces.VISIO)
	private String langID;
	@XmlAttribute(name="metric", namespace=INamespaces.VISIO)
	private Boolean metric;
	@XmlAttribute(name="viewMarkup", namespace=INamespaces.VISIO)
	private Boolean viewMarkup;
}