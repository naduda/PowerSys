package pr.model;

import java.io.Serializable;

public class TalarmParam implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private int alarmref;
	private String paramdenom;
	private String paramval;
	
	public TalarmParam() {
		
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
