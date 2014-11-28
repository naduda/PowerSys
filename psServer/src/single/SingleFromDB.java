package single;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pr.model.Alarm;
import pr.model.Report;
import pr.model.TSysParam;
import pr.model.TViewParam;
import pr.model.Transparant;

public class SingleFromDB {

	private static long lastDT = 0;
	
	private static List<Alarm> alarms = new ArrayList<>();
	private static List<TSysParam> tsysparmams = new ArrayList<>();
	private static List<TViewParam> tviewparams = new ArrayList<>();
	private static Map<Integer, Transparant> transparants = new HashMap<>();
	private static Map<Integer, Report> reports = new HashMap<>();
	private static SQLConnect sqlConnect;
	
	public static List<Alarm> getAlarms() {
		return alarms;
	}
	
	public static void addAlarm (Alarm a) {
		if (a.getEventdt().toLocalDateTime().getHour() < lastDT) {
			alarms = new ArrayList<>();
			lastDT = a.getEventdt().toLocalDateTime().getHour();
		}
		alarms.add(a);
	}

	public static List<TSysParam> getTsysparmams() {
		return tsysparmams;
	}

	public static void setTsysparmams(List<TSysParam> tsysparmams) {
		SingleFromDB.tsysparmams = tsysparmams;
	}

	public static List<TViewParam> getTviewparams() {
		return tviewparams;
	}

	public static void setTviewparams(List<TViewParam> tviewparams) {
		SingleFromDB.tviewparams = tviewparams;
	}

	public static Map<Integer, Transparant> getTransparants() {
		return transparants;
	}

	public static void setTransparants(Map<Integer, Transparant> transparants) {
		SingleFromDB.transparants = transparants;
	}
	
	public static Map<Integer, Report> getReports() {
		return reports;
	}

	public static void setReports(Map<Integer, Report> reports) {
		SingleFromDB.reports = reports;
	}

	public static SQLConnect getSqlConnect() {
		return sqlConnect;
	}

	public static void setSqlConnect(SQLConnect sqlConnect) {
		SingleFromDB.sqlConnect = sqlConnect;
	}
}
