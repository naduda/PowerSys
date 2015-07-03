package topic;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import pr.log.LogFiles;
import pr.model.Alarm;
import pr.model.ChatMessage;
import pr.model.ConfTree;
import pr.model.ControlJournalItem;
import pr.model.DvalTI;
import pr.model.LinkedValue;
import pr.model.NormalModeJournalItem;
import pr.model.SPunit;
import pr.model.SpTuCommand;
import pr.model.SpTypeSignal;
import pr.model.SwitchEquipmentJournalItem;
import pr.model.TSysParam;
import pr.model.TViewParam;
import pr.model.TalarmParam;
import pr.model.Tconftree;
import pr.model.Transparant;
import pr.model.Tscheme;
import pr.model.Tsignal;
import pr.model.TtranspHistory;
import pr.model.TtranspLocate;
import pr.model.Ttransparant;
import pr.model.Tuser;
import pr.model.UserEventJournalItem;
import pr.model.VsignalView;
import pr.powersys.IPowersys;
import pr.powersys.MySocketFactory;
import pr.powersys.ObjectSerializable;
import single.SingleObject;

@SuppressWarnings("unchecked")
public class ClientPowerSys implements IPowersys {	
	private IPowersys myServer;
	
	public ClientPowerSys() {
		myServer = getMyServer();
	}
	
	public IPowersys getMyServer() {
		if (myServer == null) {
			try {
				MySocketFactory.setServer(SingleObject.ipAddress);
				myServer = (IPowersys) Naming
						.lookup(String.format("rmi://%s:%s/PowerSysService", 
								SingleObject.ipAddress, IPowersys.RMI_PORT));
			} catch (NotBoundException | RemoteException | MalformedURLException e) {
				LogFiles.log.log(Level.WARNING, "PowerSysService is stoped");
				LogFiles.log.log(Level.INFO, "Exit ...");
				System.exit(0);
			}
		}
		return myServer;
	}
	
	public void setMyServer(IPowersys myServer) {
		this.myServer = myServer;
	}
//	==============================================================================
	@Override
	public void sendChatMessage(ChatMessage message) {
		new ClientRMI(c -> {c.sendChatMessage(message); return 0;}).get();
	}
//	==============================================================================
	@Override
	public String getDBparameters() {
		return new ClientRMI(c -> c.getDBparameters()).get().toString();
	}
//	==============================================================================
	@Override
	public void update(String query) {
		new ClientRMI(c -> {c.update(query); return 0;}).get();
	}
	
	@Override
	public List<NormalModeJournalItem> getListNormalModeItems(Timestamp dtBeg, Timestamp dtEnd, String idSignals) {
		return (List<NormalModeJournalItem>) new ClientRMI(c -> c.getListNormalModeItems(dtBeg, dtEnd, idSignals)).get();
	}
	
	@Override
	public List<SwitchEquipmentJournalItem> getSwitchJournalItems(String query) {
		return (List<SwitchEquipmentJournalItem>) new ClientRMI(c -> c.getSwitchJournalItems(query)).get();
	}
	
	@Override
	public Map<Integer, String> getReports() {
		return (Map<Integer, String>) new ClientRMI(c -> c.getReports()).get();
	}
	
	@Override
	public ObjectSerializable getReportById(int idReport, LocalDate dtBeg, LocalDate dtEnd, String format) {
		return (ObjectSerializable) new ClientRMI(c -> c.getReportById(idReport, dtBeg, dtEnd, format)).get();
	}
//	==============================================================================
	public Map<Integer, Tscheme> getSchemesMap() {
		return (Map<Integer, Tscheme>) new ClientRMI(c -> c.getSchemesMap()).get();
	}
	
	public Map<Integer, Tsignal> getTsignalsMap() {
		return (Map<Integer, Tsignal>) new ClientRMI(c -> c.getTsignalsMap()).get();
	}
	
	public Map<Integer, Tconftree> getTconftreeMap() {
		return (Map<Integer, Tconftree>) new ClientRMI(c -> c.getTconftreeMap()).get();
	}

