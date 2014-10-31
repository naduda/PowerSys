package pr.model;

import java.io.Serializable;

public class SpTypeSignal implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private int idtypesignal;
	private String nametypesignal;
	
	public SpTypeSignal() {
		
	}

	public int getIdtypesignal() {
		return idtypesignal;
	}

	public void setIdtypesignal(int idtypesignal) {
		this.idtypesignal = idtypesignal;
	}

	public String getNametypesignal() {
		return nametypesignal;
	}

	public void setNametypesignal(String nametypesignal) {
		this.nametypesignal = nametypesignal;
	}
}
