package controllers.journals;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;

import javafx.stage.Stage;
import pr.log.LogFiles;
import pr.model.NormalModeJournalItem;
import single.SingleFromDB;
import single.SingleObject;
import ui.tables.NormalModeTableItem;

public class JNormalModeController extends AJournal {
	
	@Override
	public void setItems(Timestamp dtBeg, Timestamp dtEnd) {
		bpTableController.clearTable();
		
		try {
			List<NormalModeJournalItem> items = SingleFromDB.psClient.getListNormalModeItems(dtBeg, dtEnd, SingleObject.activeSchemeSignals);
			items.forEach(it -> bpTableController.addItem(new NormalModeTableItem(it)));
		} catch (RemoteException e) {
			LogFiles.log.log(Level.SEVERE, "void setItems(...)", e);
		}
	}
	
	@Override
	public void setElementText(ResourceBundle rb) {
		super.setElementText(rb);
		
		if (getTbJournal().getScene() != null) 
			((Stage)getTbJournal().getScene().getWindow()).setTitle(rb.getString("key_miJNormalMode"));
	}
}
