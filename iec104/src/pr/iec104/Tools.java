package pr.iec104;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Tools {
	public static int getBit(byte ID, int position) {
	   return (ID >> position) & 1;
	}
	
	public static void main(String[] args) {
		byte[] by2 = {(byte)0x0E, (byte)0x0C, (byte)0x05, (byte)0x10, 
				(byte)0x0f, (byte)0x0C, (byte)0xDD};
		CP56Time2a cp = new CP56Time2a(by2);
		System.out.println("\nDATE = " + cp.getDate());
		System.out.println("===========================");
		CP56Time2a cp2 = new CP56Time2a(cp.getDate().getTime());
		System.out.println("\nDATE = " + cp2.getDate());
		
		byte[] by = {0x03, 0x00, 0x00, 0x00};
//		byte[] by = new byte[2];
//		by[0] = 0xFF & 0x03;
//		by[1] = 0xFF & 0x00;

		try {
			System.out.println(ByteBuffer.wrap(by).order(ByteOrder.LITTLE_ENDIAN).getInt());
			//ibb.rewind();
			System.out.println(ByteBuffer.wrap(by).order(ByteOrder.BIG_ENDIAN).getInt());
		} catch (Exception e) {
			System.out.println("err");
			e.printStackTrace();
		}
	}
}
