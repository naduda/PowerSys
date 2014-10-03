package pr.powersys;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import pr.model.Alarm;
import pr.model.ConfTree;
import pr.model.DvalTI;
import pr.model.DvalTS;
import pr.model.LinkedValue;
import pr.model.SPunit;
import pr.model.TSysParam;
import pr.model.TViewParam;
import pr.model.Transparant;
import pr.model.Tsignal;
import pr.model.TtranspLocate;
import pr.model.Ttransparant;
      
public interface IPowersys extends Remote {
	public static final int RMI_PORT = 1099;
	public static final String bindString = String.format("rmi://localhost:%s/PowerSysService", RMI_PORT);
	
	public Map<Integer, Tsignal> getTsignalsMap() throws RemoteException;
	public Map<Integer, ConfTree> getConfTreeMap() throws RemoteException;
	public List<Alarm> getAlarmsCurrentDay() throws RemoteException;
	public Map<String, TSysParam> getTSysParam(String paramname) throws RemoteException;
	public List<TViewParam> getTViewParam(String objdenom, String paramdenom, int userref) throws RemoteException;
	public void setTS(int idsignal, double val, int schemeref) throws RemoteException;
	public Map<Integer, DvalTI> getOldTI() throws RemoteException;
	public Map<Integer, DvalTS> getOldTS() throws RemoteException;
	public void confirmAlarm(Timestamp recorddt, Timestamp eventdt, int objref, Timestamp confirmdt, String lognote, int userref) throws RemoteException;
	public void confirmAlarmAll(String lognote, int userref) throws RemoteException;
	public Map<Integer, SPunit> getSPunitMap() throws RemoteException;
	
	public List<LinkedValue> getData(int idSignal) throws RemoteException;
	public List<LinkedValue> getDataArc(int idSignal, Timestamp dtBeg, Timestamp dtEnd) throws RemoteException;
	public List<LinkedValue> getDataIntegr(int idSignal, Timestamp dtBeg, Timestamp dtEnd, int period) throws RemoteException;
	public List<LinkedValue> getDataIntegrArc(int idSignal, Timestamp dtBeg, Timestamp dtEnd, int period) throws RemoteException;
	
	public Object getTransparantById(int idTransparant) throws RemoteException;
	public Map<Integer, Transparant> getTransparants() throws RemoteException;
	public List<Ttransparant> getTtransparantsActive(int idScheme) throws RemoteException;
	public List<Ttransparant> getTtransparantsNew(Timestamp settime) throws RemoteException;
	public TtranspLocate getTransparantLocate(int trref) throws RemoteException;
} 