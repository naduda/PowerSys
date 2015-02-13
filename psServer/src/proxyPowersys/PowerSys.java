package proxyPowersys;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

import jdbc.BatisJDBC;
import jdbc.PostgresDB.BaseMapper;
import jdbc.mappers.IMapper;
import jdbc.mappers.IMapperAction;
import jdbc.mappers.IMapperSP;
import jdbc.mappers.IMapperT;
import jdbc.mappers.IMapperV;
import pr.powersys.IPowersys;
import pr.common.Encryptor;
import pr.common.Utils;
import pr.log.LogFiles;
import pr.model.Alarm;
import pr.model.ChatMessage;
import pr.model.ConfTree;
import pr.model.ControlJournalItem;
import pr.model.DvalTI;
import pr.model.DvalTS;
import pr.model.LinkedValue;
import pr.model.NormalModeJournalItem;
import pr.model.Report;
import pr.model.SPunit;
import pr.model.SpTuCommand;
import pr.model.SpTypeSignal;
import pr.model.SwitchEquipmentJournalItem;
import pr.model.TSysParam;
import pr.model.TViewParam;
import pr.model.TalarmParam;
import pr.model.Tconftree;
import pr.model.Transparant;
import pr.model.Tscheme;
import pr.model.Tsignal;
import pr.model.TtranspHistory;
import pr.model.TtranspLocate;
import pr.model.Ttransparant;
import pr.model.Tuser;
import pr.model.UserEventJournalItem;
import pr.model.VsignalView;
import reports.ReportTools;
import single.SQLConnect;
import single.SingleFromDB;
import single.SingleObject;

@SuppressWarnings("unchecked")
public class PowerSys extends UnicastRemoteObject  implements IPowersys {
	private static final long serialVersionUID = 1L;
	
	public PowerSys() throws RemoteException {
		super();
	}
	
//	==============================================================================
	@Override
	public void sendChatMessage(ChatMessage message) {
		SingleObject.chatTopic.setChatMessage(message);
	}
//	==============================================================================
	@Override
	public String getDBparameters() {
		SQLConnect sqlConnect = SingleFromDB.getSqlConnect();
		return sqlConnect.getUser() + ";" + sqlConnect.getIpAddress() + ":" +
				sqlConnect.getPort() + ";" + sqlConnect.getDbName() + ";" + 
				new Encryptor().encrypt(sqlConnect.getPassword());
	}
//	==============================================================================
	@Override
	public void update(String query) {
		new BatisJDBC(s -> {s.getMapper(BaseMapper.class).update(query);return 0;}).get();
	}
	
	@Override
	public List<NormalModeJournalItem> getListNormalModeItems(Timestamp dtBeg, Timestamp dtEnd, String idSignals) {
		return (List<NormalModeJournalItem>) new BatisJDBC(s -> s.getMapper(IMapper.class)
				.getListNormalModeItems(dtBeg, dtEnd, idSignals)).get();
	}
	
	@Override
	public List<SwitchEquipmentJournalItem> getSwitchJournalItems(String idSignals) {
		return (List<SwitchEquipmentJournalItem>) new BatisJDBC(s -> s.getMapper(IMapper.class)
				.getSwitchJournalItems(idSignals)).get();
	}
	
	@Override
	public Map<Integer, String> getReports() {	
		Map<Integer, Report> reports = new HashMap<>();
		Map<Integer, String> ret = new HashMap<>();
		
		String pathDir = Utils.getFullPath("./Reports");
		File root = new File(pathDir);
		String[] names = root.list();
		
		Arrays.asList(names).forEach(n -> {
			File repDir = new File(pathDir + "/" + n);
			
		    if (repDir.isDirectory()) {
		    	String[] files = repDir.list();
		    	Report rep = new Report();
		    	Arrays.asList(files).forEach(f -> {
					String filePath = pathDir + "/" + n + "/" + f;
		    		String ext = f.toLowerCase().substring(f.indexOf(".") + 1);
		    		String fName = f.substring(0, f.indexOf("."));
		    		
		    		switch (ext) {
					case "jrxml":
						rep.setTemplate(new File(filePath));
						break;
					case "sql":
						String query = "";
						try {
							List<String> strs = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
							for (String s : strs) {
								query = query + s;
							}
						} catch (Exception e) {
							LogFiles.log.log(Level.SEVERE, "List<Report> getReports()", e);
						}
						rep.getQueries().put(fName, query);
						break;
					default:
						break;
					}
		    	});
		    	reports.put(reports.size() + 1, rep);
		    	ret.put(ret.size() + 1, rep.getTemplate().getName().substring(0, rep.getTemplate().getName().indexOf(".")));
		    }
		});
		
		SingleFromDB.getReports().putAll(reports);
		return ret;
	}
	
