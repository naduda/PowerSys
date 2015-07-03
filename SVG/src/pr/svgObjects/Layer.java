package pr.svgObjects;

import javax.xml.bind.annotation.XmlAttribute;

public class Layer {
	@XmlAttribute(name="name", namespace=INamespaces.VISIO)
	private String name;
	@XmlAttribute(name="index", namespace=INamespaces.VISIO)
	private Integer index;
}