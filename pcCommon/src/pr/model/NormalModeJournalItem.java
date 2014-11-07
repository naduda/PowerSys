package pr.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class NormalModeJournalItem implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String path;
	private String namesignal;
	private int idsignal;
	private double val;
	private Timestamp dt;
	private Timestamp dt_new;
	
	public NormalModeJournalItem() {
		
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getNamesignal() {
		return namesignal;
	}

	public void setNamesignal(String namesignal) {
		this.namesignal = namesignal;
	}

	public double getVal() {
		return val;
	}

	public void setVal(double val) {
		this.val = val;
	}

	public Timestamp getDt() {
		return dt;
	}

	public void setDt(Timestamp dt) {
		this.dt = dt;
	}

	public Timestamp getDt_new() {
		return dt_new;
	}

	public void setDt_new(Timestamp dt_new) {
		this.dt_new = dt_new;
	}

	public int getIdsignal() {
		return idsignal;
	}

	public void setIdsignal(int idsignal) {
		this.idsignal = idsignal;
	}
}
