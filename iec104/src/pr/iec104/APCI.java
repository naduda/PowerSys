package pr.iec104;

import java.io.Serializable;

public class APCI implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final int I_FORMAT = 1;
	private static final int S_FORMAT = 2;
	private static final int U_FORMAT = 3;
	private byte start;
	private byte length;
	private byte field1;
	private byte field2;
	private byte field3;
	private byte field4;
	
	public APCI() {
		super();
	}
	
	public APCI(byte[] bytes) {
		this();
		setStart(bytes[0]);
		setLength(bytes[1]);
		setField1(bytes[2]);
		setField2(bytes[3]);
		setField3(bytes[4]);
		setField4(bytes[5]);
	}
	
	public int getType() {
		if (Tools.getBit(field1, 0) == 0) return I_FORMAT;
		if (Tools.getBit(field1, 0) == 1 && Tools.getBit(field1, 1) == 0) return S_FORMAT;
		if (Tools.getBit(field1, 0) == 1 && Tools.getBit(field1, 1) == 1) return U_FORMAT;
		return 0;
	}

	public byte getStart() {
		return start;
	}

	public void setStart(byte start) {
		this.start = start;
	}

	public byte getLength() {
		return length;
	}

	public void setLength(byte length) {
		this.length = length;
	}

	public byte getField1() {
		return field1;
	}

	public void setField1(byte field1) {
		this.field1 = field1;
	}

	public byte getField2() {
		return field2;
	}

	public void setField2(byte field2) {
		this.field2 = field2;
	}

	public byte getField3() {
		return field3;
	}

	public void setField3(byte field3) {
		this.field3 = field3;
	}

	public byte getField4() {
		return field4;
	}

	public void setField4(byte field4) {
		this.field4 = field4;
	}
}
