package proxyPowersys;
  
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;





import java.util.stream.Collectors;

import actualdata.LastData;
import jdbc.PostgresDB;
import powersys.IPowersys;
import model.Alarm;
import model.ConfTree;
import model.DvalTI;
import model.LinkedValue;
import model.TSysParam;
import model.TViewParam;
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

	@Override
	public Map<String, TSysParam> getTSysParam(String paramname) throws RemoteException {
		Map<String, TSysParam> ret = new HashMap<>();
		LastData.getTsysparmams().stream().filter(it -> it.getParamname().equals(paramname)).forEach(it -> { ret.put(it.getVal(), it); });
		return ret;
	}

	@Override
	public List<TViewParam> getTViewParam(String objdenom, String paramdenom, int userref) throws RemoteException {
		return LastData.getTviewparams().stream().filter(it -> it.getObjdenom().equals(objdenom)).
			filter(it -> it.getParamdenom().equals(paramdenom)).filter(it -> it.getUserref() == userref).collect(Collectors.toList());
	}

	@Override
	public void setTS(int idsignal, double val, int schemeref) throws RemoteException {
		pdb.setTS(idsignal, val, schemeref);
	}

	@Override
	public List<DvalTI> getOldTI() throws RemoteException {
		return LastData.getOldTI();
	}

	@Override
	public Map<Integer, LinkedValue> getOldTS() throws RemoteException {
		return LastData.getOldTS();
	}

}
 
