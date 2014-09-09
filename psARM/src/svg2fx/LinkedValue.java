package svg2fx;

public class LinkedValue {
	private Integer key;
	private String value;
	private String typeSignal;
	
	public LinkedValue() {
		
	}
	
	public LinkedValue(Integer key, String value, String typeSignal) {
		this.key = key;
		this.value = value;
		this.typeSignal = typeSignal;
	}

	public Integer getKey() {
		return key;
	}

	public void setKey(Integer key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getTypeSignal() {
		return typeSignal;
	}

	public void setTypeSignal(String typeSignal) {
		this.typeSignal = typeSignal;
	}
}
