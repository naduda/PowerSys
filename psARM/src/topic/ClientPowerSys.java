package topic;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
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
import pr.powersys.IPowersys;

public class ClientPowerSys implements IPowersys {
	
	private IPowersys myServer;
	
	public ClientPowerSys() {
		try {
			myServer = (IPowersys) Naming.lookup("rmi://localhost:1099/PowerSysService");
		} catch (NotBoundException | RemoteException | MalformedURLException e) {
			System.err.println("PowerSysService is stoped");
			System.exit(0);
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
		return new ArrayList<Alarm>();
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
	public Map<Integer, DvalTI> getOldTI() throws RemoteException {
		return myServer.getOldTI();
	}

	@Override
	public Map<Integer, DvalTS> getOldTS() throws RemoteException {
		return myServer.getOldTS();
	}

	@Override
	public void confirmAlarm(Timestamp recorddt, Timestamp eventdt, int objref, Timestamp confirmdt, String lognote, int userref) throws RemoteException {
		myServer.confirmAlarm(recorddt, eventdt, objref, confirmdt, lognote, userref);
	}
	
	@Override
	public void confirmAlarmAll(String lognote, int userref) throws RemoteException {
		myServer.confirmAlarmAll(lognote, userref);
	}

	@Override
	public List<LinkedValue> getData(int idSignal) throws RemoteException {
		return myServer.getData(idSignal);
	}

	@Override
	public Map<Integer, SPunit> getSPunitMap() throws RemoteException {
		return myServer.getSPunitMap();
	}

	@Override
	public List<LinkedValue> getDataIntegr(int idSignal, Timestamp dtBeg, Timestamp dtEnd, int period) throws RemoteException {
		return myServer.getDataIntegr(idSignal, dtBeg, dtEnd, period);
	}
	
	@Override
	public List<LinkedValue> getDataIntegrArc(int idSignal, Timestamp dtBeg, Timestamp dtEnd, int period) throws RemoteException {
		return myServer.getDataIntegrArc(idSignal, dtBeg, dtEnd, period);
	}

	@Override
	public List<LinkedValue> getDataArc(int idSignal, Timestamp dtBeg, Timestamp dtEnd) throws RemoteException {
		return myServer.getDataArc(idSignal, dtBeg, dtEnd);
	}

	@Override
	public Object getTransparantById(int idTransparant) throws RemoteException {
		return myServer.getTransparantById(idTransparant);
	}

	@Override
	public Map<Integer, Transparant> getTransparants() {
		try {
			return myServer.getTransparants();
		} catch (RemoteException e) {
			return null;
		}
	}

	@Override
	public List<Ttransparant> getTtransparantsActive(int idScheme) throws RemoteException {
		return myServer.getTtransparantsActive(idScheme);
	}

	@Override
	public TtranspLocate getTransparantLocate(int trref) throws RemoteException {
		return myServer.getTransparantLocate(trref);
	}

	@Override
	public List<Ttransparant> getTtransparantsNew(Timestamp settime) throws RemoteException {
		return myServer.getTtransparantsNew(settime);
	}

	@Override
	public List<Ttransparant> getTtransparantsClosed(Timestamp closetime) throws RemoteException {
		return myServer.getTtransparantsClosed(closetime);
	}

	@Override
	public List<Ttransparant> getTtransparantsUpdated(Timestamp lastupdate) throws RemoteException {
		return myServer.getTtransparantsUpdated(lastupdate);
	}

	@Override
	public void insertTtransparant(int idtr, int signref, String objname, int tp, int schemeref) throws RemoteException {
		myServer.insertTtransparant(idtr, signref, objname, tp, schemeref);
	}

	@Override
	public void insertTtranspHistory(int trref, int userref, String txt, int trtype) throws RemoteException {
		myServer.insertTtranspHistory(trref, userref, txt, trtype);
	}

	@Override
	public void deleteTtranspLocate(int trref, int scref) throws RemoteException {
		myServer.deleteTtranspLocate(trref, scref);
	}

	@Override
	public void insertTtranspLocate(int trref, int scref, int x, int y, int h, int w) throws RemoteException {
		myServer.insertTtranspLocate(trref, scref, x, y, h, w);
	}

	@Override
	public int getMaxTranspID() throws RemoteException {
		return myServer.getMaxTranspID();
	}

	@Override
	public void updateTtransparantLastUpdate(int idtr) throws RemoteException {
		myServer.updateTtransparantLastUpdate(idtr);
	}

	@Override
	public void updateTtranspLocate(int trref, int scref, int x, int y, int h, int w) throws RemoteException {
		myServer.updateTtranspLocate(trref, scref, x, y, h, w);
	}

	@Override
	public void updateTtransparantCloseTime(int idtr) throws RemoteException {
		myServer.updateTtransparantCloseTime(idtr);
	}
} 