	@Override
	public Map<Integer, ConfTree> getConfTreeMap() {
		return (Map<Integer, ConfTree>) new ClientRMI(c -> c.getConfTreeMap()).get();
	}

	@Override
	public List<Alarm> getAlarmsCurrentDay() {
		List<Alarm> curAl = (List<Alarm>) new ClientRMI(c -> c.getAlarmsCurrentDay()).get();
		return curAl == null ? new ArrayList<>() : curAl;
	}

	@Override
	public Map<String, TSysParam> getTSysParam(String paramname) {
		return (Map<String, TSysParam>) new ClientRMI(c -> c.getTSysParam(paramname)).get();
	}

	@Override
	public List<TViewParam> getTViewParam(String objdenom, String paramdenom, int userref) {
		return (List<TViewParam>) new ClientRMI(c -> c.getTViewParam(objdenom, paramdenom, userref)).get();
	}

	@Override
	public void setTS(int idsignal, double val, int rCode, int userId, int schemeref) {
		new ClientRMI(c -> {c.setTS(idsignal, val, rCode, userId, schemeref); return 0;}).get();
	}

	@Override
	public void setTU(int idsignal, double val, int rCode, int userId, int schemeref) {
		new ClientRMI(c -> {c.setTU(idsignal, val, rCode, userId, schemeref); return 0;}).get();
	}
	
	@Override
	public int getSendOK(int idsignal) {
		Integer res = (Integer) new ClientRMI(c -> c.getSendOK(idsignal)).get();
		return res == null ? -1 : res;
	}
	
	@Override
	public void setDevicesState(String iddevices, int state) {
		new ClientRMI(c -> {c.setDevicesState(iddevices, state); return 0;}).get();
	}

	@Override
	public List<LinkedValue> getDevicesState(String iddevices) {
		return (List<LinkedValue>) new ClientRMI(c -> c.getDevicesState(iddevices)).get();
	}

	@Override
	public List<Integer> getActiveDevices(String idSignals) {
		return (List<Integer>) new ClientRMI(c -> c.getActiveDevices(idSignals)).get();
	}

	@Override
	public Map<Integer, DvalTI> getOldTI(String idSignals) {
		Map<Integer, DvalTI> res = (Map<Integer, DvalTI>) new ClientRMI(c -> c.getOldTI(idSignals)).get();
		return res == null ? new HashMap<>() : res;
	}

	@Override
	public Map<Integer, DvalTI> getOldTS(String idSignals) {
		Map<Integer, DvalTI> res = (Map<Integer, DvalTI>) new ClientRMI(c -> c.getOldTS(idSignals)).get();
		return res == null ? new HashMap<>() : res;
	}
	
	@Override
	public Map<Integer, DvalTI> getValuesOnDate(String idSignals, Timestamp dtValue) {
		Map<Integer, DvalTI> res = (Map<Integer, DvalTI>) new ClientRMI(c -> c.getValuesOnDate(idSignals, dtValue)).get();
		return res == null ? new HashMap<>() : res;
	}

	@Override
	public void confirmAlarm(Timestamp recorddt, Timestamp eventdt, int objref, Timestamp confirmdt, String lognote, int userref) {
		new ClientRMI(c -> {c.confirmAlarm(recorddt, eventdt, objref, confirmdt, lognote, userref); return 0;}).get();
	}
	
	@Override
	public void confirmAlarmAll(String lognote, int userref) {
		new ClientRMI(c -> {c.confirmAlarmAll(lognote, userref); return 0;}).get();
	}
	
	@Override
	public List<Integer> getNotConfirmedSignals(String idSignals) {
		return (List<Integer>) new ClientRMI(c -> c.getNotConfirmedSignals(idSignals)).get();
	}
	
	@Override
	public Alarm getHightPriorityAlarm() {
		return (Alarm) new ClientRMI(c -> c.getHightPriorityAlarm()).get();
	}
	
	@Override
	public List<TalarmParam> getTalarmParams(int alarmref) {
		return (List<TalarmParam>) new ClientRMI(c -> c.getTalarmParams(alarmref)).get();
	}
	
