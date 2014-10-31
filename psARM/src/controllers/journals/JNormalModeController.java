package controllers.journals;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import pr.model.NormalModeJournalItem;
import ui.Main;
import ui.MainStage;
import ui.tables.NormalModeTableItem;

public class JNormalModeController extends AJournal {
	
	@Override
	public void setItems(Timestamp dtBeg, Timestamp dtEnd) {
		bpTableController.clearTable();
		List<String> signalsArr = new ArrayList<String>(1);
		signalsArr.add("");
		Main.mainScheme.getSignalsTS().forEach(s -> {
			signalsArr.set(0, signalsArr.get(0) + s + ",");
		});
		String signals = signalsArr.get(0).substring(0, signalsArr.get(0).length() - 1);
		
		try {
			String query = String.format("select path, nameSignal, val, dt, (select min(dt) from d_arcvalts "
					+ "where signalref = idsignal and dt > d.dt and val <> d.val) dt_new "
					+ "from (select idSignal, signalpath(idSignal) as path, nameSignal, "
					+ "coalesce(getval_ts(idSignal, '%s'::timestamp with time zone), baseval) as val, "
					+ "getdt_ts(idSignal, '%s'::timestamp with time zone) as dt, baseval from t_signal s "
					+ "where idSignal in (0, %s) union all select idSignal, signalpath(idSignal) as path, nameSignal, val, dt, baseval "
					+ "from d_arcvalts join t_signal on signalref = idsignal "
					+ "where idSignal in (0, %s) and dt > '%s' and dt < '%s') d where val <> baseval order by dt desc",
					dtBeg, dtBeg, signals, signals, dtBeg, dtEnd);
			
			List<NormalModeJournalItem> items = MainStage.psClient.getListNormalModeItems(query);
			items.forEach(it -> bpTableController.addItem(new NormalModeTableItem(it)));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

}
