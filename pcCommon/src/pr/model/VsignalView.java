package pr.model;

import java.io.Serializable;

public class VsignalView implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private int idsignal;
	private int sortnum;
	private String signalpath;
	private String namesignal;
	private int ioa;
	private int typesignalref;
	private String nametypesignal;
	private double koef;
	private double baseval;
	private double highval;
	private double lowval;
	private String nameinterval;
	private String integration;
	private String namequantity;
	private String nameunit;
	private int status;
	private String statusname;
	private int regnum;
	private String datatypename;
	
	public VsignalView() {
		
	}

	public int getIdsignal() {
		return idsignal;
	}

	public void setIdsignal(int idsignal) {
		this.idsignal = idsignal;
	}

	public int getSortnum() {
		return sortnum;
	}

	public void setSortnum(int sortnum) {
		this.sortnum = sortnum;
	}

	public String getSignalpath() {
		return signalpath;
	}

	public void setSignalpath(String signalpath) {
		this.signalpath = signalpath;
	}

	public String getNamesignal() {
		return namesignal;
	}

	public void setNamesignal(String namesignal) {
		this.namesignal = namesignal;
	}

	public int getIoa() {
		return ioa;
	}

	public void setIoa(int ioa) {
		this.ioa = ioa;
	}

	public int getTypesignalref() {
		return typesignalref;
	}

	public void setTypesignalref(int typesignalref) {
		this.typesignalref = typesignalref;
	}

	public String getNametypesignal() {
		return nametypesignal;
	}

	public void setNametypesignal(String nametypesignal) {
		this.nametypesignal = nametypesignal;
	}

	public double getKoef() {
		return koef;
	}

	public void setKoef(double koef) {
		this.koef = koef;
	}

	public double getBaseval() {
		return baseval;
	}

	public void setBaseval(double baseval) {
		this.baseval = baseval;
	}

	public double getHighval() {
		return highval;
	}

	public void setHighval(double highval) {
		this.highval = highval;
	}

	public double getLowval() {
		return lowval;
	}

	public void setLowval(double lowval) {
		this.lowval = lowval;
	}

	public String getNameinterval() {
		return nameinterval;
	}

	public void setNameinterval(String nameinterval) {
		this.nameinterval = nameinterval;
	}

	public String getIntegration() {
		return integration;
	}

	public void setIntegration(String integration) {
		this.integration = integration;
	}

	public String getNamequantity() {
		return namequantity;
	}

	public void setNamequantity(String namequantity) {
		this.namequantity = namequantity;
	}

	public String getNameunit() {
		return nameunit;
	}

	public void setNameunit(String nameunit) {
		this.nameunit = nameunit;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getStatusname() {
		return statusname;
	}

	public void setStatusname(String statusname) {
		this.statusname = statusname;
	}

	public int getRegnum() {
		return regnum;
	}

	public void setRegnum(int regnum) {
		this.regnum = regnum;
	}

	public String getDatatypename() {
		return datatypename;
	}

	public void setDatatypename(String datatypename) {
		this.datatypename = datatypename;
	}
}