	@Override
	public List<LinkedValue> getData(int idSignal) {
		return (List<LinkedValue>) new ClientRMI(c -> c.getData(idSignal)).get();
	}

	@Override
	public Map<Integer, SPunit> getSPunitMap() {
		return (Map<Integer, SPunit>) new ClientRMI(c -> c.getSPunitMap()).get();
	}

	@Override
	public List<LinkedValue> getDataIntegr(int idSignal, Timestamp dtBeg, Timestamp dtEnd, int period) {
		return (List<LinkedValue>) new ClientRMI(c -> c.getDataIntegr(idSignal, dtBeg, dtEnd, period)).get();
	}
	
	@Override
	public List<LinkedValue> getDataIntegrArc(int idSignal, Timestamp dtBeg, Timestamp dtEnd, int period) {
		return (List<LinkedValue>) new ClientRMI(c -> c.getDataIntegrArc(idSignal, dtBeg, dtEnd, period)).get();
	}

	@Override
	public List<LinkedValue> getDataArc(int idSignal, Timestamp dtBeg, Timestamp dtEnd) {
		return (List<LinkedValue>) new ClientRMI(c -> c.getDataArc(idSignal, dtBeg, dtEnd)).get();
	}

	@Override
	public Object getTransparantById(int idTransparant) {
		return new ClientRMI(c -> c.getTransparantById(idTransparant)).get();
	}

	@Override
	public Map<Integer, Transparant> getTransparants() {
		return (Map<Integer, Transparant>) new ClientRMI(c -> c.getTransparants()).get();
	}

	@Override
	public List<Ttransparant> getTtransparantsActive(int idScheme) {
		return (List<Ttransparant>) new ClientRMI(c -> c.getTtransparantsActive(idScheme)).get();
	}

	@Override
	public TtranspLocate getTransparantLocate(int trref) {
		return (TtranspLocate) new ClientRMI(c -> c.getTransparantLocate(trref)).get();
	}

	@Override
	public List<Ttransparant> getTtransparantsNew(Timestamp settime) {
		return (List<Ttransparant>) new ClientRMI(c -> c.getTtransparantsNew(settime)).get();
	}

	@Override
	public List<Ttransparant> getTtransparantsClosed(Timestamp closetime) {
		return (List<Ttransparant>) new ClientRMI(c -> c.getTtransparantsClosed(closetime)).get();
	}

	@Override
	public List<Ttransparant> getTtransparantsUpdated(Timestamp lastupdate) {
		return (List<Ttransparant>) new ClientRMI(c -> c.getTtransparantsUpdated(lastupdate)).get();
	}

	@Override
	public void insertTtransparant(int idtr, int signref, String objname, int tp, int schemeref) {
		new ClientRMI(c -> {c.insertTtransparant(idtr, signref, objname, tp, schemeref);return 0;}).get();
	}

	@Override
	public void insertTtranspHistory(int trref, int userref, String txt, int trtype) {
		new ClientRMI(c -> {c.insertTtranspHistory(trref, userref, txt, trtype);return 0;}).get();
	}

	@Override
	public void deleteTtranspLocate(int trref, int scref) {
		new ClientRMI(c -> {c.deleteTtranspLocate(trref, scref);return 0;}).get();
	}

	@Override
	public void insertTtranspLocate(int trref, int scref, int x, int y, int h, int w) {
		new ClientRMI(c -> {c.insertTtranspLocate(trref, scref, x, y, h, w);return 0;}).get();
	}

	@Override
	public int getMaxTranspID() {
		Integer res = (Integer) new ClientRMI(c -> c.getMaxTranspID()).get();
		return res == null ? 0 : res;
	}

	@Override
	public void updateTtransparantLastUpdate(int idtr) {
		new ClientRMI(c -> {c.updateTtransparantLastUpdate(idtr);return 0;}).get();
	}

	@Override
	public void updateTtranspLocate(int trref, int scref, int x, int y, int h, int w) {
		new ClientRMI(c -> {c.updateTtranspLocate(trref, scref, x, y, h, w);return 0;}).get();
	}

