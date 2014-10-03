package pr.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Ttransparant implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private int idtr;
	private int signref;
	private int tp;
	private Timestamp settime;
	private Timestamp lastupdate;
	private Timestamp closetime;
	private String objname;
	private int schemeref;
	
	public Ttransparant() {
		
	}

	public int getIdtr() {
		return idtr;
	}

	public void setIdtr(int idtr) {
		this.idtr = idtr;
	}

	public int getSignref() {
		return signref;
	}

	public void setSignref(int signref) {
		this.signref = signref;
	}

	public int getTp() {
		return tp;
	}

	public void setTp(int tp) {
		this.tp = tp;
	}

	public Timestamp getSettime() {
		return settime;
	}

	public void setSettime(Timestamp settime) {
		this.settime = settime;
	}

	public Timestamp getLastupdate() {
		return lastupdate;
	}

	public void setLastupdate(Timestamp lastupdate) {
		this.lastupdate = lastupdate;
	}

	public Timestamp getClosetime() {
		return closetime;
	}

	public void setClosetime(Timestamp closetime) {
		this.closetime = closetime;
	}

	public String getObjname() {
		return objname;
	}

	public void setObjname(String objname) {
		this.objname = objname;
	}

	public int getSchemeref() {
		return schemeref;
	}

	public void setSchemeref(int schemeref) {
		this.schemeref = schemeref;
	}
}
