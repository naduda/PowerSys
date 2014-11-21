package svg2fx.fxObjects;

import pr.log.LogFiles;
import single.SingleObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class ScriptClass {
	private Map<String, String> functions = new HashMap<>();
	
	public ScriptClass(String filePath) {
		if (filePath == null) return;
		try {
			if (SingleObject.readedScripts.keySet().contains(filePath)) {
				functions = SingleObject.readedScripts.get(filePath);
			} else {
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
						s = s.replace("[id]", "sh.getValue().getIdValue()");
						s = s.replace("[idTS]", "sh.getValue().getIdTSValue()");
						s = s.replace("[ON]", "sh.getStateIdTS('on')");
						s = s.replace("[OFF]", "sh.getStateIdTS('off')");
						func = func + "\n" + s;
					}
				}
				
				if (funcName.length() > 0) {
					functions.put(funcName, func);
				}
				SingleObject.readedScripts.put(filePath, functions);
			}
		} catch (IOException e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
		}		
	}

	public String getScriptByName(String name) {
		return functions.get(name);
	}
}
