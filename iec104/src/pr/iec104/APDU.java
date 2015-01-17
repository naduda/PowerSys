package pr.iec104;

import java.io.Serializable;

public class APDU implements Serializable {
	private static final long serialVersionUID = 1L;
	private APCI apci;
	private ASDU asdu;
	
	public APDU() {
		super();
	}
	
	public APDU(byte[] bytes) {
		this();
		byte[] bytesAPCI = {bytes[0], bytes[1], bytes[2], bytes[3], bytes[4], bytes[5]};
		byte[] bytesASDU = new byte[bytes.length - 6];
		for (int i = 6; i < bytes.length; i++) {
			bytesASDU[i - 6] = bytes[i];
		}
		setApci(new APCI(bytesAPCI));
		if ((0xFF & getApci().getLength()) != (0xFF & bytes.length - 2)) {
			System.out.println("APDU not valid - " + (0xFF & bytes.length - 2) + " - " + 
					((Byte)getApci().getLength()).intValue() + " - " + Integer.toHexString(getApci().getLength()));
		}
		if (bytes.length > 6) setAsdu(new ASDU(bytesASDU));
	}

	public APCI getApci() {
		return apci;
	}

	public void setApci(APCI apci) {
		this.apci = apci;
	}

	public ASDU getAsdu() {
		return asdu;
	}

	public void setAsdu(ASDU asdu) {
		this.asdu = asdu;
	}
}
