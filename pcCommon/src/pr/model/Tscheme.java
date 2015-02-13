package pr.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Tscheme implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private int idscheme;
	private String schemedenom;
	private String schemename;
	private String schemedescr;
	private int parentref;
	private Object schemefile;
	private int userid;
	private Timestamp lastupdate;
	private int savehistory;
	
	public Tscheme() {
		
	}
	
	@Override
	public String toString() {
		return schemename;
	}
	
	public int getIdscheme() {
		return idscheme;
	}

	public void setIdscheme(int idscheme) {
		this.idscheme = idscheme;
	}

	public String getSchemedenom() {
		return schemedenom;
	}

	public void setSchemedenom(String schemedenom) {
		this.schemedenom = schemedenom;
	}

	public String getSchemename() {
		return schemename;
	}

	public void setSchemename(String schemename) {
		this.schemename = schemename;
	}

	public String getSchemedescr() {
		return schemedescr;
	}

	public void setSchemedescr(String schemedescr) {
		this.schemedescr = schemedescr;
	}

	public int getParentref() {
		return parentref;
	}

	public void setParentref(int parentref) {
		this.parentref = parentref;
	}

	public Object getSchemefile() {
		return schemefile;
	}

	public void setSchemefile(Object schemefile) {
		this.schemefile = schemefile;
	}

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public Timestamp getLastupdate() {
		return lastupdate;
	}

	public void setLastupdate(Timestamp lastupdate) {
		this.lastupdate = lastupdate;
	}

	public int getSavehistory() {
		return savehistory;
	}

	public void setSavehistory(int savehistory) {
		this.savehistory = savehistory;
	}
}
