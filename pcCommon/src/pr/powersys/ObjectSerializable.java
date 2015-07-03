package pr.powersys;

import java.io.Serializable;

public class ObjectSerializable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Object baosSerializable;
	
	public ObjectSerializable() {
		
	}

	public Object getBaosSerializable() {
		return baosSerializable;
	}

	public void setBaosSerializable(Object baosSerializable) {
		this.baosSerializable = baosSerializable;
	}
}
