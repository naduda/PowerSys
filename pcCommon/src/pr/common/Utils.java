package pr.common;

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
}
