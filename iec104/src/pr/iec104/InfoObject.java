package pr.iec104;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class InfoObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private byte[] addressObjectArray = new byte[4];
	private byte[] infoElementArray;
	private byte[] timeArray = new byte[7];
	private EType type;
	
	public InfoObject() {
		super();
	}
	
	public InfoObject(byte[] bytes, EType type) {
		this();
		this.type = type;
		
		addressObjectArray[0] = bytes[0];
		addressObjectArray[1] = bytes[1];
		addressObjectArray[2] = bytes[2];
		
		int infoElementSize = bytes.length - 10;
		infoElementArray = new byte[infoElementSize];
		
		for (int i = 0; i < infoElementSize; i++) {
			infoElementArray[i] = bytes[i + 3];
		}
		
		for (int i = 0; i < 7; i++) {
			timeArray[i] = bytes[bytes.length - 7 + i];
		}
	}
	
	public int getAddress() {
		return ByteBuffer.wrap(addressObjectArray).order(ByteOrder.LITTLE_ENDIAN).getInt();
	}
	
	public float getValue() {
		switch (type) {
		case M_ME_NC:
		case л_ле_ря:
		case M_ME_TF:
			return ByteBuffer.wrap(infoElementArray).order(ByteOrder.LITTLE_ENDIAN).getFloat();

		default:
			System.out.println(type + " not supported");
			break;
		}
		return 0;
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
