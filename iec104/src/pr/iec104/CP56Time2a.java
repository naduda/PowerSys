package pr.iec104;

import java.io.DataInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CP56Time2a {
	private final byte[] value = new byte[7];
	private final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	private Date date;
	
	public CP56Time2a(Date dateTime) {
		this(dateTime.getTime());
	}

	public CP56Time2a(long timeInMillis) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timeInMillis);
		setDate(calendar.getTime());

		value[0] = (byte) calendar.get(Calendar.MILLISECOND);
		value[1] = (byte) calendar.get(Calendar.SECOND);
		value[2] = (byte) calendar.get(Calendar.MINUTE);
		value[3] = (byte) calendar.get(Calendar.HOUR_OF_DAY);
		value[4] = (byte) calendar.get(Calendar.DAY_OF_MONTH);
		value[5] = (byte) (calendar.get(Calendar.MONTH) + 1);
		value[6] = (byte) (calendar.get(Calendar.YEAR) % 100);
	}

	public CP56Time2a(DataInputStream inputStream) throws IOException {
		inputStream.readFully(value);
	}
	
	public CP56Time2a(byte[] bytes) {
		String dateString = (2000 + (0xFF & bytes[6])) + "-" +
				(0xFF & bytes[5]) + "-" + (0xFF & bytes[4]) + " " + 
				(0xFF & bytes[3]) + ":" + (0xFF & bytes[2]) + ":" + 
				(0xFF & bytes[1]) + "." + (0xFF & bytes[0]);
		try {
			setDate(df.parse(dateString));
		} catch (ParseException e) {
			e.printStackTrace();
		}
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

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