	@Override
	public void updateTtransparantCloseTime(int idtr) {
		new ClientRMI(c -> {c.updateTtransparantCloseTime(idtr);return 0;}).get();
	}

	@Override
	public TtranspHistory getTtranspHistory(int trref) {
		return (TtranspHistory) new ClientRMI(c -> c.getTtranspHistory(trref)).get();
	}

	@Override
	public void updateTtranspHistory(int trref, String txt) {
		new ClientRMI(c -> {c.updateTtranspHistory(trref, txt);return 0;}).get();
	}

	@Override
	public Ttransparant getTtransparantById(int idtr) {
		return (Ttransparant) new ClientRMI(c -> c.getTtransparantById(idtr)).get();
	}

	@Override
	public List<SpTuCommand> getSpTuCommand() {
		return (List<SpTuCommand>) new ClientRMI(c -> c.getSpTuCommand()).get();
	}

	@Override
	public Map<Integer, Tuser> getTuserMap() {
		return (Map<Integer, Tuser>) new ClientRMI(c -> c.getTuserMap()).get();
	}

	@Override
	public List<Alarm> getAlarmsPeriod(Timestamp dtBeg, Timestamp dtEnd) {
		return (List<Alarm>) new ClientRMI(c -> c.getAlarmsPeriod(dtBeg, dtEnd)).get();
	}
	
	@Override
	public List<Alarm> getAlarmsPeriodById(Timestamp dtBeg, Timestamp dtEnd, int idSignal) {
		return (List<Alarm>) new ClientRMI(c -> c.getAlarmsPeriodById(dtBeg, dtEnd, idSignal)).get();
	}

	@Override
	public List<ControlJournalItem> getJContrlItems(Timestamp dtBeg, Timestamp dtEnd) {
		return (List<ControlJournalItem>) new ClientRMI(c -> c.getJContrlItems(dtBeg, dtEnd)).get();
	}

	@Override
	public Map<Integer, VsignalView> getVsignalViewMap() {
		return (Map<Integer, VsignalView>) new ClientRMI(c -> c.getVsignalViewMap()).get();
	}

	@Override
	public void setBaseVal(int idSignal, double val) {
		new ClientRMI(c -> {c.setBaseVal(idSignal, val); return 0;}).get();
	}

	@Override
	public void updateTsignalStatus(int idSignal, int status) {
		new ClientRMI(c -> {c.updateTsignalStatus(idSignal, status); return 0;}).get();
	}

	@Override
	public void insertDeventLog(int eventtype, int objref, Timestamp eventdt, double objval, int objstatus, int authorref) {
		new ClientRMI(c -> {c.insertDeventLog(eventtype, objref, eventdt, objval, objstatus, authorref); return 0;}).get();
	}

	@Override
	public List<UserEventJournalItem> getUserEventJournalItems(Timestamp dtBeg, Timestamp dtEnd) {
		return (List<UserEventJournalItem>) new ClientRMI(c -> c.getUserEventJournalItems(dtBeg, dtEnd)).get();
	}

	@Override
	public Map<Integer, SpTypeSignal> getSpTypeSignalMap() {
		return (Map<Integer, SpTypeSignal>) new ClientRMI(c -> c.getSpTypeSignalMap()).get();
	}

	@Override
	public void updateTScheme(int idscheme, String schemedenom, String schemename, 
			String schemedescr, int parentref, Object schemefile, int userid) {
		
		new ClientRMI(c -> {
			c.updateTScheme(idscheme, schemedenom, schemename, schemedescr, parentref, schemefile, userid); 
			return 0;
		}).get();
	}

	@Override
	public void deleteScheme(int idscheme) {
		new ClientRMI(c -> {c.deleteScheme(idscheme);return 0;}).get();
	}

	@Override
	public void addScheme(String schemedenom, String schemename, String schemedescr, int parentref, 
			Object schemefile, int userid) {
		
		new ClientRMI(c -> {
			c.addScheme(schemedenom, schemename, schemedescr, parentref, schemefile, userid); 
			return 0;
		}).get();
	}
} 
