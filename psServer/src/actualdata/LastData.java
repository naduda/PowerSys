package actualdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pr.model.Alarm;
import pr.model.TSysParam;
import pr.model.TViewParam;
import pr.model.Transparant;

public class LastData {

	private static long lastDT = 0;
	
	private static List<Alarm> alarms = new ArrayList<>();
	private static List<TSysParam> tsysparmams = new ArrayList<>();
	private static List<TViewParam> tviewparams = new ArrayList<>();
	private static Map<Integer, Transparant> transparants = new HashMap<>();
	
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
		LastData.tsysparmams = tsysparmams;
	}

	public static List<TViewParam> getTviewparams() {
		return tviewparams;
	}

	public static void setTviewparams(List<TViewParam> tviewparams) {
		LastData.tviewparams = tviewparams;
	}

	public static Map<Integer, Transparant> getTransparants() {
		return transparants;
	}

	public static void setTransparants(Map<Integer, Transparant> transparants) {
		LastData.transparants = transparants;
	}
}
