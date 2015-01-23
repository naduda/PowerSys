package topic.messagelisteners;

import pr.model.Alarm;
import single.ProgramProperty;
import single.SingleObject;

public class AlarmMessageListener extends AbstarctMessageListener {

	@Override
	void runLogic(Object obj) {
		SingleObject.alarmActivities.clearActivities();
		
		ProgramProperty.alarmProperty.set((Alarm) obj);
	}

}