	@Override
	public String getReportById(int idReport, LocalDate dtBeg, LocalDate dtEnd) {
		ReportTools rt = new ReportTools(SingleFromDB.getSqlConnect());
		return rt.getReportById(idReport, dtBeg, dtEnd);
	}
//	==============================================================================
	@Override
	public Map<Integer, Tscheme> getSchemesMap() {
		return (Map<Integer, Tscheme>) new BatisJDBC(s -> s.getMapper(IMapperT.class).getSchemesMap()).get();
	}
	
	@Override
	public Map<Integer, Tsignal> getTsignalsMap() {
		return SingleFromDB.getSignals();
	}
	
	@Override
	public Map<Integer, Tconftree> getTconftreeMap() {
		return (Map<Integer, Tconftree>) new BatisJDBC(s -> s.getMapper(IMapperT.class).getTconftreeMap()).get();
	}

	@Override
	public Map<Integer, ConfTree> getConfTreeMap() {
		return (Map<Integer, ConfTree>) new BatisJDBC(s -> s.getMapper(IMapperT.class).getConfTreeMap()).get();
	}

	@Override
	public List<Alarm> getAlarmsCurrentDay() {
		return SingleFromDB.getAlarms();
	}

	@Override
	public Map<String, TSysParam> getTSysParam(String paramname) {
		Map<String, TSysParam> ret = new HashMap<>();
//		List<TSysParam> params = (List<TSysParam>) new BatisJDBC(s -> s.getMapper(IMapperT.class).getTSysParam()).get();
//		params.stream().filter(it -> it.getParamname()
//				.equals(paramname)).forEach(it -> ret.put(it.getVal(), it));
		SingleFromDB.getTsysparmams().stream().filter(it -> it.getParamname()
				.equals(paramname)).forEach(it -> ret.put(it.getVal(), it));
		return ret;
	}

	@Override
	public List<TViewParam> getTViewParam(String objdenom, String paramdenom, int userref) {
		return SingleFromDB.getTviewparams().stream().filter(it -> it.getObjdenom().equals(objdenom)).
			filter(it -> it.getParamdenom().equals(paramdenom)).
			filter(it -> it.getUserref() == userref).collect(Collectors.toList());
	}

	@Override
	public void setTS(int idsignal, double val, int rCode, int userId, int schemeref) {
		new BatisJDBC(s -> s.getMapper(IMapper.class).setTS(idsignal, val, rCode, userId, schemeref)).get();
	}
	
	@Override
	public void setTU(int idsignal, double val, int rCode, int userId, int schemeref) throws RemoteException {
		new BatisJDBC(s -> s.getMapper(IMapper.class).setTU(idsignal, val, rCode, userId, schemeref)).get();
	}
	
	@Override
	public void setDevicesState(String iddevices, int state) throws RemoteException {
		new BatisJDBC(s -> {
			s.getMapper(IMapper.class).setDevicesState(iddevices, state);
			return 0;
		}).get();
	}

	@Override
	public List<LinkedValue> getDevicesState(String iddevices) {
		return (List<LinkedValue>) new BatisJDBC(s -> s.getMapper(IMapper.class).getDevicesState(iddevices)).get();
	}

	@Override
	public List<Integer> getActiveDevices(String idSignals) throws RemoteException {
		return (List<Integer>) new BatisJDBC(s -> s.getMapper(IMapper.class).getActiveDevices(idSignals)).get();
	}
	
