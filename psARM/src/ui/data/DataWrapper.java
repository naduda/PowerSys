package ui.data;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;

import javafx.beans.property.SimpleStringProperty;
import model.LinkedValue;

public class DataWrapper {
	private final SimpleStringProperty pDate;
	private final SimpleStringProperty pValue;
	private String formatDate;
	private String formatValue;
	private LinkedValue data;
	
	public DataWrapper(LinkedValue data, String formatDate, String formatValue) {
		this.data = data;
		this.formatDate = formatDate;
		this.formatValue = formatValue;
		pDate = new SimpleStringProperty(data.getDt() != null ? data.getDt().toString() : "");
		pValue = new SimpleStringProperty(data.getVal() != null ? data.getVal().toString() : "");
	}
	
	public String getPDate() {
		SimpleDateFormat dFormat = new SimpleDateFormat(formatDate);
		return dFormat.format(data.getDt());
	}
	
	public void setPDate(String val) {
		pDate.set(val);
    }

	public String getPValue() {
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
		decimalFormatSymbols.setDecimalSeparator('.');
		decimalFormatSymbols.setGroupingSeparator(' ');
		
		DecimalFormat decimalFormat = new DecimalFormat(formatValue, decimalFormatSymbols);
		String textValue = decimalFormat.format(Double.parseDouble(pValue.get()));
		return textValue;
	}
	
	public void setPValue(String val) {
		pValue.set(val);
    }
}
