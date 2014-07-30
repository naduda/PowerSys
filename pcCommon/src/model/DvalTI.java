package model;

import java.io.Serializable;
import java.sql.Timestamp;

public class DvalTI implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Timestamp dt;
	private int signalref;
	private double val;
	private Timestamp servdt;
	private int rcode;
	private boolean actualData = true;
	
	public DvalTI() {

	}

	public Timestamp getDt() {
		return dt;
	}
	
	public void setDt(Timestamp dt) {
		this.dt = dt;
	}
	
	public int getSignalref() {
		return signalref;
	}
	
	public void setSignalref(int signalref) {
		this.signalref = signalref;
	}
	
	public double getVal() {
		return val;
	}
	
	public void setVal(double val) {
		this.val = val;
	}
	
	public Timestamp getServdt() {
		return servdt;
	}
	
	public void setServdt(Timestamp servdt) {
		this.servdt = servdt;
	}
	
	public int getRcode() {
		return rcode;
	}
	
	public void setRcode(int rcode) {
		this.rcode = rcode;
	}

	public boolean isActualData() {
		return actualData;
	}

	public void setActualData(boolean actualData) {
		this.actualData = actualData;
	}
}
