package pr.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class TtranspHistory implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private int trref;
	private int infotype;
	private Timestamp tm;
	private int userref;
	private String txt;
	private int trtype;
	
	public TtranspHistory() {
		
	}

	public int getTrref() {
		return trref;
	}

	public void setTrref(int trref) {
		this.trref = trref;
	}

	public int getInfotype() {
		return infotype;
	}

	public void setInfotype(int infotype) {
		this.infotype = infotype;
	}

	public Timestamp getTm() {
		return tm;
	}

	public void setTm(Timestamp tm) {
		this.tm = tm;
	}

	public int getUserref() {
		return userref;
	}

	public void setUserref(int userref) {
		this.userref = userref;
	}

	public String getTxt() {
		return txt;
	}

	public void setTxt(String txt) {
		this.txt = txt;
	}

	public int getTrtype() {
		return trtype;
	}

	public void setTrtype(int trtype) {
		this.trtype = trtype;
	}
}
