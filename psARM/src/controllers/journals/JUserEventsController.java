package controllers.journals;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.List;
import java.util.logging.Level;

import pr.log.LogFiles;
import pr.model.UserEventJournalItem;
import single.SingleFromDB;
import ui.tables.UserEventTableItem;

public class JUserEventsController extends AJournal {

	@Override
	void setItems(Timestamp dtBeg, Timestamp dtEnd) {
		bpTableController.clearTable();
		try {
			List<UserEventJournalItem> items = SingleFromDB.psClient.getUserEventJournalItems(dtBeg, dtEnd);
			items.forEach(it -> bpTableController.addItem(new UserEventTableItem(it)));
		} catch (RemoteException e) {
			LogFiles.log.log(Level.SEVERE, "void setItems(...)", e);
		}
	}

}
