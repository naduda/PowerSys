package controllers.journals;

import java.sql.Timestamp;
import java.util.List;
import java.util.ResourceBundle;

import javafx.stage.Stage;
import pr.model.ControlJournalItem;
import single.SingleFromDB;
import ui.tables.ControlTableItem;

public class JControlController extends AJournal {
	
	@Override
	public void setItems(Timestamp dtBeg, Timestamp dtEnd) {
		bpTableController.clearTable();
		List<ControlJournalItem> items = SingleFromDB.psClient.getJContrlItems(dtBeg, dtEnd);
		items.forEach(it -> bpTableController.addItem(new ControlTableItem(it)));
	}

	@Override
	public void setElementText(ResourceBundle rb) {
		super.setElementText(rb);
		if (getTbJournal().getScene() != null) 
			((Stage)getTbJournal().getScene().getWindow()).setTitle(rb.getString("key_miJControl"));
	}
}
