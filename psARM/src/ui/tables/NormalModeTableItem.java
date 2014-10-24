package ui.tables;

import java.text.SimpleDateFormat;

import pr.model.NormalModeJournalItem;
import javafx.beans.property.SimpleStringProperty;

public class NormalModeTableItem {
	private final SimpleDateFormat dFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
	
	private final SimpleStringProperty pPath;
	private final SimpleStringProperty pNameSignal;
	private final SimpleStringProperty pValue;
	private final SimpleStringProperty pDT;
	private final SimpleStringProperty pDTNew;
	
	public NormalModeTableItem(NormalModeJournalItem it) {
		pPath = new SimpleStringProperty(it.getPath() != null ? it.getPath() : "");
		pNameSignal = new SimpleStringProperty(it.getNamesignal() != null ? it.getNamesignal() : "");
		pValue = new SimpleStringProperty(it.getVal() + "");
		pDT = new SimpleStringProperty(it.getDt() != null ? dFormat.format(it.getDt()) : "");
		pDTNew = new SimpleStringProperty(it.getDt_new() != null ? dFormat.format(it.getDt_new()) : "");
	}
	
	public String getPPath() {
		return pPath.get();
	}

	public void setPPath(String val) {
		this.pPath.set(val);
	}
	
	public String getPNameSignal() {
		return pNameSignal.get();
	}

	public void setPNameSignal(String val) {
		this.pNameSignal.set(val);
	}
	
	public String getPValue() {
		return pValue.get();
	}

	public void setPValue(String val) {
		this.pValue.set(val);
	}
	
	public String getPDT() {
		return pDT.get();
	}

	public void setPDT(String val) {
		this.pDT.set(val);
	}
	
	public String getPDTNew() {
		return pDTNew.get();
	}

	public void setPDTNew(String val) {
		this.pDTNew.set(val);
	}
}
