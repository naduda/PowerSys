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
	@XmlAttribute(name="prompt", namespace="http://schemas.microsoft.com/visio/2003/SVGExtensions/")
	private String prompt;
	@XmlAttribute(name="type", namespace="http://schemas.microsoft.com/visio/2003/SVGExtensions/")
	private Integer type;
	@XmlAttribute(name="format", namespace="http://schemas.microsoft.com/visio/2003/SVGExtensions/")
	private String format;
	@XmlAttribute(name="sortKey", namespace="http://schemas.microsoft.com/visio/2003/SVGExtensions/")
	private String sortKey;
	@XmlAttribute(name="invis", namespace="http://schemas.microsoft.com/visio/2003/SVGExtensions/")
	private boolean invis;
	@XmlAttribute(name="ask", namespace="http://schemas.microsoft.com/visio/2003/SVGExtensions/")
	private boolean ask;
	@XmlAttribute(name="langID", namespace="http://schemas.microsoft.com/visio/2003/SVGExtensions/")
	private String langID;
	@XmlAttribute(name="cal", namespace="http://schemas.microsoft.com/visio/2003/SVGExtensions/")
	private String cal;	
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
		switch(type) {
		case 2: this.val = "VT0(" + val + ")";
			break;
		default :
			this.val = "VT4(" + val + ")";
			break;	
		}
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

	public String getPrompt() {
		return prompt;
	}

	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getSortKey() {
		return sortKey;
	}

	public void setSortKey(String sortKey) {
		this.sortKey = sortKey;
	}

	public boolean isInvis() {
		return invis;
	}

	public void setInvis(boolean invis) {
		this.invis = invis;
	}

	public boolean isAsk() {
		return ask;
	}

	public void setAsk(boolean ask) {
		this.ask = ask;
	}

	public String getLangID() {
		return langID;
	}

	public void setLangID(String langID) {
		this.langID = langID;
	}

	public String getCal() {
		return cal;
	}

	public void setCal(String cal) {
		this.cal = cal;
	}
}
