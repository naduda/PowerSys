package svg2fx;

public class LinkedValue {
	private Integer key;
	private String value;
	
	public LinkedValue() {
		
	}
	
	public LinkedValue(Integer key, String value) {
		this.key = key;
		this.value = value;
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
}