	@Override
	public int getSendOK(int idsignal) throws RemoteException {
		return (int) new BatisJDBC(s -> s.getMapper(IMapper.class).getSendOK(idsignal)).get();
	}

	@Override
	public Map<Integer, DvalTI> getOldTI(String idSignals) {
		List<DvalTI> listTI = (List<DvalTI>)(new BatisJDBC(s -> s.getMapper(IMapper.class)
				.getOldTI(idSignals).stream().filter(f -> f != null).collect(Collectors.toList()))).get();
		return listTI.stream().collect(Collectors.toMap(DvalTI::getSignalref, obj -> obj));
	}

	@Override
	public Map<Integer, DvalTI> getOldTS(String idSignals) {
		List<DvalTS> listTS = (List<DvalTS>)(new BatisJDBC(s -> s.getMapper(IMapper.class)
				.getOldTS(idSignals).stream().filter(f -> f != null).collect(Collectors.toList()))).get();
		return listTS.stream().collect(Collectors.toMap(DvalTS::getSignalref, obj -> obj));
	}
	
	@Override
	public Map<Integer, DvalTI> getValuesOnDate(String idSignals, Timestamp dtValue) {
		List<DvalTI> list = (List<DvalTI>)(new BatisJDBC(s -> s.getMapper(IMapper.class)
				.getValuesOnDate(idSignals, dtValue).stream().filter(f -> f != null).collect(Collectors.toList()))).get();
		return list.stream().collect(Collectors.toMap(DvalTI::getSignalref, obj -> obj));
	}

	@Override
	public void confirmAlarm(Timestamp recorddt, Timestamp eventdt, int objref, Timestamp confirmdt, String lognote, int userref) {
		new BatisJDBC(s -> {
			s.getMapper(IMapperAction.class).confirmAlarm(recorddt, eventdt, objref, confirmdt, lognote, userref);
			return 0;
		}).get();
	}
	
	@Override
	public void confirmAlarmAll(String lognote, int userref) {
		new BatisJDBC(s -> {s.getMapper(IMapperAction.class).confirmAlarmAll(lognote, userref);return 0;}).get();
	}
	
	@Override
	public List<Integer> getNotConfirmedSignals(String idSignals) {
		return (List<Integer>) new BatisJDBC(s -> s.getMapper(IMapper.class).getNotConfirmedSignals(idSignals)).get();
	}
	
	@Override
	public Alarm getHightPriorityAlarm() {
		return (Alarm) new BatisJDBC(s -> s.getMapper(IMapper.class).getHightPriorityAlarm()).get();
	}
	
	@Override
	public List<TalarmParam> getTalarmParams(int alarmref) {
		return (List<TalarmParam>) new BatisJDBC(s -> s.getMapper(IMapper.class).getTalarmParams(alarmref)).get();
	}
	
	@Override
	public List<LinkedValue> getData(int idSignal) {
		return (List<LinkedValue>) new BatisJDBC(s -> s.getMapper(IMapper.class).getData(idSignal)).get();
	}

	@Override
	public Map<Integer, SPunit> getSPunitMap() {
		return (Map<Integer, SPunit>) new BatisJDBC(s -> s.getMapper(IMapperSP.class).getSPunitMap()).get();
	}

	@Override
	public List<LinkedValue> getDataIntegr(int idSignal, Timestamp dtBeg, Timestamp dtEnd, int period) {
		return (List<LinkedValue>) new BatisJDBC(s -> s.getMapper(IMapper.class).getDataIntegr(idSignal, dtBeg, dtEnd, period)).get();
	}
	
	@Override
	public List<LinkedValue> getDataIntegrArc(int idSignal, Timestamp dtBeg, Timestamp dtEnd, int period) {
		return (List<LinkedValue>) new BatisJDBC(s -> s.getMapper(IMapper.class).getDataIntegrArc(idSignal, dtBeg, dtEnd, period)).get();
	}

