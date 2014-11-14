package single;

import java.util.ResourceBundle;

public class Constants {
	private final static int MANUAL_MODE = 107;
	private final static int AUTO_MODE = 0;
	
	public static String getQuality(int rcode) {
		ResourceBundle rb = SingleObject.getResourceBundle();
		String ret = "";
		switch (rcode) {
		case MANUAL_MODE:
			ret = rb.getString("keyManual");
			break;
		case AUTO_MODE:
			ret = rb.getString("keyAuto");
			break;
		default:
			ret = "";
			break;
		}
		return ret;
	}
}
