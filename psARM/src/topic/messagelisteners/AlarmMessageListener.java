package topic.messagelisteners;

import pr.model.Alarm;
import ui.single.ProgramProperty;

public class AlarmMessageListener extends AbstarctMessageListener {

	@Override
	void runLogic(Object obj) {
		ProgramProperty.alarmProperty.set((Alarm) obj);
	}

}
