package pr.svgObjects;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class UserDefs {
	@XmlElement(name="ud", namespace=INamespaces.VISIO)
	List<Ud> ud;
}