package pr.svgObjects;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Def {
	@XmlAttribute(name = "id")
	private String id;
	@XmlElement(name="g", namespace="http://www.w3.org/2000/svg")
	List<G> lg;
	@XmlElement(name="marker", namespace="http://www.w3.org/2000/svg")
	List<Marker> markers;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public List<G> getLg() {
		return lg;
	}
	
	public void setLg(List<G> lg) {
		this.lg = lg;
	}
	
	public List<Marker> getMarkers() {
		return markers;
	}
	
	public void setMarkers(List<Marker> markers) {
		this.markers = markers;
	}
	
	public Marker getMarkerById(String id) {
		return markers.stream().filter(f -> f.getId().equals(id)).findFirst().get();
	}
	
	public G getGById(String id) {
		return lg.stream().filter(f -> f.getId().equals(id)).findFirst().get();
	}
}
