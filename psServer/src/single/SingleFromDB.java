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

import jdbc.PostgresDB;
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
	public final static Map<Integer, Tsignal> signals = new HashMap<>();
	private static SQLConnect sqlConnect;
	private Timestamp dt = null;
	
	@SuppressWarnings("unchecked")
	public SingleFromDB(PostgresDB pdb) {
		final ExecutorService service = Executors.newFixedThreadPool(5);
		LogFiles.log.log(Level.INFO, "Start reading DB");
		
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
			try {
				dt = new Timestamp(formatter.parse(formatter.format(new Date())).getTime()); // 00 min, 00 sec
			} catch (ParseException e) {
				LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
			}
			
			futures.put("alarms", service.submit(() -> pdb.getAlarms(dt)));
			futures.put("tsysparmams", service.submit(() -> pdb.getTSysParam()));
			futures.put("tviewparams", service.submit(() -> pdb.getTViewParam()));
			futures.put("transparants", service.submit(() -> pdb.getTransparants()));
			futures.put("signals", service.submit(() -> pdb.getTsignalsMap()));
			
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
}
