package pr.iec104;

public class Tools {
	public static int getBit(byte ID, int position) {
	   return (ID >> position) & 1;
	}
	
	public static void main(String[] args) {
		byte[] by = {(byte)0xDD, (byte)0x0C, (byte)0x0F, (byte)0x10, (byte)0x05, (byte)0x0C, (byte)0x0E};
		long value = 0;
		for (int i = 0; i < by.length; i++) {
		   value += ((long) by[i] & 0xffL) << (8 * i);
		}
		System.out.println(value);
		
		value = 0;
		for (int i = 0; i < by.length; i++) {
		   value = (value << 8) + (by[i] & 0xff);
		}
		System.out.println(value);
		
		byte[] by2 = {(byte)0x0E, (byte)0x0C, (byte)0x05, (byte)0x10, (byte)0x0f, (byte)0x0C, (byte)0xDD};
		value = 0;
		for (int i = 0; i < by2.length; i++) {
		   value += ((long) by2[i] & 0xffL) << (8 * i);
		}
		System.out.println(value);
		
		value = 0;
		for (int i = 0; i < by2.length; i++) {
		   value = (value << 8) + (by2[i] & 0xff);
		}
		System.out.println(value);
		System.out.println(System.currentTimeMillis());
	}
}
