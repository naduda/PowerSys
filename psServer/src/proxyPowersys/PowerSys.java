package proxyPowersys;
  
import java.rmi.RemoteException;
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

public class PowerSys extends UnicastRemoteObject  implements IPowersys {
	private static final long serialVersionUID = 1L;
	
	private PostgresDB pdb;
	
	public PowerSys(PostgresDB pdb) throws RemoteException {
		super();
		this.pdb = pdb;
	}
//	==============================================================================
	@Override
	public void update(String query) throws RemoteException {
		pdb.update(query);
	}
	
	@Override
	public List<NormalModeJournalItem> getListNormalModeItems(String query) throws RemoteException {
		return pdb.getListNormalModeItems(query);
	}
	
	@Override
	public List<SwitchEquipmentJournalItem> getSwitchJournalItems(String query) throws RemoteException {
		return pdb.getSwitchJournalItems(query);
	}
//	==============================================================================

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
	public Map<Integer, DvalTI> getOldTI() throws RemoteException {
		return LastData.getOldTI();
	}

	@Override
	public Map<Integer, DvalTS> getOldTS() throws RemoteException {
		return pdb.getOldTS().stream().filter(it -> it != null).collect(Collectors.toMap(DvalTS::getSignalref, obj -> obj));
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
		return pdb.getSPunitMap();
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

	@Override
	public List<Ttransparant> getTtransparantsClosed(Timestamp closetime) throws RemoteException {
		return pdb.getTtransparantsNew(closetime);
	}

	@Override
	public List<Ttransparant> getTtransparantsUpdated(Timestamp lastupdate) throws RemoteException {
		return pdb.getTtransparantsUpdated(lastupdate);
	}

	@Override
	public void insertTtransparant(int idtr, int signref, String objname, int tp, int schemeref) throws RemoteException {
		pdb.insertTtransparant(idtr, signref, objname, tp, schemeref);
	}

	@Override
	public void insertTtranspHistory(int trref, int userref, String txt, int trtype) throws RemoteException {
		pdb.insertTtranspHistory(trref, userref, txt, trtype);
	}

	@Override
	public void deleteTtranspLocate(int trref, int scref) throws RemoteException {
		pdb.deleteTtranspLocate(trref, scref);
	}

	@Override
	public void insertTtranspLocate(int trref, int scref, int x, int y, int h, int w) throws RemoteException {
		pdb.insertTtranspLocate(trref, scref, x, y, h, w);
	}

	@Override
	public int getMaxTranspID() throws RemoteException {
		return pdb.getMaxTranspID();
	}

	@Override
	public void updateTtransparantLastUpdate(int idtr) throws RemoteException {
		pdb.updateTtransparantLastUpdate(idtr);
	}

	@Override
	public void updateTtranspLocate(int trref, int scref, int x, int y, int h, int w) throws RemoteException {
		pdb.updateTtranspLocate(trref, scref, x, y, h, w);
	}

	@Override
	public void updateTtransparantCloseTime(int idtr) throws RemoteException {
		pdb.updateTtransparantCloseTime(idtr);
	}
	
	@Override
	public TtranspHistory getTtranspHistory(int trref) throws RemoteException {
		return pdb.getTtranspHistory(trref);
	}

	@Override
	public void updateTtranspHistory(int trref, String txt) throws RemoteException {
		pdb.updateTtranspHistory(trref, txt);
	}

	@Override
	public Ttransparant getTtransparantById(int idtr) throws RemoteException {
		return pdb.getTtransparantById(idtr);
	}

	@Override
	public List<SpTuCommand> getSpTuCommand() throws RemoteException {
		return pdb.getSpTuCommand();
	}

	@Override
	public Map<Integer, Tuser> getTuserMap() throws RemoteException {
		return pdb.getTuserMap();
	}

	@Override
	public List<Alarm> getAlarmsPeriod(Timestamp dtBeg, Timestamp dtEnd) throws RemoteException {
		return pdb.getAlarmsPeriod(dtBeg, dtEnd);
	}
	
	@Override
	public List<Alarm> getAlarmsPeriodById(Timestamp dtBeg, Timestamp dtEnd, int idSignal) throws RemoteException {
		return pdb.getAlarmsPeriodById(dtBeg, dtEnd, idSignal);
	}

	@Override
	public List<ControlJournalItem> getJContrlItems(Timestamp dtBeg, Timestamp dtEnd) throws RemoteException {
		return pdb.getJContrlItems(dtBeg, dtEnd);
	}

	@Override
	public Map<Integer, VsignalView> getVsignalViewMap() throws RemoteException {
		return pdb.getVsignalViewMap();
	}

	@Override
	public void setBaseVal(int idSignal, double val) throws RemoteException {
		pdb.setBaseVal(idSignal, val);
	}
	
	@Override
	public void updateTsignalStatus(int idSignal, int status) throws RemoteException {
		pdb.updateTsignalStatus(idSignal, status);
	}
	
	@Override
	public void insertDeventLog(int eventtype, int objref, Timestamp eventdt, double objval, int objstatus, int authorref) throws RemoteException {
		pdb.insertDeventLog(eventtype, objref, eventdt, objval, objstatus, authorref);
	}
	
	@Override
	public List<UserEventJournalItem> getUserEventJournalItems(Timestamp dtBeg, Timestamp dtEnd) throws RemoteException {
		return pdb.getUserEventJournalItems(dtBeg, dtEnd);
	}
	@Override
	public Map<Integer, SpTypeSignal> getSpTypeSignalMap() throws RemoteException {
		return pdb.getSpTypeSignalMap();
	}
}
 
