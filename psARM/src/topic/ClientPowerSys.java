package topic;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import pr.log.LogFiles;
import pr.model.Alarm;
import pr.model.ConfTree;
import pr.model.ControlJournalItem;
import pr.model.DvalTI;
import pr.model.DvalTS;
import pr.model.LinkedValue;
import pr.model.NormalModeJournalItem;
import pr.model.SPunit;
import pr.model.SpTuCommand;
import pr.model.SpTypeSignal;
import pr.model.SwitchEquipmentJournalItem;
import pr.model.TSysParam;
import pr.model.TViewParam;
import pr.model.Transparant;
import pr.model.Tsignal;
import pr.model.TtranspHistory;
import pr.model.TtranspLocate;
import pr.model.Ttransparant;
import pr.model.Tuser;
import pr.model.UserEventJournalItem;
import pr.model.VsignalView;
import pr.powersys.IPowersys;
import pr.powersys.MySocketFactory;
import single.SingleObject;

public class ClientPowerSys implements IPowersys {	
	private IPowersys myServer;
	
	public ClientPowerSys() {
		try {
			MySocketFactory.setServer(SingleObject.ipAddress);
			myServer = (IPowersys) Naming.lookup(String.format("rmi://%s:%s/PowerSysService", SingleObject.ipAddress, IPowersys.RMI_PORT));
		} catch (NotBoundException | RemoteException | MalformedURLException e) {
			LogFiles.log.log(Level.WARNING, "PowerSysService is stoped");
			LogFiles.log.log(Level.INFO, "Exit ...");
			System.exit(0);
		}
	}
	
//	==============================================================================
	@Override
	public void update(String query) {
		try {
			myServer.update(query);
		} catch (RemoteException e) {
			LogFiles.log.log(Level.SEVERE, "void update(...)", e);
		}
	}
	
	@Override
	public List<NormalModeJournalItem> getListNormalModeItems(String query) throws RemoteException {
		return myServer.getListNormalModeItems(query);
	}
	
	@Override
	public List<SwitchEquipmentJournalItem> getSwitchJournalItems(String query) throws RemoteException {
		return myServer.getSwitchJournalItems(query);
	}
//	==============================================================================
	public Map<Integer, Tsignal> getTsignalsMap() {
		try {
			return myServer.getTsignalsMap();
		} catch (RemoteException e) {
			LogFiles.log.log(Level.SEVERE, "Map<Integer, Tsignal> getTsignalsMap()", e);
		}
		return null;
	}

	@Override
	public Map<Integer, ConfTree> getConfTreeMap() throws RemoteException {
		try {
			return myServer.getConfTreeMap();
		} catch (RemoteException e) {
			LogFiles.log.log(Level.SEVERE, "Map<Integer, Tsignal> getConfTreeMap()", e);
		}
		return null;
	}

	@Override
	public List<Alarm> getAlarmsCurrentDay() throws RemoteException {
		try {
			return myServer.getAlarmsCurrentDay();
		} catch (RemoteException e) {
			LogFiles.log.log(Level.SEVERE, "List<Alarm> getAlarmsCurrentDay()", e);
		}
		return new ArrayList<>();
	}

	@Override
	public Map<String, TSysParam> getTSysParam(String paramname) throws RemoteException {
		try {
			return myServer.getTSysParam(paramname);
		} catch (RemoteException e) {
			LogFiles.log.log(Level.SEVERE, "Map<String, TSysParam> getTSysParam(String paramname)", e);
		}
		return null;
	}

	@Override
	public List<TViewParam> getTViewParam(String objdenom, String paramdenom, int userref) throws RemoteException {
		try {
			return myServer.getTViewParam(objdenom, paramdenom, userref);
		} catch (RemoteException e) {
			LogFiles.log.log(Level.SEVERE, "List<TViewParam> getTViewParam(...)", e);
		}
		return null;
	}

	@Override
	public void setTS(int idsignal, double val, int schemeref) throws RemoteException {
		myServer.setTS(idsignal, val, schemeref);
	}