	@Override
	public List<LinkedValue> getDataArc(int idSignal, Timestamp dtBeg, Timestamp dtEnd) {
		return (List<LinkedValue>) new BatisJDBC(s -> s.getMapper(IMapper.class).getDataArc(idSignal, dtBeg, dtEnd)).get();
	}

	@Override
	public Object getTransparantById(int idTransparant) {
		return new BatisJDBC(s -> s.getMapper(IMapperT.class).getTransparantById(idTransparant)).get();
	}

	@Override
	public Map<Integer, Transparant> getTransparants() {
		return SingleFromDB.getTransparants();
	}

	@Override
	public List<Ttransparant> getTtransparantsActive(int idScheme) {
		return (List<Ttransparant>) new BatisJDBC(s -> s.getMapper(IMapperT.class).getTtransparantsActive(idScheme)).get();
	}

	@Override
	public TtranspLocate getTransparantLocate(int trref) {
		return (TtranspLocate) new BatisJDBC(s -> s.getMapper(IMapperT.class).getTransparantLocate(trref)).get();
	}

	@Override
	public List<Ttransparant> getTtransparantsNew(Timestamp settime) {
		return (List<Ttransparant>) new BatisJDBC(s -> s.getMapper(IMapperT.class).getTtransparantsNew(settime)).get();
	}

	@Override
	public List<Ttransparant> getTtransparantsClosed(Timestamp closetime) {
		return (List<Ttransparant>) new BatisJDBC(s -> s.getMapper(IMapperT.class).getTtransparantsNew(closetime)).get();
	}

	@Override
	public List<Ttransparant> getTtransparantsUpdated(Timestamp lastupdate) {
		return (List<Ttransparant>) new BatisJDBC(s -> s.getMapper(IMapperT.class).getTtransparantsUpdated(lastupdate)).get();
	}

	@Override
	public void insertTtransparant(int idtr, int signref, String objname, int tp, int schemeref) {
		new BatisJDBC(s -> {
			s.getMapper(IMapperAction.class).insertTtransparant(idtr, signref, objname, tp, schemeref);
			return 0;
		}).get();
	}

	@Override
	public void insertTtranspHistory(int trref, int userref, String txt, int trtype) {
		new BatisJDBC(s -> {
			s.getMapper(IMapperAction.class).insertTtranspHistory(trref, userref, txt, trtype);
			return 0;
		}).get();
	}

	@Override
	public void deleteTtranspLocate(int trref, int scref) {
		new BatisJDBC(s -> {s.getMapper(IMapperAction.class).deleteTtranspLocate(trref, scref);return 0;}).get();
	}

	@Override
	public void insertTtranspLocate(int trref, int scref, int x, int y, int h, int w) {
		new BatisJDBC(s -> {s.getMapper(IMapperAction.class).insertTtranspLocate(trref, scref, x, y, h, w);return 0;}).get();
	}

	@Override
	public int getMaxTranspID() {
		return (int) new BatisJDBC(s -> s.getMapper(IMapperT.class).getMaxTranspID()).get();
	}

	@Override
	public void updateTtransparantLastUpdate(int idtr) {
		new BatisJDBC(s -> {s.getMapper(IMapperAction.class).updateTtransparantLastUpdate(idtr);return 0;}).get();
	}

	@Override
	public void updateTtranspLocate(int trref, int scref, int x, int y, int h, int w) {
		new BatisJDBC(s -> {s.getMapper(IMapperAction.class).updateTtranspLocate(trref, scref, x, y, h, w);return 0;}).get();
	}

	@Override
	public void updateTtransparantCloseTime(int idtr) {
		new BatisJDBC(s -> {s.getMapper(IMapperT.class).updateTtransparantCloseTime(idtr);return 0;}).get();
	}
	
	@Override
	public TtranspHistory getTtranspHistory(int trref) {
		return (TtranspHistory) new BatisJDBC(s -> s.getMapper(IMapperT.class).getTtranspHistory(trref)).get();
	}

	@Override
	public void updateTtranspHistory(int trref, String txt) {
		new BatisJDBC(s -> {s.getMapper(IMapperAction.class).updateTtranspHistory(trref, txt);return 0;}).get();
	}

