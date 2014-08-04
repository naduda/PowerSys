package powersys;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import model.Alarm;
import model.ConfTree;
import model.DvalTI;
import model.LinkedValue;
import model.TSysParam;
import model.TViewParam;
import model.Tsignal;
      
public interface IPowersys extends Remote {
	public Map<Integer, Tsignal> getTsignalsMap() throws RemoteException;
	public Map<Integer, ConfTree> getConfTreeMap() throws RemoteException;
	public List<Alarm> getAlarmsCurrentDay() throws RemoteException;
	public Map<String, TSysParam> getTSysParam(String paramname) throws RemoteException;
	public List<TViewParam> getTViewParam(String objdenom, String paramdenom, int userref) throws RemoteException;
	public void setTS(int idsignal, double val, int schemeref) throws RemoteException;
	public List<DvalTI> getOldTI() throws RemoteException;
	public Map<Integer, LinkedValue> getOldTS() throws RemoteException;
} 