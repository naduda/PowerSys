package actualdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Alarm;
import model.ConfTree;
import model.DvalTI;
import model.DvalTS;
import model.SPunit;
import model.TSysParam;
import model.TViewParam;
import model.Tsignal;

public class LastData {

	private static long lastDT = 0;
	
	private static Map<Integer, Tsignal> signals = new HashMap<>();
	private static Map<Integer, SPunit> spunits = new HashMap<>();
	private static Map<Integer, ConfTree> confTree = new HashMap<>();
	private static List<Alarm> alarms = new ArrayList<>();
	private static List<TSysParam> tsysparmams = new ArrayList<>();
	private static List<TViewParam> tviewparams = new ArrayList<>();
	private static Map<Integer, DvalTI> oldTI = new HashMap<>();
	private static Map<Integer, DvalTS> oldTS = new HashMap<>();
	
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

	public static Map<Integer, SPunit> getSpunits() {
		return spunits;
	}

	public static void setSpunits(Map<Integer, SPunit> spunits) {
		LastData.spunits = spunits;
	}

	public static List<TViewParam> getTviewparams() {
		return tviewparams;
	}

	public static void setTviewparams(List<TViewParam> tviewparams) {
		LastData.tviewparams = tviewparams;
	}

	public static Map<Integer, DvalTI> getOldTI() {
		return oldTI;
	}

	public static void setOldTI(Map<Integer, DvalTI> oldTI) {
		LastData.oldTI = oldTI;
	}

	public static Map<Integer, DvalTS> getOldTS() {
		return oldTS;
	}

	public static void setOldTS(Map<Integer, DvalTS> oldTS) {
		LastData.oldTS = oldTS;
	}

	public static Map<Integer, Tsignal> getSignals() {
		return signals;
	}

	public static void setSignals(Map<Integer, Tsignal> signals) {
		LastData.signals = signals;
	}

	public static Map<Integer, ConfTree> getConfTree() {
		return confTree;
	}

	public static void setConfTree(Map<Integer, ConfTree> confTree) {
		LastData.confTree = confTree;
	}
}
