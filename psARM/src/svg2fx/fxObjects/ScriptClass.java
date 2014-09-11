package svg2fx.fxObjects;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

public class ScriptClass {
	private HashMap<String, String> functions = new HashMap<>();
	
	public ScriptClass(String filePath) {
		if (filePath == null) return;
		try {
			List<String> strs = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
			
			String func = "";
			String funcName = "";
			for (String s : strs) {
				if (s.toLowerCase().startsWith("function")) {
					if (funcName.length() > 0) {
						functions.put(funcName, func);
					}
					func = s;
					funcName = s.substring(s.indexOf(" ") + 1, s.indexOf("("));
				} else {
					func = func + "\n" + s.replace("[id]", "sh.getValue().getIdValue()").replace("[idTS]", "sh.getValue().getIdTSValue()");
				}
			}
			if (funcName.length() > 0) {
				functions.put(funcName, func);
			}
		} catch (IOException e) {
			System.err.println("public ScriptClass(String filePath)");
		}		
	}

	public String getScriptByName(String name) {
		return functions.get(name);
	}
}