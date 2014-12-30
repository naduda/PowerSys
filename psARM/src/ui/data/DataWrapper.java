package ui.data;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import pr.log.LogFiles;
import pr.model.LinkedValue;
import single.SingleFromDB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.StringConverter;

@SuppressWarnings("rawtypes")
public class DataWrapper {
	private final int MAX_ROW_LENGTH = 20;
	
	private final ObservableList<Map> allData = FXCollections.observableArrayList();
	private final List<TableColumn<Map, String>> tableColumns = new ArrayList<>();
	private final SimpleDateFormat dFormat = new SimpleDateFormat();
	private final DecimalFormat decimalFormat = new DecimalFormat();
	private String formatDate;
	
	public DataWrapper(String formatDate, String formatValue) {
		this.formatDate = formatDate;
		
		dFormat.applyPattern(formatDate);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
		decimalFormatSymbols.setDecimalSeparator('.');
		decimalFormatSymbols.setGroupingSeparator(' ');
		decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);
		decimalFormat.applyPattern(formatValue);
		
		@SuppressWarnings("unchecked")
		Callback<TableColumn<Map, String>, TableCell<Map, String>> cellFactoryForMap = 
			p -> new TextFieldTableCell(new StringConverter() {
                @Override
                public String toString(Object t) {
                    return t.toString();
                }
                @Override
                public Object fromString(String string) {
                    return string;
                }                                    
            });
    
		tableColumns.forEach(c -> c.setCellFactory(cellFactoryForMap));
	}
	
	@SuppressWarnings("unchecked")
	public void wrapData(DataFX dataFX, TableView tvChart, TableColumn<Map, String> columnDate) {
		List<LinkedValue> data = dataFX.getData();
		List<Integer> idSignals = dataFX.getIdSignals();
		
		tableColumns.clear();
		allData.clear();
		
		columnDate.setCellValueFactory(new MapValueFactory("Date"));
    	tableColumns.add(columnDate);
    	
		idSignals.forEach(idSignal -> {
			String signalName = SingleFromDB.signals.get(idSignal).getNamesignal();
			if (signalName.length() > MAX_ROW_LENGTH) {
				String[] sss = signalName.split(" ");
				signalName = "";
				String newRow = sss[0] + " ";
				for (int i = 1; i < sss.length; i++) {
					if (newRow.length() + sss[i].length() < MAX_ROW_LENGTH) {
						newRow = newRow + sss[i] + " ";
					} else {
						signalName = signalName + newRow + "\n";
						newRow = sss[i];
					}
				}
				signalName = signalName + newRow;
			}
        	TableColumn<Map, String> column = new TableColumn<>(signalName);
        	column.setCellValueFactory(new MapValueFactory("" + idSignal));
        	column.setMinWidth(150);
        	tableColumns.add(column);
		});
		
		Map<Object, List<String>> dataMap = new HashMap<>();
		data.forEach(d -> {
			Object key = d.getDt();
			if (dataMap.get(key) == null) {
				List<String> values = new ArrayList<>();
				values.add(d.getId() + "_" + decimalFormat.format(d.getVal()));
				dataMap.put(key, values);
			} else {
				dataMap.get(key).add(d.getId() + "_" + decimalFormat.format(d.getVal()));
			}
		});
		
		dataMap.keySet().forEach(keyDate -> {
			Map<String, String> dataRow = new HashMap<>();
			dataRow.put("Date", dFormat.format(keyDate));
			
			dataMap.get(keyDate).forEach(e -> {
				String[] ss = e.split("_");
				dataRow.put(ss[0], ss[1]);
			});
			
			allData.add(dataRow);
		});
		
		tvChart.getColumns().setAll(tableColumns);
		tvChart.setItems(allData);
		allData.sort(new DefaultSorting());
		tvChart.scrollTo(tvChart.getItems().size());
	}
	
	private class DefaultSorting implements Comparator<Object> {
		@Override
		public int compare(Object obj1, Object obj2) {			
			try {
				int startIndex = obj1.toString().indexOf("Date=") + 5;
				Date d1 = dFormat.parse(obj1.toString().substring(startIndex, startIndex + formatDate.length()));
				startIndex = obj2.toString().indexOf("Date=") + 5;
				Date d2 = dFormat.parse(obj2.toString().substring(startIndex, startIndex + formatDate.length()));
				
				return d1.compareTo(d2);
			} catch (ParseException e) {
				LogFiles.log.log(Level.SEVERE, "int compare(...)", e);
			}
			return 0;
		}		
	}
}