	@Override
	public Ttransparant getTtransparantById(int idtr) {
		return (Ttransparant) new BatisJDBC(s -> s.getMapper(IMapperT.class).getTtransparantById(idtr)).get();
	}

	@Override
	public List<SpTuCommand> getSpTuCommand() {
		return (List<SpTuCommand>) new BatisJDBC(s -> s.getMapper(IMapperSP.class).getSpTuCommand()).get();
	}

	@Override
	public Map<Integer, Tuser> getTuserMap() {
		return (Map<Integer, Tuser>) new BatisJDBC(s -> s.getMapper(IMapperT.class).getTuserMap()).get();
	}

	@Override
	public List<Alarm> getAlarmsPeriod(Timestamp dtBeg, Timestamp dtEnd) {
		return (List<Alarm>) new BatisJDBC(s -> s.getMapper(IMapper.class).getAlarmsPeriod(dtBeg, dtEnd)).get();
	}
	
	@Override
	public List<Alarm> getAlarmsPeriodById(Timestamp dtBeg, Timestamp dtEnd, int idSignal) {
		return (List<Alarm>) new BatisJDBC(s -> s.getMapper(IMapper.class).getAlarmsPeriodById(dtBeg, dtEnd, idSignal)).get();
	}

	@Override
	public List<ControlJournalItem> getJContrlItems(Timestamp dtBeg, Timestamp dtEnd) {
		return (List<ControlJournalItem>) new BatisJDBC(s -> s.getMapper(IMapper.class).getJContrlItems(dtBeg, dtEnd)).get();
	}

	@Override
	public Map<Integer, VsignalView> getVsignalViewMap() {
		return (Map<Integer, VsignalView>) new BatisJDBC(s -> s.getMapper(IMapperV.class).getVsignalViewMap()).get();
	}

	@Override
	public void setBaseVal(int idSignal, double val) {
		new BatisJDBC(s -> {s.getMapper(IMapperAction.class).setBaseVal(idSignal, val);return 0;}).get();
	}
	
	@Override
	public void updateTsignalStatus(int idSignal, int status) {
		new BatisJDBC(s -> {s.getMapper(IMapperAction.class).updateTsignalStatus(idSignal, status);return 0;}).get();
	}
	
	@Override
	public void insertDeventLog(int eventtype, int objref, Timestamp eventdt, double objval, int objstatus, int authorref) {
		new BatisJDBC(s -> {
			s.getMapper(IMapperAction.class).insertDeventLog(eventtype, objref, eventdt, objval, objstatus, authorref);
			return 0;
		}).get();
	}
	
	@Override
	public List<UserEventJournalItem> getUserEventJournalItems(Timestamp dtBeg, Timestamp dtEnd) {
		return (List<UserEventJournalItem>) new BatisJDBC(s -> s.getMapper(IMapper.class).getUserEventJournalItems(dtBeg, dtEnd)).get();
	}
	
	@Override
	public Map<Integer, SpTypeSignal> getSpTypeSignalMap() {
		return (Map<Integer, SpTypeSignal>) new BatisJDBC(s -> s.getMapper(IMapperSP.class).getSpTypeSignalMap()).get();
	}

	@Override
	public void updateTScheme(int idscheme, String schemedenom, String schemename, 
			String schemedescr, int parentref, Object schemefile, int userid) {
		
		new BatisJDBC(s -> {
			s.getMapper(IMapperAction.class).updateTScheme(idscheme, schemedenom, schemename, schemedescr, parentref, schemefile, userid);
			return 0;
		}).get();
	}

	@Override
	public void deleteScheme(int idscheme) {
		new BatisJDBC(s -> {s.getMapper(IMapperAction.class).deleteScheme(idscheme);return 0;}).get();
	}

	@Override
	public void addScheme(String schemedenom, String schemename, String schemedescr, int parentref, Object schemefile, int userid) {
		new BatisJDBC(s -> {
			s.getMapper(IMapperAction.class).addScheme(schemedenom, schemename, schemedescr, parentref, schemefile, userid);
			return 0;
		}).get();
	}
}
 
