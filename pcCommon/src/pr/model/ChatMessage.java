package pr.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class ChatMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String address;
	private String textValue;
	private Timestamp dt;
	
	public ChatMessage() {
		
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTextValue() {
		return textValue;
	}

	public void setTextValue(String textValue) {
		this.textValue = textValue;
	}

	public Timestamp getDt() {
		return dt;
	}

	public void setDt(Timestamp dt) {
		this.dt = dt;
	}
}
