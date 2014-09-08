package svg2fx.svgObjects;

import javafx.scene.Node;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class Marker extends AClassSVG {
	@XmlAttribute(name="id")
	private String id;
	@XmlAttribute(name="refX")
	private Double refX = 0.0;
	@XmlAttribute(name="refY")
	private Double refY = 0.0;
	@XmlAttribute(name="orient")
	private String orient;
	@XmlAttribute(name="markerUnits")
	private String markerUnits;
	@XmlElement(name="use", namespace="http://www.w3.org/2000/svg")
	private Use use;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public Double getRefX() {
		return refX;
	}

	public void setRefX(Double refX) {
		this.refX = refX;
	}

	public Double getRefY() {
		return refY;
	}

	public void setRefY(Double refY) {
		this.refY = refY;
	}

	public Use getUse() {
		return use;
	}
	
	public String getOrient() {
		return orient;
	}

	public void setOrient(String orient) {
		this.orient = orient;
	}

	public String getMarkerUnits() {
		return markerUnits;
	}

	public void setMarkerUnits(String markerUnits) {
		this.markerUnits = markerUnits;
	}

	public void setUse(Use use) {
		this.use = use;
	}

	@Override
	public Node getNode(SVG svg) {
		// TODO Auto-generated method stub
		return null;
	}
}
