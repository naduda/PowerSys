package state;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "schemeSettings")
@XmlAccessorType(XmlAccessType.FIELD)
public class SchemeSettings {
	private String schemeName;
	private int idScheme;
	private double schemeScale;
	
	public String getSchemeName() {
		return schemeName;
	}

	public void setSchemeName(String schemeName) {
		this.schemeName = schemeName;
	}

	public double getSchemeScale() {
		return schemeScale;
	}

	public void setSchemeScale(double schemeScale) {
		this.schemeScale = schemeScale;
	}

	public int getIdScheme() {
		return idScheme;
	}

	public void setIdScheme(int idScheme) {
		this.idScheme = idScheme;
	}
}
