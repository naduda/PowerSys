package pr.iec104;

import java.io.Serializable;

public class InfoObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private byte[] addressObjectArray = new byte[3];
	private byte[] infoElementArray;
	private byte[] timeArray = new byte[7];;
	
	public InfoObject() {
		super();
	}
	
	public InfoObject(byte[] bytes) {
		this();
		
		addressObjectArray[0] = bytes[2];
		addressObjectArray[1] = bytes[1];
		addressObjectArray[2] = bytes[0];
		
		int infoElementSize = bytes.length - 10;
		infoElementArray = new byte[infoElementSize];
		int k = 0;
		for (int i = infoElementSize - 1; i >= 0; i--) {
			infoElementArray[k++] = bytes[i + 3];
		}
		
		k = 0;
		for (int i = 6; i >= 0; i--) {
			timeArray[k++] = bytes[bytes.length - 7 + i];
		}
	}

	public byte[] getAddressObjectArray() {
		return addressObjectArray;
	}

	public void setAddressObjectArray(byte[] addressObjectArray) {
		this.addressObjectArray = addressObjectArray;
	}

	public byte[] getInfoElementArray() {
		return infoElementArray;
	}

	public void setInfoElementArray(byte[] infoElementArray) {
		this.infoElementArray = infoElementArray;
	}

	public byte[] getTimeArray() {
		return timeArray;
	}

	public void setTimeArray(byte[] timeArray) {
		this.timeArray = timeArray;
	}
}
