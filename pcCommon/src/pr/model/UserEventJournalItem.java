package pr.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class UserEventJournalItem implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Timestamp eventdt;
	private String app;
	private String fio;
	private String descr;
	
	public UserEventJournalItem() {
		
	}

	public Timestamp getEventdt() {
		return eventdt;
	}

	public void setEventdt(Timestamp eventdt) {
		this.eventdt = eventdt;
	}

	public String getApp() {
		return app;
	}

	public void setApp(String app) {
		this.app = app;
	}

	public String getFio() {
		return fio;
	}

	public void setFio(String fio) {
		this.fio = fio;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}
}
