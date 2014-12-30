package pr.model;

import java.io.Serializable;

public class SpTuCommand implements Serializable {
	private static final long serialVersionUID = 1L;

	private int objref;
	private int val;
	private String txt;
	private int istu;
	private String denom;
	
	public SpTuCommand() {
		
	}

	public int getObjref() {
		return objref;
	}

	public void setObjref(int objref) {
		this.objref = objref;
	}

	public int getVal() {
		return val;
	}

	public void setVal(int val) {
		this.val = val;
	}

	public String getTxt() {
		return txt;
	}

	public void setTxt(String txt) {
		this.txt = txt;
	}

	public int getIstu() {
		return istu;
	}

	public void setIstu(int istu) {
		this.istu = istu;
	}

	public String getDenom() {
		return denom;
	}

	public void setDenom(String denom) {
		this.denom = denom;
	}

	@Override
	public String toString() {
		return getTxt();
	}
}
