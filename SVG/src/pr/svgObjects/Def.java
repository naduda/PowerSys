package pr.svgObjects;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlAccessorType(XmlAccessType.FIELD)
public class Def {
	@XmlAttribute(name = "id")
	private String id;
	@XmlElement(name="g", namespace=INamespaces.SVG)
	private List<G> lg;
	@XmlElement(name="marker", namespace=INamespaces.SVG)
	private List<Marker> markers;
	@XmlElement(name="linearGradient")
	private List<LinearGradient> linearGradients;
	@XmlElement(name="radialGradient")
	private List<RadialGradient> radialGradients;
	@XmlElement(name="pattern")
	private List<Pattern> patterns;
	@XmlTransient
	private Map<String, LinearGradient> linearGradientsMap;
	@XmlTransient
	private Map<String, RadialGradient> radialGradientsMap;
	@XmlTransient
	private Map<String, Pattern> patternsMap;
	
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

	public List<LinearGradient> getLinearGradients() {
		return linearGradients;
	}

	public void setLinearGradients(List<LinearGradient> linearGradients) {
		this.linearGradients = linearGradients;
	}
	
	private Map<String, LinearGradient> getLinearGradientsMap() {
		if (linearGradientsMap == null) {
			linearGradientsMap = new HashMap<>();
			if (linearGradients != null) linearGradients.forEach(g -> linearGradientsMap.put(g.getId(), g));
		}
		return linearGradientsMap;
	}
	
	public LinearGradient getLinearGradientById(String id) {
		return getLinearGradientsMap().get(id);
	}
	
	private Map<String, RadialGradient> getRadialGradientsMap() {
		if (radialGradientsMap == null) {
			radialGradientsMap = new HashMap<>();
			if (radialGradients != null) radialGradients.forEach(g -> radialGradientsMap.put(g.getId(), g));
		}
		return radialGradientsMap;
	}
	
	public RadialGradient getRadialGradientById(String id) {
		return getRadialGradientsMap().get(id);
	}
	
	private Map<String, Pattern> getPatternsMap() {
		if (patternsMap == null) {
			patternsMap = new HashMap<>();
			patterns.forEach(g -> patternsMap.put(g.getId(), g));
		}
		return patternsMap;
	}
	
	public Pattern getPatternById(String id) {
		return getPatternsMap().get(id);
	}

	public List<RadialGradient> getRadialGradients() {
		return radialGradients;
	}

	public void setRadialGradients(List<RadialGradient> radialGradients) {
		this.radialGradients = radialGradients;
	}
}