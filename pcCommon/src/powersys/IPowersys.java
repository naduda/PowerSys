package powersys;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import model.Alarm;
import model.ConfTree;
import model.DvalTI;
import model.DvalTS;
import model.TSysParam;
import model.TViewParam;
import model.Tsignal;
      
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
} 