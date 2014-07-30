package model;

import java.io.Serializable;
import java.util.Date;

public class DvalTS extends DvalTI implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private int userref;
	private Date statedt;
	private int schemeref;
	
	public DvalTS() {

	}

	public int getUserref() {
		return userref;
	}
	
	public void setUserref(int userref) {
		this.userref = userref;
	}
	
	public Date getStatedt() {
		return statedt;
	}
	
	public void setStatedt(Date statedt) {
		this.statedt = statedt;
	}
	
	public int getSchemeref() {
		return schemeref;
	}
	
	public void setSchemeref(int schemeref) {
		this.schemeref = schemeref;
	}
}
