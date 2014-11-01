package controllers.journals;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.List;

import pr.model.UserEventJournalItem;
import ui.MainStage;
import ui.tables.UserEventTableItem;

public class JUserEventsController extends AJournal {

	@Override
	void setItems(Timestamp dtBeg, Timestamp dtEnd) {
		bpTableController.clearTable();
		try {
			List<UserEventJournalItem> items = MainStage.psClient.getUserEventJournalItems(dtBeg, dtEnd);
			items.forEach(it -> bpTableController.addItem(new UserEventTableItem(it)));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

}