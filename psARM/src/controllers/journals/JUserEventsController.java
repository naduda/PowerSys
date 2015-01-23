package controllers.journals;

import java.sql.Timestamp;
import java.util.List;
import java.util.ResourceBundle;

import javafx.stage.Stage;
import pr.model.UserEventJournalItem;
import single.SingleFromDB;
import ui.tables.UserEventTableItem;

public class JUserEventsController extends AJournal {

	@Override
	void setItems(Timestamp dtBeg, Timestamp dtEnd) {
		bpTableController.clearTable();
		List<UserEventJournalItem> items = SingleFromDB.psClient.getUserEventJournalItems(dtBeg, dtEnd);
		items.forEach(it -> bpTableController.addItem(new UserEventTableItem(it)));
	}
	
	@Override
	public void setElementText(ResourceBundle rb) {
		super.setElementText(rb);
		
		if (getTbJournal().getScene() != null) 
			((Stage)getTbJournal().getScene().getWindow()).setTitle(rb.getString("key_miJNormalMode"));
	}
}
