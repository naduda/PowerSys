package pr.powersys;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import pr.model.Alarm;
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
import pr.model.Transparant;
import pr.model.Tsignal;
import pr.model.TtranspHistory;
import pr.model.TtranspLocate;
import pr.model.Ttransparant;
import pr.model.Tuser;
import pr.model.UserEventJournalItem;
import pr.model.VsignalView;
      
public interface IPowersys extends Remote {
	public static final int RMI_PORT = 1099;
//	public static final String bindString = String.format("rmi://%s:%s/PowerSysService", RMI_PORT);
	
//	==============================================================================
	void update(String query) throws RemoteException;
	List<NormalModeJournalItem> getListNormalModeItems(String query) throws RemoteException;
	List<SwitchEquipmentJournalItem> getSwitchJournalItems(String query) throws RemoteException;
//	==============================================================================
	public Map<Integer, Tsignal> getTsignalsMap() throws RemoteException;
	public Map<Integer, VsignalView> getVsignalViewMap() throws RemoteException;
	public Map<Integer, ConfTree> getConfTreeMap() throws RemoteException;
	public List<Alarm> getAlarmsCurrentDay() throws RemoteException;
	public List<Alarm> getAlarmsPeriod(Timestamp dtBeg, Timestamp dtEnd) throws RemoteException;
	public List<Alarm> getAlarmsPeriodById(Timestamp dtBeg, Timestamp dtEnd, int idSignal) throws RemoteException;
	public Map<String, TSysParam> getTSysParam(String paramname) throws RemoteException;
	public List<TViewParam> getTViewParam(String objdenom, String paramdenom, int userref) throws RemoteException;
	public void setTS(int idsignal, double val, int schemeref) throws RemoteException;
	public Map<Integer, DvalTI> getOldTI(String idSignals) throws RemoteException;
	public Map<Integer, DvalTI> getOldTS(String idSignals) throws RemoteException;
	public void confirmAlarm(Timestamp recorddt, Timestamp eventdt, int objref, Timestamp confirmdt, String lognote, int userref) throws RemoteException;
	public void confirmAlarmAll(String lognote, int userref) throws RemoteException;
	public Map<Integer, SPunit> getSPunitMap() throws RemoteException;
	public Map<Integer, SpTypeSignal> getSpTypeSignalMap() throws RemoteException;
	public List<SpTuCommand> getSpTuCommand() throws RemoteException;
	public Map<Integer, Tuser> getTuserMap() throws RemoteException;
	
	public List<LinkedValue> getData(int idSignal) throws RemoteException;
	public List<LinkedValue> getDataArc(int idSignal, Timestamp dtBeg, Timestamp dtEnd) throws RemoteException;
	public List<LinkedValue> getDataIntegr(int idSignal, Timestamp dtBeg, Timestamp dtEnd, int period) throws RemoteException;
	public List<LinkedValue> getDataIntegrArc(int idSignal, Timestamp dtBeg, Timestamp dtEnd, int period) throws RemoteException;
	
	public Object getTransparantById(int idTransparant) throws RemoteException;
	public Map<Integer, Transparant> getTransparants() throws RemoteException;
	public List<Ttransparant> getTtransparantsActive(int idScheme) throws RemoteException;
	public List<Ttransparant> getTtransparantsNew(Timestamp settime) throws RemoteException;
	public TtranspLocate getTransparantLocate(int trref) throws RemoteException;
	public List<Ttransparant> getTtransparantsClosed(Timestamp closetime) throws RemoteException;
	public List<Ttransparant> getTtransparantsUpdated(Timestamp lastupdate) throws RemoteException;
	
	public void insertTtransparant(int idtr, int signref, String objname, int tp, int schemeref) throws RemoteException;
	public void insertTtranspHistory(int trref, int userref, String txt, int trtype) throws RemoteException;
	public void deleteTtranspLocate(int trref, int scref) throws RemoteException;
	public void insertTtranspLocate(int trref, int scref, int x, int y, int h, int w) throws RemoteException;
	public int getMaxTranspID() throws RemoteException;
	public void updateTtransparantLastUpdate(int idtr) throws RemoteException;
	public void updateTtranspLocate(int trref, int scref, int x, int y, int h, int w) throws RemoteException;
	public void updateTtransparantCloseTime(int idtr) throws RemoteException;
	public TtranspHistory getTtranspHistory(int trref) throws RemoteException;
	public void updateTtranspHistory(int trref, String txt) throws RemoteException;
	public Ttransparant getTtransparantById(int idtr) throws RemoteException;
	
	public List<ControlJournalItem> getJContrlItems(Timestamp dtBeg, Timestamp dtEnd) throws RemoteException;
	void setBaseVal(int idSignal, double val) throws RemoteException;
	void updateTsignalStatus(int idSignal, int status) throws RemoteException;
	
	void insertDeventLog(int eventtype, int objref, Timestamp eventdt, double objval, int objstatus, int authorref) throws RemoteException;
	
	List<UserEventJournalItem> getUserEventJournalItems(Timestamp dtBeg, Timestamp dtEnd) throws RemoteException;
} 