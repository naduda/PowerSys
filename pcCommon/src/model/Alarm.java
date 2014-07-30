package model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Alarm implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private int objref;
	private String alarmname;
	private Timestamp recorddt;
	private Timestamp eventdt;
	private String alarmmes;
	private int logstate;
	private Timestamp confirmdt;
	private int userref;
	private String lognote;
	private int alarmpriority;
	private int eventtype;
	
	private int alarmid;
	private double objval;
	
	private String pObject;
	private String pLocation;
	private String pEventType;
	private String pAlarmPriority;
	private String pLogState;

	public Alarm() {

	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Alarm)) 
			return false;
		else {
			Alarm a = (Alarm) obj;
			if (a.alarmid == this.alarmid && a.alarmmes.equals(this.alarmmes) &&
					a.eventdt.equals(this.eventdt) && a.eventtype == this.eventtype &&
					a.alarmname.equals(this.alarmname) && a.alarmpriority == this.alarmpriority &&
					(a.confirmdt == null ? a.confirmdt == this.confirmdt : a.confirmdt.equals(this.confirmdt)) && 
					(a.lognote == null ? a.lognote == this.lognote : a.lognote.equals(this.lognote)) &&
					a.logstate == this.logstate && a.objref == this.objref && a.objval == this.objval)
				return true;
			else return false;
		}
	}

	public int getObjref() {
		return objref;
	}

	public void setObjref(int objref) {
		this.objref = objref;
	}

	public String getAlarmname() {
		return alarmname;
	}

	public void setAlarmname(String alarmname) {
		this.alarmname = alarmname;
	}

	public Timestamp getRecorddt() {
		return recorddt;
	}

	public void setRecorddt(Timestamp recorddt) {
		this.recorddt = recorddt;
	}

	public Timestamp getEventdt() {
		return eventdt;
	}

	public void setEventdt(Timestamp eventdt) {
		this.eventdt = eventdt;
	}

	public String getAlarmmes() {
		return alarmmes;
	}

	public void setAlarmmes(String alarmmes) {
		this.alarmmes = alarmmes;
	}

	public int getLogstate() {
		return logstate;
	}

	public void setLogstate(int logstate) {
		this.logstate = logstate;
	}

	public Timestamp getConfirmdt() {
		return confirmdt;
	}

	public void setConfirmdt(Timestamp confirmdt) {
		this.confirmdt = confirmdt;
	}

	public int getUserref() {
		return userref;
	}

	public void setUserref(int userref) {
		this.userref = userref;
	}

	public String getLognote() {
		return lognote;
	}

	public void setLognote(String lognote) {
		this.lognote = lognote;
	}

	public int getAlarmpriority() {
		return alarmpriority;
	}

	public void setAlarmpriority(int alarmpriority) {
		this.alarmpriority = alarmpriority;
	}

	public int getEventtype() {
		return eventtype;
	}

	public void setEventtype(int eventtype) {
		this.eventtype = eventtype;
	}

	public int getAlarmid() {
		return alarmid;
	}

	public void setAlarmid(int alarmid) {
		this.alarmid = alarmid;
	}

	public double getObjval() {
		return objval;
	}

	public void setObjval(double objval) {
		this.objval = objval;
	}

	public String getpObject() {
		return pObject;
	}

	public void setpObject(String pObject) {
		this.pObject = pObject;
	}

	public String getpLocation() {
		return pLocation;
	}

	public void setpLocation(String pLocation) {
		this.pLocation = pLocation;
	}

	public String getpEventType() {
		return pEventType;
	}

	public void setpEventType(String pEventType) {
		this.pEventType = pEventType;
	}

	public String getpAlarmPriority() {
		return pAlarmPriority;
	}

	public void setpAlarmPriority(String pAlarmPriority) {
		this.pAlarmPriority = pAlarmPriority;
	}

	public String getpLogState() {
		return pLogState;
	}

	public void setpLogState(String pLogState) {
		this.pLogState = pLogState;
	}
	
}
