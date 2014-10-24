package topic.messagelisteners;

import javafx.application.Platform;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import ui.Main;
public abstract class AbstarctMessageListener implements MessageListener {

	@Override
	public void onMessage(Message msg) {
		try {
			if (msg instanceof ObjectMessage) {				
				Object obj = ((ObjectMessage)msg).getObject();
				if (Main.mainScheme != null) {
					Platform.runLater(new Runnable() {
		                @Override public void run() {
		                	runLogic(obj);
		                }
		            });
		    	}
			}
		} catch (Exception e) {
		      System.out.println("Error while consuming a message: " + e.getMessage());
		      e.printStackTrace();
		}
	}

	abstract void runLogic(Object obj);
}