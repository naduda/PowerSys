package state;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class HotKeyClass {
	@XmlAttribute(name = "id")
	private String idCode;
	@XmlAttribute(name = "alt")
	private boolean isAlt;
	@XmlAttribute(name = "ctrl")
	private boolean isCtrl;
	@XmlAttribute(name = "shift")
	private boolean isShift;
	@XmlAttribute(name = "code")
	private String code;
	
	public HotKeyClass() {
		
	}
	
	public HotKeyClass(String idCode, boolean isAlt, boolean isCtrl, boolean isShift, String code) {
		setAlt(isAlt);
		setCtrl(isCtrl);
		setShift(isShift);
		setCode(code);
		setIdCode(idCode);
	}

	public boolean isAlt() {
		return isAlt;
	}

	public void setAlt(boolean isAlt) {
		this.isAlt = isAlt;
	}

	public boolean isCtrl() {
		return isCtrl;
	}

	public void setCtrl(boolean isCtrl) {
		this.isCtrl = isCtrl;
	}

	public boolean isShift() {
		return isShift;
	}

	public void setShift(boolean isShift) {
		this.isShift = isShift;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getIdCode() {
		return idCode;
	}

	public void setIdCode(String idCode) {
		this.idCode = idCode;
	}
}
