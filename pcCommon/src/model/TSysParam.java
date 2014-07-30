package model;

public class TSysParam {

	private int paramtype;
	private String paramname;
	private String val;
	private String paramdescr;
	
	public TSysParam() {
		
	}

	public int getParamtype() {
		return paramtype;
	}

	public void setParamtype(int paramtype) {
		this.paramtype = paramtype;
	}

	public String getParamname() {
		return paramname;
	}

	public void setParamname(String paramname) {
		this.paramname = paramname;
	}

	public String getVal() {
		return val;
	}

	public void setVal(String val) {
		this.val = val;
	}

	public String getParamdescr() {
		return paramdescr;
	}

	public void setParamdescr(String paramdescr) {
		this.paramdescr = paramdescr;
	}
}
