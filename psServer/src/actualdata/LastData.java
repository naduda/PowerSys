package actualdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Alarm;
import model.DvalTI;
import model.LinkedValue;
import model.TSysParam;
import model.TViewParam;

public class LastData {

	private static long lastDT = 0;
	
	private static List<Alarm> alarms = new ArrayList<>();
	private static List<TSysParam> tsysparmams = new ArrayList<>();
	private static List<TViewParam> tviewparams = new ArrayList<>();
	private static List<DvalTI> oldTI = new ArrayList<>();
	private static Map<Integer, LinkedValue> oldTS = new HashMap<>();
	
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

	public static List<DvalTI> getOldTI() {
		return oldTI;
	}

	public static void setOldTI(List<DvalTI> oldTI) {
		LastData.oldTI = oldTI;
	}

	public static Map<Integer, LinkedValue> getOldTS() {
		return oldTS;
	}

	public static void setOldTS(Map<Integer, LinkedValue> oldTS) {
		LastData.oldTS = oldTS;
	}
	
}
