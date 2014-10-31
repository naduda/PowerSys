package pr.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class SwitchEquipmentJournalItem implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String namesignal;
	private String txtval;
	private Timestamp dt;
	private int rcode;
	
	public SwitchEquipmentJournalItem() {
		
	}

	public String getNamesignal() {
		return namesignal;
	}

	public void setNamesignal(String namesignal) {
		this.namesignal = namesignal;
	}

	public String getTxtval() {
		return txtval;
	}

	public void setTxtval(String txtval) {
		this.txtval = txtval;
	}

	public Timestamp getDt() {
		return dt;
	}

	public void setDt(Timestamp dt) {
		this.dt = dt;
	}

	public int getRcode() {
		return rcode;
	}

	public void setRcode(int rcode) {
		this.rcode = rcode;
	}
}
