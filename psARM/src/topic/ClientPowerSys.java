package topic;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
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

	@Override
	public Map<String, TSysParam> getTSysParam(String paramname) throws RemoteException {
		try {
			return myServer.getTSysParam(paramname);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<TViewParam> getTViewParam(String objdenom, String paramdenom, int userref) throws RemoteException {
		try {
			return myServer.getTViewParam(objdenom, paramdenom, userref);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void setTS(int idsignal, double val, int schemeref) throws RemoteException {
		myServer.setTS(idsignal, val, schemeref);
	}

	@Override
	public List<DvalTI> getOldTI() throws RemoteException {
		return myServer.getOldTI();
	}

	@Override
	public Map<Integer, LinkedValue> getOldTS() throws RemoteException {
		return myServer.getOldTS();
	}
	
} 
