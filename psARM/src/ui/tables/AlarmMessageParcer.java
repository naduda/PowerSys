package ui.tables;

import java.util.stream.Collectors;
import pr.model.Alarm;
import ui.MainStage;

public class AlarmMessageParcer {

	public static void setAlarmMessage(Alarm a) {
		parsVF(a);
		parsVS(a);
	}
	
	private static String cutMessage(String s, String param) {
		String primary = s;
		s = s.toLowerCase();
		String ret = "|";
		try {
			int startIndex = s.indexOf("<" + param);
			ret = s.substring(startIndex);
			int endIndex = ret.indexOf(">") + 1;
			ret = ret.substring(0, endIndex);
			endIndex = startIndex + endIndex;
			ret = ret + "|" + primary.substring(startIndex, endIndex);
		} catch (Exception e1) {
			ret = "|";
		}
		return ret;
	}
	
	private static void parsVF(Alarm a) {
		String sAlarmMes = a.getAlarmmes();
		String mes = cutMessage(sAlarmMes, "vf");
		if (mes.length() < 2) return;
		String[] arr = mes.split("\\|");
		
		mes = arr[0];
		String toReplace = arr[1];		
		
		try {
			int ind = mes.indexOf(":" + (int)a.getObjval());
			if (ind < 0) {
				mes = mes.substring(mes.lastIndexOf("=") + 1, mes.lastIndexOf("::"));
			} else {
				mes = mes.substring(ind + 1);
				mes = mes.substring(mes.indexOf("=") + 1, mes.indexOf(":"));
			}
			a.setAlarmmes(sAlarmMes.replaceAll(toReplace, mes));
		} catch (Exception e) {
			a.setAlarmmes(sAlarmMes);
			System.err.println(sAlarmMes + " / " + a.getObjval());
		}
	}
	
	private static void parsVS(Alarm a) {
		String sAlarmMes = a.getAlarmmes();
		String mes = cutMessage(sAlarmMes, "vs");
		if (mes.length() < 2) return;
		String[] arr = mes.split("\\|");
		
		mes = arr[0];
		String toReplace = arr[1];
		
		try {
			mes = MainStage.psClient.getTSysParam("SIGNAL_STATUS").values().stream()
			.filter(f -> Integer.parseInt(f.getVal()) == (int)a.getObjval())
			.collect(Collectors.toList()).get(0).getParamdescr();
			
			a.setAlarmmes(sAlarmMes.replaceAll(toReplace, mes));
		} catch (Exception e) {
			System.out.println("error = " + e.getMessage() + "   : " + sAlarmMes);
		}
	}
}
