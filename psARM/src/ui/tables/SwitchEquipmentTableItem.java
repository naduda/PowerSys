package ui.tables;

import java.text.SimpleDateFormat;

import pr.model.SwitchEquipmentJournalItem;
import ui.single.Constants;
import javafx.beans.property.SimpleStringProperty;

public class SwitchEquipmentTableItem {
	private final SimpleDateFormat dFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
	
	private final SimpleStringProperty pSignal;
	private final SimpleStringProperty pState;
	private final SimpleStringProperty pDT;
	private final SimpleStringProperty pMode;
	
	public SwitchEquipmentTableItem(SwitchEquipmentJournalItem it) {
		pSignal = new SimpleStringProperty(it.getNamesignal() != null ? it.getNamesignal() : "");
		pState = new SimpleStringProperty(it.getTxtval() != null ? it.getTxtval() : "");
		pDT = new SimpleStringProperty(it.getDt() != null ? dFormat.format(it.getDt()) : "");
		pMode = new SimpleStringProperty(Constants.getQuality(it.getRcode()));
	}
	
	public String getPSignal() {
		return pSignal.get();
	}

	public void setPSignal(String val) {
		this.pSignal.set(val);
	}
	
	public String getPState() {
		return pState.get();
	}

	public void setPState(String val) {
		this.pState.set(val);
	}
	public String getPDT() {
		return pDT.get();
	}

	public void setPDT(String val) {
		this.pDT.set(val);
	}
	public String getPMode() {
		return pMode.get();
	}

	public void setPMode(String val) {
		this.pMode.set(val);
	}
}
