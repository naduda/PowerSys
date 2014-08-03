package topic;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import model.Alarm;
import model.ConfTree;
import model.Tsignal;
import powersys.IPowersys;

public class ClientPowerSys implements IPowersys {
	
	private IPowersys myServer;
	
	public ClientPowerSys() {
		try {
			myServer = (IPowersys) Naming.lookup("rmi://localhost:1099/PowerSysService");
		} catch (NotBoundException | RemoteException | MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public Map<Integer, Tsignal> getTsignalsMap() {
		try {
			return myServer.getTsignalsMap();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Map<Integer, ConfTree> getConfTreeMap() throws RemoteException {
		try {
			return myServer.getConfTreeMap();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<Alarm> getAlarmsCurrentDay() throws RemoteException {
		try {
			return myServer.getAlarmsCurrentDay();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return null;
	}
	
} 
