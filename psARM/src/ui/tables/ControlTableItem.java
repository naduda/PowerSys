package ui.tables;

import java.text.SimpleDateFormat;

import pr.model.ControlJournalItem;
import javafx.beans.property.SimpleStringProperty;

public class ControlTableItem {

	private final SimpleStringProperty pDate;
	private final SimpleStringProperty pScheme;
	private final SimpleStringProperty pSignal;
	private final SimpleStringProperty pValue;
	private final SimpleStringProperty pState;
	private final SimpleStringProperty pProcessTime;
	private final SimpleStringProperty pUser;
	
	public ControlTableItem(ControlJournalItem cji) {
		SimpleDateFormat dFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
		pDate = new SimpleStringProperty(cji.getDt() != null ? dFormat.format(cji.getDt()) : "");
		pScheme = new SimpleStringProperty(cji.getSchemename() != null ? cji.getSchemename() : "");
		pSignal = new SimpleStringProperty(cji.getNamesignal() != null ? cji.getNamesignal() : "");
		pValue = new SimpleStringProperty(cji.getVal() != null ? cji.getVal() : "");
		pState = new SimpleStringProperty(cji.getStatus() != null ? cji.getStatus() : "");
		pProcessTime = new SimpleStringProperty(cji.getDuration() != null ? cji.getDuration() : "");
		pUser = new SimpleStringProperty(cji.getFio() != null ? cji.getFio() : "");
	}

	public String getPDate() {
		return pDate.get();
	}

	public void setPDate(String dt) {
		this.pDate.set(dt);
	}

	public String getPScheme() {
		return pScheme.get();
	}

	public void setPScheme(String schemename) {
		this.pScheme.set(schemename);
	}

	public String getPSignal() {
		return pSignal.get();
	}

	public void setPSignal(String namesignal) {
		this.pSignal.set(namesignal);
	}

	public String getPValue() {
		return pValue.get();
	}

	public void setPValue(String val) {
		this.pValue.set(val);
	}

	public String getPState() {
		return pState.get();
	}

	public void setPState(String status) {
		this.pState.set(status);
	}

	public String getPProcessTime() {
		return pProcessTime.get();
	}

	public void setPProcessTime(String duration) {
		this.pProcessTime.set(duration);
	}

	public String getPUser() {
		return pUser.get();
	}

	public void setPUser(String fio) {
		this.pUser.set(fio);
	}
}
