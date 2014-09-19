package model;

import java.io.Serializable;

public class SPunit implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private int idunit;
	private int quantityref;
	private String nameunit;
	private double sikoef;
	private int sortnum;
	
	public SPunit() {
		
	}

	public int getIdunit() {
		return idunit;
	}

	public void setIdunit(int idunit) {
		this.idunit = idunit;
	}

	public int getQuantityref() {
		return quantityref;
	}

	public void setQuantityref(int quantityref) {
		this.quantityref = quantityref;
	}

	public String getNameunit() {
		return nameunit;
	}

	public void setNameunit(String nameunit) {
		this.nameunit = nameunit;
	}

	public double getSikoef() {
		return sikoef;
	}

	public void setSikoef(double sikoef) {
		this.sikoef = sikoef;
	}

	public int getSortnum() {
		return sortnum;
	}

	public void setSortnum(int sortnum) {
		this.sortnum = sortnum;
	}
}
