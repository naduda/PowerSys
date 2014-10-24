package topic.messagelisteners;

import controllers.journals.JAlarmsController;
import pr.model.Alarm;
import ui.MainStage;

public class AlarmMessageListener extends AbstarctMessageListener {

	@Override
	void runLogic(Object obj) {
		Alarm a = (Alarm) obj;
		JAlarmsController jAlarmController = MainStage.controller.getMenuBarController().getjAlarmController();
		
    	if (a.getConfirmdt() == null) {
    		MainStage.controller.getAlarmsController().addAlarm(a);
    		if (jAlarmController != null) {
    			jAlarmController.getAlarmsController().addAlarm(a);
    		}
    	} else {
    		MainStage.controller.getAlarmsController().updateAlarm(a);
    		if (jAlarmController != null) {
    			jAlarmController.getAlarmsController().updateAlarm(a);
    		}
    	}
	}

}
