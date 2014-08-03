package actualdata;

import java.util.ArrayList;
import java.util.List;

import model.Alarm;

public class LastData {

	private static long lastDT = 0;
	
	private static List<Alarm> alarms = new ArrayList<>();
	
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
}
