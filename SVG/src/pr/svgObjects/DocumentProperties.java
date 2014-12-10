package pr.svgObjects;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class DocumentProperties {
	@XmlElement(name="userDefs", namespace="http://schemas.microsoft.com/visio/2003/SVGExtensions/")
	private UserDefs userDefs;
	@XmlAttribute(name="langID", namespace="http://schemas.microsoft.com/visio/2003/SVGExtensions/")
	private String langID;
	@XmlAttribute(name="metric", namespace="http://schemas.microsoft.com/visio/2003/SVGExtensions/")
	private Boolean metric;
	@XmlAttribute(name="viewMarkup", namespace="http://schemas.microsoft.com/visio/2003/SVGExtensions/")
	private Boolean viewMarkup;
}
