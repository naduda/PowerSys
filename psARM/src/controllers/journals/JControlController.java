package controllers.journals;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.List;

import pr.model.ControlJournalItem;
import ui.single.SingleFromDB;
import ui.tables.ControlTableItem;

public class JControlController extends AJournal {
	@Override
	public void setItems(Timestamp dtBeg, Timestamp dtEnd) {
		bpTableController.clearTable();
		try {
			List<ControlJournalItem> items = SingleFromDB.psClient.getJContrlItems(dtBeg, dtEnd);
			items.forEach(it -> bpTableController.addItem(new ControlTableItem(it)));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}
