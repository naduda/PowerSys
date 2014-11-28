package pr.model;

import java.io.Serializable;

public class Transparant implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private int idtr;
	private int moderef;
	private int objtyperef;
	private String descr;
	private Object img;
	private byte[] imageByteArray;
	
	public Transparant() {
		
	}

	@Override
	public String toString() {
		return getDescr();
	}

	public int getIdtr() {
		return idtr;
	}

	public void setIdtr(int idtr) {
		this.idtr = idtr;
	}

	public int getModeref() {
		return moderef;
	}

	public void setModeref(int moderef) {
		this.moderef = moderef;
	}

	public int getObjtyperef() {
		return objtyperef;
	}

	public void setObjtyperef(int objtyperef) {
		this.objtyperef = objtyperef;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public Object getImg() {
		return img;
	}

	public void setImg(Object img) {
		this.img = img;
	}

	public byte[] getImageByteArray() {
		return imageByteArray;
	}

	public void setImageByteArray(byte[] imageByteArray) {
		this.imageByteArray = imageByteArray;
	}
}
