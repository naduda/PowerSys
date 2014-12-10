package pr.svgObjects;

import javax.xml.bind.annotation.XmlAttribute;

public class Layer {
	@XmlAttribute(name="name", namespace="http://schemas.microsoft.com/visio/2003/SVGExtensions/")
	private String name;
	@XmlAttribute(name="index", namespace="http://schemas.microsoft.com/visio/2003/SVGExtensions/")
	private Integer index;
}
