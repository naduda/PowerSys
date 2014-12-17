package pr.common;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class MyFormatter extends Formatter { 
	private static final DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	private static final int LEVEL_NAME_LENGTH = 10;
	private static final int INFO_LENGTH = 70;
	
	public static boolean isDetailsLog = true;
	
    public String format(LogRecord record) {
    	if (record.getParameters() == null) {
			String[] sss = {"null"};
			record.setParameters(sss);
		}
        StringBuilder builder = new StringBuilder(1000);
        builder.append(df.format(new Date(record.getMillis()))).append(" ");
        builder.append(fixedLenthString(record.getLevel().getName(), LEVEL_NAME_LENGTH));
        String info = formatMessage(record);
        if (record.getLevel().equals(Level.INFO) || record.getLevel().equals(Level.WARNING)) {
        	while (info.length() > INFO_LENGTH) {
        		builder.append(fixedLenthString(info, INFO_LENGTH)).append("\n");
        		builder.append(fixedLenthString("", 35));
        		info = info.substring(INFO_LENGTH);
			}
        	builder.append(fixedLenthString(info, INFO_LENGTH)).append("\n");
		} else if (record.getLevel().equals(Level.SEVERE)) {
			int tabLength = builder.length();
			builder.append("[" + record.getSourceClassName() + "]").append("\n");
			builder.append(fixedLenthString("", tabLength));
			builder.append(fixedLenthString(info, INFO_LENGTH)).append("\n");
			
			Throwable e = record.getThrown();
			if (isDetailsLog && e != null) {
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				
				builder.append(fixedLenthString("", tabLength)).append(errors.toString()).append("\n");
				builder.append("******************************************").append("\n");
				e.printStackTrace();
			}
			for(Object o : record.getParameters()) {
				builder.append(fixedLenthString("", tabLength)).append(o.toString()).append("\n");
			}
		}        
        
        return builder.toString();
    }
    
    /**
	 * Повертає рядок заданої довжини
	 * В кінці доставляє пробєли, або обрізає
	 */
    private String fixedLenthString(String string, int length) {
    	if (string == null) string = "null";
		if (length <= string.length()) {
			return string.substring(0, length);
		} else {
			return String.format("%s%" + (length - string.length()) + "s", string, "");
		}
	}
}
