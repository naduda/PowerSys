package powersys;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import model.Alarm;
import model.ConfTree;
import model.Tsignal;
      
public interface IPowersys extends Remote {
	public Map<Integer, Tsignal> getTsignalsMap() throws RemoteException;
	public Map<Integer, ConfTree> getConfTreeMap() throws RemoteException;
	public List<Alarm> getAlarmsCurrentDay() throws RemoteException;
} 