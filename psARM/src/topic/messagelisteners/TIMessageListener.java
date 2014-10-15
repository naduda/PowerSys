package topic.messagelisteners;

import pr.model.DvalTI;
import ui.MainStage;

public class TIMessageListener extends AbstarctMessageListener {

	@Override
	void runLogic(Object obj) {
		MainStage.controller.updateTI((DvalTI) obj);
	}

}
