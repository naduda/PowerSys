package pr.svgObjects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class CP {
	@XmlAttribute(name="nameU", namespace=INamespaces.VISIO)
	private String nameU;
	@XmlAttribute(name="lbl", namespace=INamespaces.VISIO)
	private String lbl;
	@XmlAttribute(name="val", namespace=INamespaces.VISIO)
	private String val;	
	@XmlAttribute(name="prompt", namespace=INamespaces.VISIO)
	private String prompt;
	@XmlAttribute(name="type", namespace=INamespaces.VISIO)
	private Integer type;
	@XmlAttribute(name="format", namespace=INamespaces.VISIO)
	private String format;
	@XmlAttribute(name="sortKey", namespace=INamespaces.VISIO)
	private String sortKey;
	@XmlAttribute(name="invis", namespace=INamespaces.VISIO)
	private boolean invis;
	@XmlAttribute(name="ask", namespace=INamespaces.VISIO)
	private boolean ask;
	@XmlAttribute(name="langID", namespace=INamespaces.VISIO)
	private String langID;
	@XmlAttribute(name="cal", namespace=INamespaces.VISIO)
	private String cal;	
	
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
