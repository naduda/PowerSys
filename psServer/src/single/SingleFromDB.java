package single;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;

import jdbc.BatisJDBC;
import jdbc.PostgresDB;
import jdbc.mappers.IMapper;
import jdbc.mappers.IMapperT;
import pr.log.LogFiles;
import pr.model.Alarm;
import pr.model.Report;
import pr.model.TSysParam;
import pr.model.TViewParam;
import pr.model.Transparant;
import pr.model.Tsignal;

public class SingleFromDB {
	private final Map<String, Future<Object>> futures = new HashMap<>();
	private final Map<String, Object> results = new HashMap<>();
	
	private static long lastDT = 0;
	
	private final static List<Alarm> alarms = new ArrayList<>();
	private final static List<TSysParam> tsysparmams = new ArrayList<>();
	private final static List<TViewParam> tviewparams = new ArrayList<>();
	private final static Map<Integer, Transparant> transparants = new HashMap<>();
	private final static Map<Integer, Report> reports = new HashMap<>();
	private final static Map<Integer, Tsignal> signals = new HashMap<>();
	private static SQLConnect sqlConnect;
	private Timestamp dt = null;
	private static PostgresDB pdb;
	
	@SuppressWarnings("unchecked")
	public SingleFromDB() {
		pdb = getPdb();
		final ExecutorService service = Executors.newFixedThreadPool(5);
		LogFiles.log.log(Level.INFO, "Start reading DB");
		
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
			try {
				dt = new Timestamp(formatter.parse(formatter.format(new Date())).getTime()); // 00 min, 00 sec
			} catch (ParseException e) {
				LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
			}
			
			futures.put("alarms", service.submit(() -> 
				new BatisJDBC(s -> s.getMapper(IMapper.class).getAlarms(dt)).get()));
			futures.put("tsysparmams", service.submit(() -> 
				new BatisJDBC(s -> s.getMapper(IMapperT.class).getTSysParam()).get()));
			futures.put("tviewparams", service.submit(() -> 
				new BatisJDBC(s -> s.getMapper(IMapperT.class).getTViewParam()).get()));
			futures.put("transparants", service.submit(() -> 
				new BatisJDBC(s -> s.getMapper(IMapperT.class).getTransparants()).get()));
			futures.put("signals", service.submit(() -> 
				new BatisJDBC(s -> s.getMapper(IMapperT.class).getTsignalsMap()).get()));
			
			for (String k : futures.keySet()) {
				Future<Object> f = futures.get(k);
				results.put(k, f.get());
				LogFiles.log.log(Level.INFO, k + " finished.");
			}
			
			((List<Alarm>)results.get("alarms")).forEach(SingleFromDB::addAlarm);
			tsysparmams.addAll((Collection<? extends TSysParam>) results.get("tsysparmams"));
			tviewparams.addAll((Collection<? extends TViewParam>) results.get("tviewparams"));
			transparants.putAll((Map<? extends Integer, ? extends Transparant>) results.get("transparants"));
			signals.putAll((Map<? extends Integer, ? extends Tsignal>) results.get("signals"));
		} catch (InterruptedException | ExecutionException e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
		} finally {
			service.shutdown();
		}
	}
	
	public static List<Alarm> getAlarms() {
		return alarms;
	}
	
	public static void addAlarm (Alarm a) {
		if (a.getEventdt().toLocalDateTime().getHour() < lastDT) {
			alarms.clear();
			lastDT = a.getEventdt().toLocalDateTime().getHour();
		}
		alarms.add(a);
	}

	public static List<TSysParam> getTsysparmams() {
		return tsysparmams;
	}

	public static List<TViewParam> getTviewparams() {
		return tviewparams;
	}

	public static Map<Integer, Transparant> getTransparants() {
		return transparants;
	}
	
	public static Map<Integer, Report> getReports() {
		return reports;
	}

	public static SQLConnect getSqlConnect() {
		return sqlConnect;
	}

	public static void setSqlConnect(SQLConnect sqlConnect) {
		SingleFromDB.sqlConnect = sqlConnect;
	}

	public static Map<Integer, Tsignal> getSignals() {
		return signals;
	}

	public static PostgresDB getPdb() {
		if (pdb == null) {
			pdb = new PostgresDB(sqlConnect.getIpAddress(), sqlConnect.getPort(), sqlConnect.getDbName(),
					sqlConnect.getUser(), sqlConnect.getPassword());
		}
		return pdb;
//		...\glassfish\glassfish\lib\gf-client.jar
//		return new PostgresDB("127.0.0.1", "3700", "dimitrovEU");
	}

	public static void setPdb(PostgresDB pdb) {
		SingleFromDB.pdb = pdb;
	}
}
