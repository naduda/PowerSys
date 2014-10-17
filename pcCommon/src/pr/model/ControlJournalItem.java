package pr.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class ControlJournalItem implements Serializable {
	private static final long serialVersionUID = 1L;

	private Timestamp dt;
	private String schemename;
	private String namesignal;
	private String val;
	private String status;
	private String duration;
	private String fio;
	
	public ControlJournalItem() {
		
	}

	public Timestamp getDt() {
		return dt;
	}

	public void setDt(Timestamp dt) {
		this.dt = dt;
	}

	public String getSchemename() {
		return schemename;
	}

	public void setSchemename(String schemename) {
		this.schemename = schemename;
	}

	public String getNamesignal() {
		return namesignal;
	}

	public void setNamesignal(String namesignal) {
		this.namesignal = namesignal;
	}

	public String getVal() {
		return val;
	}

	public void setVal(String val) {
		this.val = val;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getFio() {
		return fio;
	}

	public void setFio(String fio) {
		this.fio = fio;
	}
}
