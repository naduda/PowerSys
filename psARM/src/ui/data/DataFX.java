package ui.data;

import java.rmi.RemoteException;
import java.util.List;

import ui.MainStage;
import pr.model.LinkedValue;
import pr.model.SPunit;
import pr.model.Tsignal;
import javafx.scene.layout.StackPane;

public class DataFX {

	private List<LinkedValue> data;
	private Tsignal signal;
	private SPunit spunit;
	
	public DataFX(int idSignal) {
		try {
			data = MainStage.psClient.getData(idSignal);
			signal = MainStage.psClient.getTsignalsMap().get(idSignal);
			spunit = MainStage.psClient.getSPunitMap().get(signal.getUnitref());
			data.forEach(v -> {v.setVal((double)v.getVal() * signal.getKoef());});
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public StackPane getChart() {
		return new LineChartContainer("Title", signal.getNamesignal(), "Time", "Value, " + spunit.getNameunit(), data);
	}
	
	public List<LinkedValue> getData() {
		return data;
	}

	public void setData(List<LinkedValue> data) {
		this.data = data;
	}

	public Tsignal getSignal() {
		return signal;
	}
}
