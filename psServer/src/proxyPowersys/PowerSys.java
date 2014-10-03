package proxyPowersys;
  
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import actualdata.LastData;
import jdbc.PostgresDB;
import pr.powersys.IPowersys;
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

public class PowerSys extends UnicastRemoteObject  implements IPowersys {
	private static final long serialVersionUID = 1L;
	
	private PostgresDB pdb;
	
	public PowerSys(PostgresDB pdb) throws RemoteException {
		super();
		LocateRegistry.createRegistry(RMI_PORT);
		this.pdb = pdb;
	}

	@Override
	public Map<Integer, Tsignal> getTsignalsMap() throws RemoteException {
		return LastData.getSignals();
	}

	@Override
	public Map<Integer, ConfTree> getConfTreeMap() throws RemoteException {
		return LastData.getConfTree();
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
	public Map<Integer, DvalTI> getOldTI() throws RemoteException {
		return LastData.getOldTI();
	}

	@Override
	public Map<Integer, DvalTS> getOldTS() {
		return LastData.getOldTS();
	}

	@Override
	public void confirmAlarm(Timestamp recorddt, Timestamp eventdt, int objref, Timestamp confirmdt, String lognote, int userref) {
		pdb.confirmAlarm(recorddt, eventdt, objref, confirmdt, lognote, userref);
	}
	
	@Override
	public void confirmAlarmAll(String lognote, int userref) {
		pdb.confirmAlarmAll(lognote, userref);
	}

	@Override
	public List<LinkedValue> getData(int idSignal) throws RemoteException {
		return pdb.getData(idSignal);
	}

	@Override
	public Map<Integer, SPunit> getSPunitMap() throws RemoteException {
		return LastData.getSpunits();
	}

	@Override
	public List<LinkedValue> getDataIntegr(int idSignal, Timestamp dtBeg, Timestamp dtEnd, int period) throws RemoteException {
		return pdb.getDataIntegr(idSignal, dtBeg, dtEnd, period);
	}
	
	@Override
	public List<LinkedValue> getDataIntegrArc(int idSignal, Timestamp dtBeg, Timestamp dtEnd, int period) throws RemoteException {
		return pdb.getDataIntegrArc(idSignal, dtBeg, dtEnd, period);
	}

	@Override
	public List<LinkedValue> getDataArc(int idSignal, Timestamp dtBeg, Timestamp dtEnd) throws RemoteException {
		return pdb.getDataArc(idSignal, dtBeg, dtEnd);
	}

	@Override
	public Object getTransparantById(int idTransparant) throws RemoteException {
		return pdb.getTransparantById(idTransparant);
	}

	@Override
	public Map<Integer, Transparant> getTransparants() throws RemoteException {
		return LastData.getTransparants();
	}

	@Override
	public List<Ttransparant> getTtransparantsActive(int idScheme) throws RemoteException {
		return pdb.getTtransparantsActive(idScheme);
	}

	@Override
	public TtranspLocate getTransparantLocate(int trref) throws RemoteException {
		return pdb.getTransparantLocate(trref);
	}

	@Override
	public List<Ttransparant> getTtransparantsNew(Timestamp settime) throws RemoteException {
		return pdb.getTtransparantsNew(settime);
	}
}
 
