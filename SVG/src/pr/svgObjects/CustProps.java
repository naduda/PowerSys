package pr.svgObjects;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class CustProps {
	@XmlElement(name="cp", namespace=INamespaces.VISIO)
	private List<CP> customProps;

	public List<CP> getCustomProps() {
		return customProps;
	}

	public void setCustomProps(List<CP> customProps) {
		this.customProps = customProps;
	}
	
	public CP getCPbyName(String name) {
		if (customProps == null || name == null) return null;
//		Optional<CP> ls = customProps.stream().filter(f -> f.getLbl().equals(name)).findFirst();
		Optional<CP> ls = customProps.stream().filter(f -> f.getNameU().equals(name)).findFirst();
		if (ls.isPresent()) {
			try {
				return ls.get();
			} catch (NoSuchElementException e) {
				System.out.println(ls);
			}
		}
		return null;
	}
}