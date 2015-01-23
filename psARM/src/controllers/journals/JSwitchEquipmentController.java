package controllers.journals;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import pr.model.SwitchEquipmentJournalItem;
import single.SingleFromDB;
import single.SingleObject;
import ui.tables.SwitchEquipmentTableItem;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class JSwitchEquipmentController extends TableController {
	@FXML private Label lCount;
	@FXML private Text tCount;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		super.initialize(location, resources);
		
		List<SwitchEquipmentJournalItem> items = SingleFromDB.psClient.getSwitchJournalItems(SingleObject.activeSchemeSignals);
		items.forEach(it -> addItem(new SwitchEquipmentTableItem(it)));
		tCount.setText(items.size() + "");
	}
	
	@Override
	public void setElementText(ResourceBundle rb) {
		super.setElementText(rb);
		lCount.setText(rb.getString("keyCount"));
		if (tCount.getScene() != null) 
			((Stage)tCount.getScene().getWindow()).setTitle(rb.getString("key_miJNormalMode"));
	}
}
