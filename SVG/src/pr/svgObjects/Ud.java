package pr.svgObjects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class Ud {
	@XmlAttribute(name="nameU", namespace="http://schemas.microsoft.com/visio/2003/SVGExtensions/")
	private String nameU;
	@XmlAttribute(name="prompt", namespace="http://schemas.microsoft.com/visio/2003/SVGExtensions/")
	private String prompt;
	@XmlAttribute(name="val", namespace="http://schemas.microsoft.com/visio/2003/SVGExtensions/")
	private String val;
}
