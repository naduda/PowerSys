package pr.log;

import java.io.File;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import pr.common.MyFormatter;
import pr.common.Utils;

public class LogFiles {
	public static Logger log = Logger.getLogger(LogFiles.class.getName());
	
	public LogFiles() {
		seLogFile(log, "log");
		log.log(Level.INFO, "START ...");
	}
	
	private void seLogFile(Logger log, String fileName) {
		try {
            log.setUseParentHandlers(false);
           
            MyFormatter formatter = new MyFormatter();
            ConsoleHandler handler = new ConsoleHandler();
            handler.setFormatter(formatter);
            log.addHandler(handler);
            
            String location = Utils.getFullPath("./logs/");
            File f = new File(location);
            if (!f.exists()) f.mkdir();
            FileHandler fHandler = new FileHandler(location + fileName, 10000000, 1);
            fHandler.setFormatter(formatter);
            log.addHandler(fHandler);
        } catch (IOException e) {
            System.err.println("Could not setup logger configuration: " + e);
        }
	}
}
