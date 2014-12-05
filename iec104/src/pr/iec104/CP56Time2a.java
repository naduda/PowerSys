package pr.iec104;

import java.io.DataInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CP56Time2a {
	private final byte[] value = new byte[7];
	
	public CP56Time2a(Date dateTime) {
		this(dateTime.getTime());
	}

	public CP56Time2a(long timeInMillis) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timeInMillis);

		int ms = calendar.get(Calendar.MILLISECOND) + 1000 * calendar.get(Calendar.SECOND);

		value[0] = (byte) ms;
		value[1] = (byte) (ms >> 8);
		value[2] = (byte) calendar.get(Calendar.MINUTE);
		value[3] = (byte) calendar.get(Calendar.HOUR_OF_DAY);
		value[4] = (byte) (calendar.get(Calendar.DAY_OF_MONTH) + ((((calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7) + 1) << 5));
		value[5] = (byte) (calendar.get(Calendar.MONTH) + 1);
		value[6] = (byte) (calendar.get(Calendar.YEAR) % 100);
	}

	public CP56Time2a(DataInputStream inputStream) throws IOException {
		inputStream.readFully(value);
	}
	
	public CP56Time2a(byte[] bytes) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		String dateString = (2000 + ((Byte)bytes[0]).intValue()) + "-" +
				((Byte)bytes[1]).intValue() + "-" + ((Byte)bytes[2]).intValue() + " " + 
				((Byte)bytes[3]).intValue() + ":" + ((Byte)bytes[4]).intValue() + ":" + 
				((Byte)bytes[5]).intValue() + "." + ((Byte)bytes[6]).intValue();
		System.out.println(dateString);
	}
	
	public long getTimestamp(int earliestYear) {

		int century = earliestYear / 100 * 100;
		if (value[6] < (earliestYear % 100)) {
			century += 100;
		}

		Calendar calendar = Calendar.getInstance();

		calendar.set(value[6] + century, value[5] - 1, value[4] & 0x1f, value[3], value[2],
				(((value[0] & 0xff) + ((value[1] & 0xff) << 8))) / 1000);
		calendar.set(Calendar.MILLISECOND, (((value[0] & 0xff) + ((value[1] & 0xff) << 8))) % 1000);

		return calendar.getTimeInMillis();
	}

	@Override
	public String toString() {
		return "Time56: " + new Date(getTimestamp(0));
	}
}
