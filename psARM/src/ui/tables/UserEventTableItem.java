package ui.tables;

import java.text.SimpleDateFormat;

import pr.model.UserEventJournalItem;
import javafx.beans.property.SimpleStringProperty;

public class UserEventTableItem {
	private final SimpleDateFormat dFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
	
	private final SimpleStringProperty pDate;
	private final SimpleStringProperty pApplication;
	private final SimpleStringProperty pUser;
	private final SimpleStringProperty pEvent;
	
	public UserEventTableItem(UserEventJournalItem it) {
		pDate = new SimpleStringProperty(it.getEventdt() != null ? dFormat.format(it.getEventdt()) : "");
		pApplication = new SimpleStringProperty(it.getApp() != null ? it.getApp() : "");
		pUser = new SimpleStringProperty(it.getFio() != null ? it.getFio() : "Administrator");
		pEvent = new SimpleStringProperty(it.getDescr() != null ? it.getDescr() : "");
	}
	
	public String getPDate() {
		return pDate.get();
	}

	public void setPDate(String val) {
		this.pDate.set(val);
	}
	
	public String getPApplication() {
		return pApplication.get();
	}

	public void setPApplication(String val) {
		this.pApplication.set(val);
	}
	
	public String getPUser() {
		return pUser.get();
	}

	public void setPUser(String val) {
		this.pUser.set(val);
	}
	
	public String getPEvent() {
		return pEvent.get();
	}

	public void setPEvent(String val) {
		this.pEvent.set(val);
	}
}
