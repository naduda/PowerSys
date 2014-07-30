package model;

public class Tsignal {

	private int idsignal;
	private int noderef;
	private int sortnum;
	private int typesignalref;
	private String namesignal;
	private int typeresref;
	private double koef;
	private double baseval;
	private double highval;
	private double lowval;
	private int intervalref;
	private int integrationref;
	private int quantityref;
	private int unitref;
	private int regnum;
	private int datatyperef;
	private int ioa;
	private int alarmtimeout;
	private int linksignalref;
	private int status;
	private int useformula;
	private String formula;
	private int usetupassw;
	private String tupassw;
	private int usearchive;
	private int stateref;
	private String udt;
	private int arcdepth;
	private double alarmarchivetsval;
	private int alarmarchivedepth;
	private int schemeref;
	private int uotyperef;
	
	private String location; 
	
	public Tsignal () {
		
	}
	
	@Override
	public String toString() {
		return idsignal + " - " + namesignal;
	}

	public int getIdsignal() {
		return idsignal;
	}
	
	public void setIdsignal(int idsignal) {
		this.idsignal = idsignal;
	}
	
	public int getNoderef() {
		return noderef;
	}
	
	public void setNoderef(int noderef) {
		this.noderef = noderef;
	}
	
	public int getSortnum() {
		return sortnum;
	}
	
	public void setSortnum(int sortnum) {
		this.sortnum = sortnum;
	}
	
	public int getTypesignalref() {
		return typesignalref;
	}
	
	public void setTypesignalref(int typesignalref) {
		this.typesignalref = typesignalref;
	}
	
	public String getNamesignal() {
		return namesignal;
	}
	
	public void setNamesignal(String namesignal) {
		this.namesignal = namesignal;
	}
	
	public int getTyperesref() {
		return typeresref;
	}
	
	public void setTyperesref(int typeresref) {
		this.typeresref = typeresref;
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
	
	public int getIntervalref() {
		return intervalref;
	}
	
	public void setIntervalref(int intervalref) {
		this.intervalref = intervalref;
	}
	
	public int getIntegrationref() {
		return integrationref;
	}
	
	public void setIntegrationref(int integrationref) {
		this.integrationref = integrationref;
	}
	
	public int getQuantityref() {
		return quantityref;
	}
	
	public void setQuantityref(int quantityref) {
		this.quantityref = quantityref;
	}
	
	public int getUnitref() {
		return unitref;
	}
	
	public void setUnitref(int unitref) {
		this.unitref = unitref;
	}
	
	public int getRegnum() {
		return regnum;
	}
	
	public void setRegnum(int regnum) {
		this.regnum = regnum;
	}
	
	public int getDatatyperef() {
		return datatyperef;
	}
	
	public void setDatatyperef(int datatyperef) {
		this.datatyperef = datatyperef;
	}
	
	public int getIoa() {
		return ioa;
	}
	
	public void setIoa(int ioa) {
		this.ioa = ioa;
	}
	
	public int getAlarmtimeout() {
		return alarmtimeout;
	}
	
	public void setAlarmtimeout(int alarmtimeout) {
		this.alarmtimeout = alarmtimeout;
	}
	
	public int getLinksignalref() {
		return linksignalref;
	}
	
	public void setLinksignalref(int linksignalref) {
		this.linksignalref = linksignalref;
	}
	
	public int getStatus() {
		return status;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}
	
	public int getUseformula() {
		return useformula;
	}
	
	public void setUseformula(int useformula) {
		this.useformula = useformula;
	}
	
	public String getFormula() {
		return formula;
	}
	
	public void setFormula(String formula) {
		this.formula = formula;
	}
	
	public int getUsetupassw() {
		return usetupassw;
	}
	
	public void setUsetupassw(int usetupassw) {
		this.usetupassw = usetupassw;
	}
	
	public String getTupassw() {
		return tupassw;
	}
	
	public void setTupassw(String tupassw) {
		this.tupassw = tupassw;
	}
	
	public int getUsearchive() {
		return usearchive;
	}
	
	public void setUsearchive(int usearchive) {
		this.usearchive = usearchive;
	}
	
	public int getStateref() {
		return stateref;
	}
	
	public void setStateref(int stateref) {
		this.stateref = stateref;
	}
	
	public String getUdt() {
		return udt;
	}
	
	public void setUdt(String udt) {
		this.udt = udt;
	}
	
	public int getArcdepth() {
		return arcdepth;
	}
	
	public void setArcdepth(int arcdepth) {
		this.arcdepth = arcdepth;
	}
	
	public double getAlarmarchivetsval() {
		return alarmarchivetsval;
	}
	
	public void setAlarmarchivetsval(double alarmarchivetsval) {
		this.alarmarchivetsval = alarmarchivetsval;
	}
	
	public int getAlarmarchivedepth() {
		return alarmarchivedepth;
	}
	
	public void setAlarmarchivedepth(int alarmarchivedepth) {
		this.alarmarchivedepth = alarmarchivedepth;
	}
	
	public int getSchemeref() {
		return schemeref;
	}
	
	public void setSchemeref(int schemeref) {
		this.schemeref = schemeref;
	}
	
	public int getUotyperef() {
		return uotyperef;
	}
	
	public void setUotyperef(int uotyperef) {
		this.uotyperef = uotyperef;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
}
