package model;

import java.io.Serializable;

public class LinkedValue implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private int idsignal;
	private double val;
	
	public LinkedValue() {
		
	}

	public int getIdsignal() {
		return idsignal;
	}

	public void setIdsignal(int idsignal) {
		this.idsignal = idsignal;
	}

	public double getVal() {
		return val;
	}

	public void setVal(double val) {
		this.val = val;
	}

}
