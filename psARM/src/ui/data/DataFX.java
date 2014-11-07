package ui.data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import pr.model.LinkedValue;
import javafx.scene.layout.StackPane;

public class DataFX {

	private List<LinkedValue> data = new ArrayList<>();
	private List<Integer> idSignals = new ArrayList<>();
	
	public DataFX(List<Integer> idSignals) {
		this.idSignals = idSignals;
	}
		
	private void rewriteID() {
		idSignals.clear();
		List<LinkedValue> dataListChart = new ArrayList<>();
		dataListChart.addAll(data);
		
		while (dataListChart.size() > 0) {
			int idSignal = dataListChart.get(0).getId();
			idSignals.add(idSignal);
			List<LinkedValue> dataSignal = dataListChart.stream().filter(f -> f.getId() == idSignal).collect(Collectors.toList());
			dataListChart.removeAll(dataSignal);
		}
	}
	
	public StackPane getChart() {
		return new LineChartContainer("Title", "Time", data, idSignals);
	}
	
	public List<LinkedValue> getData() {
		return data;
	}

	public void setData(List<LinkedValue> data) {
		this.data = data;
		rewriteID();
	}

	public List<Integer> getIdSignals() {
		return idSignals;
	}

	public void setIdSignals(List<Integer> idSignals) {
		this.idSignals = idSignals;
	}
}
