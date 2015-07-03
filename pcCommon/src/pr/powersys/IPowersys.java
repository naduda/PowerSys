package pr.powersys;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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
      
public interface IPowersys extends Remote {
	static final int RMI_PORT = 1099;
//	public static final String bindString = String.format("rmi://%s:%s/PowerSysService", RMI_PORT);

//	==============================================================================
	void sendChatMessage(ChatMessage message) throws RemoteException;
//	==============================================================================
	String getDBparameters() throws RemoteException;
//	==============================================================================
	void update(String query) throws RemoteException;
	List<NormalModeJournalItem> getListNormalModeItems(Timestamp dtBeg, Timestamp dtEnd, String idSignals) throws RemoteException;
	List<SwitchEquipmentJournalItem> getSwitchJournalItems(String idSignals) throws RemoteException;
	
	Map<Integer, String> getReports() throws RemoteException;
	ObjectSerializable getReportById(int idReport, LocalDate dtBeg, LocalDate dtEnd, String format) throws RemoteException;
//	==============================================================================
	Map<Integer, Tscheme> getSchemesMap() throws RemoteException;
	Map<Integer, Tconftree> getTconftreeMap() throws RemoteException;
	Map<Integer, Tsignal> getTsignalsMap() throws RemoteException;
	Map<Integer, VsignalView> getVsignalViewMap() throws RemoteException;
	Map<Integer, ConfTree> getConfTreeMap() throws RemoteException;
	List<Alarm> getAlarmsCurrentDay() throws RemoteException;
	List<Alarm> getAlarmsPeriod(Timestamp dtBeg, Timestamp dtEnd) throws RemoteException;
	List<Alarm> getAlarmsPeriodById(Timestamp dtBeg, Timestamp dtEnd, int idSignal) throws RemoteException;
	Map<String, TSysParam> getTSysParam(String paramname) throws RemoteException;
	List<TViewParam> getTViewParam(String objdenom, String paramdenom, int userref) throws RemoteException;
	void setTS(int idsignal, double val, int rCode, int userId, int schemeref) throws RemoteException;
	void setTU(int idsignal, double val, int rCode, int userId, int schemeref) throws RemoteException;
	int getSendOK(int idsignal) throws RemoteException;
	void setDevicesState(String iddevices, int state) throws RemoteException;
	List<LinkedValue> getDevicesState(String iddevices) throws RemoteException;
	List<Integer> getActiveDevices(String idSignals) throws RemoteException;
	Map<Integer, DvalTI> getOldTI(String idSignals) throws RemoteException;
	Map<Integer, DvalTI> getOldTS(String idSignals) throws RemoteException;
	Map<Integer, DvalTI> getValuesOnDate(String idSignals, Timestamp dtValue) throws RemoteException;
	void confirmAlarm(Timestamp recorddt, Timestamp eventdt, int objref, Timestamp confirmdt, String lognote, int userref) throws RemoteException;
	void confirmAlarmAll(String lognote, int userref) throws RemoteException;
	List<Integer> getNotConfirmedSignals(String idSignals) throws RemoteException;
	Alarm getHightPriorityAlarm() throws RemoteException;
	List<TalarmParam> getTalarmParams(int alarmref) throws RemoteException;
	Map<Integer, SPunit> getSPunitMap() throws RemoteException;
	Map<Integer, SpTypeSignal> getSpTypeSignalMap() throws RemoteException;
	List<SpTuCommand> getSpTuCommand() throws RemoteException;
	Map<Integer, Tuser> getTuserMap() throws RemoteException;
	
	List<LinkedValue> getData(int idSignal) throws RemoteException;
	List<LinkedValue> getDataArc(int idSignal, Timestamp dtBeg, Timestamp dtEnd) throws RemoteException;
	List<LinkedValue> getDataIntegr(int idSignal, Timestamp dtBeg, Timestamp dtEnd, int period) throws RemoteException;
	List<LinkedValue> getDataIntegrArc(int idSignal, Timestamp dtBeg, Timestamp dtEnd, int period) throws RemoteException;
	
	Object getTransparantById(int idTransparant) throws RemoteException;
	Map<Integer, Transparant> getTransparants() throws RemoteException;
	List<Ttransparant> getTtransparantsActive(int idScheme) throws RemoteException;
	List<Ttransparant> getTtransparantsNew(Timestamp settime) throws RemoteException;
	TtranspLocate getTransparantLocate(int trref) throws RemoteException;
	List<Ttransparant> getTtransparantsClosed(Timestamp closetime) throws RemoteException;
	List<Ttransparant> getTtransparantsUpdated(Timestamp lastupdate) throws RemoteException;
	
	void deleteScheme(int idscheme) throws RemoteException;
	void addScheme(String schemedenom, String schemename, String schemedescr,
			int parentref, Object schemefile, int userid) throws RemoteException;
	void updateTScheme(int idscheme, String schemedenom, String schemename, String schemedescr,
			int parentref, Object schemefile, int userid) throws RemoteException;
	void insertTtransparant(int idtr, int signref, String objname, int tp, int schemeref) throws RemoteException;
	void insertTtranspHistory(int trref, int userref, String txt, int trtype) throws RemoteException;
	void deleteTtranspLocate(int trref, int scref) throws RemoteException;
	void insertTtranspLocate(int trref, int scref, int x, int y, int h, int w) throws RemoteException;
	int getMaxTranspID() throws RemoteException;
	void updateTtransparantLastUpdate(int idtr) throws RemoteException;
	void updateTtranspLocate(int trref, int scref, int x, int y, int h, int w) throws RemoteException;
	void updateTtransparantCloseTime(int idtr) throws RemoteException;
	TtranspHistory getTtranspHistory(int trref) throws RemoteException;
	void updateTtranspHistory(int trref, String txt) throws RemoteException;
	Ttransparant getTtransparantById(int idtr) throws RemoteException;
	
	public List<ControlJournalItem> getJContrlItems(Timestamp dtBeg, Timestamp dtEnd) throws RemoteException;
	void setBaseVal(int idSignal, double val) throws RemoteException;
	void updateTsignalStatus(int idSignal, int status) throws RemoteException;
	
	void insertDeventLog(int eventtype, int objref, Timestamp eventdt, double objval, int objstatus, int authorref) throws RemoteException;
	
	List<UserEventJournalItem> getUserEventJournalItems(Timestamp dtBeg, Timestamp dtEnd) throws RemoteException;
} 