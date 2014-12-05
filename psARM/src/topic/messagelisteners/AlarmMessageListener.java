package topic.messagelisteners;

import java.rmi.RemoteException;
import java.util.logging.Level;

import pr.log.LogFiles;
import pr.model.Alarm;
import single.ProgramProperty;
import single.SingleFromDB;
import single.SingleObject;

public class AlarmMessageListener extends AbstarctMessageListener {

	@Override
	void runLogic(Object obj) {
		try {
			Alarm hpa = SingleFromDB.psClient.getHightPriorityAlarm();
			SingleObject.alarmActivities.clearActivities();
			ProgramProperty.hightPriorityAlarmProperty.set(hpa);
		} catch (RemoteException e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
		}
		
		ProgramProperty.alarmProperty.set((Alarm) obj);
	}

}
