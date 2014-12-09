package pr.svgObjects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class CP {
	@XmlAttribute(name="nameU", namespace="http://schemas.microsoft.com/visio/2003/SVGExtensions/")
	private String nameU;
	@XmlAttribute(name="lbl", namespace="http://schemas.microsoft.com/visio/2003/SVGExtensions/")
	private String lbl;
	@XmlAttribute(name="val", namespace="http://schemas.microsoft.com/visio/2003/SVGExtensions/")
	private String val;
	@XmlAttribute(name="signals", namespace="http://schemas.microsoft.com/visio/2003/SVGExtensions/")
	private String signals;
	@XmlAttribute(name="id", namespace="http://schemas.microsoft.com/visio/2003/SVGExtensions/")
	private String id;
	@XmlAttribute(name="idTS", namespace="http://schemas.microsoft.com/visio/2003/SVGExtensions/")
	private String idTS;
	@XmlAttribute(name="Precision", namespace="http://schemas.microsoft.com/visio/2003/SVGExtensions/")
	private String precision;
	
	@Override
	public String toString() {
		return getVal();
	}

	public String getNameU() {
		return nameU;
	}

	public void setNameU(String nameU) {
		this.nameU = nameU;
	}

	public String getLbl() {
		return lbl;
	}

	public void setLbl(String lbl) {
		this.lbl = lbl;
	}

	public String getVal() {
		if (val != null && val.toLowerCase().startsWith("vt")) {
			return val.substring(val.indexOf("(") + 1, val.indexOf(")"));
		}
		return val;
	}

	public void setVal(String val) {
		this.val = val;
	}

	public String getSignals() {
		if (signals != null && signals.toLowerCase().startsWith("vt")) {
			return signals.substring(signals.indexOf("(") + 1, signals.indexOf(")"));
		}
		return signals;
	}

	public void setSignals(String signals) {
		this.signals = signals;
	}

	public String getId() {
		if (id != null && id.toLowerCase().startsWith("vt")) {
			return id.substring(id.indexOf("(") + 1, id.indexOf(")"));
		}
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIdTS() {
		if (idTS != null && idTS.toLowerCase().startsWith("vt")) {
			return idTS.substring(idTS.indexOf("(") + 1, idTS.indexOf(")"));
		}
		return idTS;
	}

	public void setIdTS(String idTS) {
		this.idTS = idTS;
	}

	public String getPrecision() {
		if (precision != null && precision.toLowerCase().startsWith("vt")) {
			return precision.substring(precision.indexOf("(") + 1, precision.indexOf(")"));
		}
		return precision;
	}

	public void setPrecision(String precision) {
		this.precision = precision;
	}
}
