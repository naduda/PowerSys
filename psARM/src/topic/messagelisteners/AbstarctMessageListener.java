package topic.messagelisteners;

import javafx.application.Platform;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import pr.log.LogFiles;
import single.SingleObject;

import java.util.logging.Level;

public abstract class AbstarctMessageListener implements MessageListener {

	@Override
	public void onMessage(Message msg) {
		try {
			if (msg instanceof ObjectMessage) {				
				Object obj = ((ObjectMessage)msg).getObject();
				if (SingleObject.mainScheme != null) {
					Platform.runLater(() -> runLogic(obj));
		    	}
			}
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, "void onMessage(...)", e);
		}
	}

	abstract void runLogic(Object obj);
}
