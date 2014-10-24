package ui.data;

import java.rmi.RemoteException;
import java.util.List;

import ui.MainStage;
import pr.model.LinkedValue;
import pr.model.VsignalView;
import javafx.scene.layout.StackPane;

public class DataFX {

	private List<LinkedValue> data;
	private VsignalView signal;
	
	public DataFX(int idSignal) {
		try {
			data = MainStage.psClient.getData(idSignal);
			signal = MainStage.signals.get(idSignal);
			data.forEach(v -> {v.setVal((double)v.getVal() * signal.getKoef());});
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public StackPane getChart() {
		return new LineChartContainer("Title", signal.getNamesignal(), "Time", "Value, " + signal.getNameunit(), data);
	}
	
	public List<LinkedValue> getData() {
		return data;
	}

	public void setData(List<LinkedValue> data) {
		this.data = data;
	}

	public VsignalView getSignal() {
		return signal;
	}
}
