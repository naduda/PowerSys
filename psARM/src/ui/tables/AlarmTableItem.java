package ui.tables;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Map;

import ui.MainStage;
import pr.model.Alarm;
import pr.model.TSysParam;
import javafx.beans.property.SimpleStringProperty;

public class AlarmTableItem {
	private final SimpleDateFormat dFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
	
	private Map<String, TSysParam> sysParamsEvent;
	private Map<String, TSysParam> sysParamsPriority;
	private Map<String, TSysParam> sysParamsLogState;
	
	private final SimpleStringProperty pObject;
	private final SimpleStringProperty pLocation;
	private final SimpleStringProperty pAlarmName;
	private final SimpleStringProperty pRecordDT;
	private final SimpleStringProperty pEventDT;
	private final SimpleStringProperty pAlarmMes;
	private final SimpleStringProperty pLogState;
	private final SimpleStringProperty pConfirmDT;
	private final SimpleStringProperty pUserRef;
	private final SimpleStringProperty pLogNote;
	private final SimpleStringProperty pAlarmPriority;
	private final SimpleStringProperty pEventType;
	private final SimpleStringProperty pSchemeObject;
	
	private Alarm alarm;
	
	public AlarmTableItem(Alarm a) {
		try {
			sysParamsEvent = MainStage.psClient.getTSysParam("ALARM_EVENT");
			sysParamsPriority = MainStage.psClient.getTSysParam("ALARM_PRIORITY");
			sysParamsLogState = MainStage.psClient.getTSysParam("LOG_STATE");
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		a.setpObject(MainStage.signals.get(a.getObjref()).getNamesignal());
		a.setpLocation(MainStage.signals.get(a.getObjref()).getSignalpath());
		
    	AlarmMessageParcer.setAlarmMessage(a);

    	a.setpEventType(sysParamsEvent.get("" + a.getEventtype()).getParamdescr());
    	a.setpAlarmPriority(sysParamsPriority.get("" + a.getAlarmpriority()).getParamdescr());
    	
    	int lState = a.isAlarmuser_ack() ? a.getLogstate() : 10;
    	a.setLogstate(lState);
    	a.setpLogState(sysParamsLogState.get("" + lState).getParamdescr());
		
		pObject = new SimpleStringProperty(a.getpObject() != null ? a.getpObject() : "");
		pLocation = new SimpleStringProperty(a.getpLocation() != null ? a.getpLocation() : "");
		pAlarmName = new SimpleStringProperty(a.getAlarmname() != null ? a.getAlarmname() : "");
		pRecordDT = new SimpleStringProperty(a.getRecorddt() != null ? dFormat.format(a.getRecorddt()) : "");
		pEventDT = new SimpleStringProperty(a.getEventdt() != null ? dFormat.format(a.getEventdt()) : "");
		pAlarmMes = new SimpleStringProperty(a.getAlarmmes() != null ? a.getAlarmmes() : "");
		pLogState = new SimpleStringProperty(a.getpLogState() != null ? a.getpLogState() : "");
		pConfirmDT = new SimpleStringProperty(a.getConfirmdt() != null ? dFormat.format(a.getConfirmdt()) : "");
		pUserRef = new SimpleStringProperty(a.getUserref() == 0 ? "" : 
			a.getUserref() == -1 ? "Administrator" : MainStage.users.get(a.getUserref()).getFio());
		pLogNote = new SimpleStringProperty(a.getLognote() != null ? a.getLognote() : "");
		pAlarmPriority = new SimpleStringProperty(a.getpAlarmPriority() != null ? a.getpAlarmPriority() : "");
		pEventType = new SimpleStringProperty(a.getpEventType() != null ? a.getpEventType() : "");
		pSchemeObject = new SimpleStringProperty("");
		
		setAlarm(a);
	}
		
	public String getPObject() {
        return pObject.get();
    }

    public void setPObject(String sObject) {
    	pObject.set(sObject);
    }
    
    public String getPLocation() {
        return pLocation.get();
    }

    public void setPLocation(String sLocation) {
    	pLocation.set(sLocation);
    }
    
    public String getPAlarmName() {
        return pAlarmName.get();
    }

    public void setPAlarmName(String sAlarmName) {
    	pAlarmName.set(sAlarmName);
    }
    
    public String getPRecordDT() {
        return pRecordDT.get();
    }

    public void setPRecordDT(String sRecordDT) {
    	pRecordDT.set(sRecordDT);
    }
    
    public String getPEventDT() {
        return pEventDT.get();
    }

    public void setPEventDT(String sEventDT) {
    	pEventDT.set(sEventDT);
    }
    
    public String getPAlarmMes() {
        return pAlarmMes.get();
    }

    public void setPAlarmMes(String sAlarmMes) {
    	pAlarmMes.set(sAlarmMes);
    }
    
    public String getPLogState() {
        return pLogState.get();
    }

    public void setPLogState(String sLogState) {
    	pLogState.set(sLogState);
    }

	public String getPConfirmDT() {
        return pConfirmDT.get();
    }

    public void setPConfirmDT(String sConfirmDT) {
    	pConfirmDT.set(sConfirmDT);
    }
    
    public String getPUserRef() {
        return pUserRef.get();
    }

    public void setPUserRef(String sUserRef) {
    	pUserRef.set(sUserRef);
    }
    
    public String getPLogNote() {
        return pLogNote.get();
    }

    public void setPLogNote(String sLogNote) {
    	pLogNote.set(sLogNote);
    }
    
    public String getPAlarmPriority() {
        return pAlarmPriority.get();
    }

    public void setPAlarmPriority(String sAlarmPriority) {
    	pAlarmPriority.set(sAlarmPriority);
    }

	public String getPEventType() {
        return pEventType.get();
    }

    public void setPEventType(String sEventType) {
    	pEventType.set(sEventType);
    }
    
    public String getPSchemeObject() {
        return pSchemeObject.get();
    }

    public void setPSchemeObject(String sSchemeObject) {
    	pSchemeObject.set(sSchemeObject);
    }

	public Alarm getAlarm() {
		return alarm;
	}

	public void setAlarm(Alarm alarm) {
		this.alarm = alarm;
	}
}
