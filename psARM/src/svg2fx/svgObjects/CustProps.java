package svg2fx.svgObjects;

import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class CustProps {
	@XmlElement(name="cp", namespace="http://schemas.microsoft.com/visio/2003/SVGExtensions/")
	private List<CP> customProps;

	public List<CP> getCustomProps() {
		return customProps;
	}

	public void setCustomProps(List<CP> customProps) {
		this.customProps = customProps;
	}
	
	public CP getCPbyName(String name) {
		List<CP> ls = customProps.stream().filter(f -> f.getLbl().equals(name)).collect(Collectors.toList());
		if (ls.size() > 0) {
			return ls.get(0);
		}
		return null;
	}
}
