package topic.messagelisteners;

import pr.model.DvalTS;
import ui.MainStage;

public class TSMessageListener extends AbstarctMessageListener {

	@Override
	void runLogic(Object obj) {
		MainStage.controller.updateTI((DvalTS) obj);
	}

}