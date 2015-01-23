package topic.messagelisteners;

import java.time.LocalTime;
import java.util.logging.Level;

import pr.log.LogFiles;
import pr.model.DvalTI;
import single.ProgramProperty;
import ui.MainStage;

public class NotificationsMessageListener extends AbstarctMessageListener {

	@Override
	void runLogic(Object obj) {
		if (obj instanceof DvalTI) {
			DvalTI dtx = (DvalTI)obj;
			if (!dtx.getTypeSignal().startsWith("tu")) {
				MainStage.controller.updateTI(dtx);
			} else {
				System.out.println("tu = " + dtx.getSignalref() + " -> " + LocalTime.now());
			}
		} else if (obj instanceof String) {
			String typeMessage = obj.toString().toLowerCase().substring(0, obj.toString().indexOf(";"));
			switch (typeMessage) {
				case "device":
					ProgramProperty.tDeviceProperty.set(obj.toString().substring(obj.toString().indexOf(";") + 1));
					break;
				default:
					LogFiles.log.log(Level.INFO, obj.toString());
					break;	
			}
		}
	}

}