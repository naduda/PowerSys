package pr.common;

import java.util.Calendar;
import java.util.Date;

public class Utils {
	
	/*
	 *  Повертає повний шлях до файлу заданого за шаблоном:
	 *  path = "d:/1.txt"
	 *  path = "./1.txt"
	 *  path = "../../1.txt"
	 */
	public static String getFullPath(String path) {
		String res = "";
		if (path == null) {
			return res;
		}
		path = path.replace("\\", "/");
		
		if (path.length() != 0) {
			if (path.substring(0,1).equals(".")) {
				if (path.substring(0, 2).equals("./")) {
					res = System.getProperty("user.dir") + "/" + path.subSequence(2, path.length());
				} else if (path.substring(0, 3).equals("../")) {
					String rPath = path;
					String lPath = System.getProperty("user.dir").replace("\\", "/");
					while (rPath.substring(0, 3).equals("../")) {
						lPath = lPath.substring(0, lPath.lastIndexOf("/"));
						rPath = rPath.substring(rPath.indexOf("/") + 1);
					}
					res = lPath + "/" + rPath;
				}
			} else {
				res = path;
			}
		}
		
		return res.replace("\\", "/");
	}
	
	/**
	 * Повертає різницю між двома датами
	 * period: 0 - ms, 1 - s, 2 - mi, 3 - h, 4 - d
	 */
	public static long dateDiff(Date earlier, Date heigher, int period) {
		Calendar calendar1 = Calendar.getInstance();
	    Calendar calendar2 = Calendar.getInstance();
	    calendar1.setTime(earlier);
	    calendar2.setTime(heigher);
	    long milliseconds1 = calendar1.getTimeInMillis();
	    long milliseconds2 = calendar2.getTimeInMillis();
	    long diff = milliseconds2 - milliseconds1;
	    
	    switch (period) {
		case 0:
			return diff;
		case 1:
			return diff / 1000;
		case 2:
			return diff / (60 * 1000);
		case 3:
			return diff / (60 * 60 * 1000);
		case 4:
			return diff / (24 * 60 * 60 * 1000);
		default:
			return 0;
		}
	}
}
