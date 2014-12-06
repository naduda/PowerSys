package pr.iec104;

import java.io.Serializable;
import java.math.BigInteger;

public class IdentBlock implements Serializable {
	private static final long serialVersionUID = 1L;
	private byte identType;
	private byte sq;
	private byte[] cause = new byte[2];
	private byte[] addressArray = new byte[2];
	
	public IdentBlock() {
		super();
	}
	
	public IdentBlock(byte[] bytes) {
		super();
		setIdentType(bytes[0]);
		setSq(bytes[1]);
		
		cause[0] = bytes[3];
		cause[1] = bytes[2];
		
		addressArray[0] = bytes[5];
		addressArray[1] = bytes[4];
	}
	
	@Override
	public String toString() {
		return "Type = " + identType + "; " +
				getSCtoString() + "; " + 
				"Cause = " + bytesToInt(cause) + "; " + 
				"Address = " + bytesToInt(addressArray) + "; ";
	}
	
	private int bytesToInt(byte[] bb) {
		return new BigInteger(bb).intValue();
	}
	
	private String getSCtoString() {
		if (Tools.getBit(sq, 7) == 0) return "keyValue: " + ((Byte)sq).intValue();
		return null;
	}
	
	public boolean isBlockKeyValue() {
		return Tools.getBit(sq, 7) == 0;
	}
	
	public int getValuesSize() {
		return ((Byte)sq).intValue();
	}
	
	public byte getIdentType() {
		return identType;
	}

	public void setIdentType(byte identType) {
		this.identType = identType;
	}

	public byte getSq() {
		return sq;
	}

	public void setSq(byte sq) {
		this.sq = sq;
	}
	
	public byte[] getCause() {
		return cause;
	}

	public void setCause(byte[] cause) {
		this.cause = cause;
	}

	public byte[] getAddressArray() {
		return addressArray;
	}

	public void setAddressArray(byte[] addressArray) {
		this.addressArray = addressArray;
	}
}