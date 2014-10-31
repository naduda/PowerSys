package controllers.journals;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import pr.model.SwitchEquipmentJournalItem;
import ui.Main;
import ui.MainStage;
import ui.tables.SwitchEquipmentTableItem;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

public class JSwitchEquipmentController extends TableController {
	@FXML Label lCount;
	@FXML Text tCount;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		super.initialize(location, resources);
		
		List<String> signalsArr = new ArrayList<String>(1);
		signalsArr.add("");
		Main.mainScheme.getSignalsTS().forEach(s -> {
			signalsArr.set(0, signalsArr.get(0) + s + ",");
		});
		String signals = signalsArr.get(0).substring(0, signalsArr.get(0).length() - 1);
		
		try {
			String query = String.format("select formatvalue(id,val) as txtval , * "
					+ "from (select t.namesignal, (getlast_ts(t.idsignal, null, 0)).* "
					+ "from t_signal t where t.typesignalref = 2 and t.idsignal in (%s)) as t "
					+ "order by t.namesignal", signals);
			
			List<SwitchEquipmentJournalItem> items = MainStage.psClient.getSwitchJournalItems(query);
			items.forEach(it -> addItem(new SwitchEquipmentTableItem(it)));
			tCount.setText(items.size() + "");
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void setElementText(ResourceBundle rb) {
		super.setElementText(rb);
		lCount.setText(rb.getString("keyCount"));
	}
}
