package model;

import java.io.Serializable;

public class LinkedValue implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Object dt;
	private Object val;
	
	public LinkedValue() {
		
	}
	
	@Override
	public String toString() {
		return val.toString();
	}

	public LinkedValue(Object dt, Object val) {
		this.dt = dt;
		this.val = val;
	}

	public Object getDt() {
		return dt;
	}

	public void setDt(Object dt) {
		this.dt = dt;
	}

	public Object getVal() {
		return val;
	}

	public void setVal(Object val) {
		this.val = val;
	}
}
