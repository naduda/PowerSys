package proxyPowersys;
  
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;

import actualdata.LastData;
import jdbc.PostgresDB;
import powersys.IPowersys;
import model.Alarm;
import model.ConfTree;
import model.Tsignal;

public class PowerSys extends UnicastRemoteObject  implements IPowersys {
	private static final long serialVersionUID = 1L;
	
	private PostgresDB pdb;
	
	public PowerSys(PostgresDB pdb) throws RemoteException {
		super();
		LocateRegistry.createRegistry(1099);
		this.pdb = pdb;
	}

	@Override
	public Map<Integer, Tsignal> getTsignalsMap() throws RemoteException {
		return pdb.getTsignalsMap();
	}

	@Override
	public Map<Integer, ConfTree> getConfTreeMap() throws RemoteException {
		return pdb.getConfTreeMap();
	}

	@Override
	public List<Alarm> getAlarmsCurrentDay() throws RemoteException {
		return LastData.getAlarms();
	}  
}
 
