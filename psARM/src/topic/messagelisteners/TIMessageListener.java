package topic.messagelisteners;

import controllers.ToolBarController;
import pr.model.DvalTI;
import ui.MainStage;

public class TIMessageListener extends AbstarctMessageListener {

	@Override
	void runLogic(Object obj) {
		if (!ToolBarController.showHistoryProperty.get()) MainStage.controller.updateTI((DvalTI) obj);
	}

}