	@Override
	public Map<Integer, DvalTI> getOldTI() {
		long st = System.currentTimeMillis();
		try {
			Map<Integer, DvalTI> rez = myServer.getOldTI();
			LogFiles.log.log(Level.INFO, "Get oldTI values ==> " + (System.currentTimeMillis() - st) / 1000);
			return rez;
		} catch (RemoteException e) {
			LogFiles.log.log(Level.SEVERE, "Map<Integer, DvalTI> getOldTI()", e);
		}
		LogFiles.log.log(Level.INFO, "Get oldTI values ==> " + (System.currentTimeMillis() - st) / 1000);
		System.out.println((System.currentTimeMillis() - st) / 1000);
		return new HashMap<>();
	}

	@Override
	public Map<Integer, DvalTS> getOldTS() {
		long st = System.currentTimeMillis();
		try {
			Map<Integer, DvalTS> rez = myServer.getOldTS();
			LogFiles.log.log(Level.INFO, "Get oldTS values ==> " + (System.currentTimeMillis() - st) / 1000);
			return rez;
		} catch (RemoteException e) {
			LogFiles.log.log(Level.SEVERE, "Map<Integer, DvalTS> getOldTS()", e);
		}
		return new HashMap<>();
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
			LogFiles.log.log(Level.SEVERE, "Map<Integer, Transparant> getTransparants()", e);
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

	@Override
	public TtranspHistory getTtranspHistory(int trref) throws RemoteException {
		return myServer.getTtranspHistory(trref);
	}

	@Override
	public void updateTtranspHistory(int trref, String txt) throws RemoteException {
		myServer.updateTtranspHistory(trref, txt);
	}

	@Override
	public Ttransparant getTtransparantById(int idtr) throws RemoteException {
		return myServer.getTtransparantById(idtr);
	}

	@Override
	public List<SpTuCommand> getSpTuCommand() {
		try {
			return myServer.getSpTuCommand();
		} catch (RemoteException e) {
			LogFiles.log.log(Level.SEVERE, "List<SpTuCommand> getSpTuCommand()", e);
			return null;
		}
	}

	@Override
	public Map<Integer, Tuser> getTuserMap() {
		try {
			return myServer.getTuserMap();
		} catch (RemoteException e) {
			LogFiles.log.log(Level.SEVERE, "Map<Integer, Tuser> getTuserMap()", e);
			return null;
		}
	}

	@Override
	public List<Alarm> getAlarmsPeriod(Timestamp dtBeg, Timestamp dtEnd) throws RemoteException {
		return myServer.getAlarmsPeriod(dtBeg, dtEnd);
	}
	
	@Override
	public List<Alarm> getAlarmsPeriodById(Timestamp dtBeg, Timestamp dtEnd, int idSignal) throws RemoteException {
		return myServer.getAlarmsPeriodById(dtBeg, dtEnd, idSignal);
	}

	@Override
	public List<ControlJournalItem> getJContrlItems(Timestamp dtBeg, Timestamp dtEnd) throws RemoteException {
		return myServer.getJContrlItems(dtBeg, dtEnd);
	}

	@Override
	public Map<Integer, VsignalView> getVsignalViewMap() {
		try {
			return myServer.getVsignalViewMap();
		} catch (RemoteException e) {
			LogFiles.log.log(Level.SEVERE, "Map<Integer, VsignalView> getVsignalViewMap()", e);
		}
		return null;
	}

	@Override
	public void setBaseVal(int idSignal, double val) throws RemoteException {
		myServer.setBaseVal(idSignal, val);
	}

	@Override
	public void updateTsignalStatus(int idSignal, int status) throws RemoteException {
		myServer.updateTsignalStatus(idSignal, status);
	}

	@Override
	public void insertDeventLog(int eventtype, int objref, Timestamp eventdt, double objval, int objstatus, int authorref) throws RemoteException {
		myServer.insertDeventLog(eventtype, objref, eventdt, objval, objstatus, authorref);
	}

	@Override
	public List<UserEventJournalItem> getUserEventJournalItems(Timestamp dtBeg, Timestamp dtEnd) throws RemoteException {
		return myServer.getUserEventJournalItems(dtBeg, dtEnd);
	}

	@Override
	public Map<Integer, SpTypeSignal> getSpTypeSignalMap() {
		try {
			return myServer.getSpTypeSignalMap();
		} catch (RemoteException e) {
			LogFiles.log.log(Level.SEVERE, "Map<Integer, SpTypeSignal> getSpTypeSignalMap()", e);
			LogFiles.log.log(Level.INFO, "Exit ...");
			System.exit(0);
		}
		return null;
	}

} 
