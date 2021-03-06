package pr.model;

import java.io.Serializable;

public class TViewParam implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private int userref;
	private String objdenom;
	private String objref;
	private int alarmref;
	private String paramdenom;
	private String paramval;
	
	public TViewParam() {
		
	}

	public int getUserref() {
		return userref;
	}

	public void setUserref(int userref) {
		this.userref = userref;
	}

	public String getObjdenom() {
		return objdenom;
	}

	public void setObjdenom(String objdenom) {
		this.objdenom = objdenom;
	}

	public String getObjref() {
		return objref;
	}

	public void setObjref(String objref) {
		this.objref = objref;
	}

	public int getAlarmref() {
		return alarmref;
	}

	public void setAlarmref(int alarmref) {
		this.alarmref = alarmref;
	}

	public String getParamdenom() {
		return paramdenom;
	}

	public void setParamdenom(String paramdenom) {
		this.paramdenom = paramdenom;
	}

	public String getParamval() {
		return paramval;
	}

	public void setParamval(String paramval) {
		this.paramval = paramval;
	}
}
