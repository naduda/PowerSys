package topic;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;

import com.sun.messaging.ConnectionConfiguration;
import com.sun.messaging.ConnectionFactory;
import com.sun.messaging.jms.Session;
import com.sun.messaging.jms.TopicConnection;

public class SendProtocolTopic {
	private boolean isRun = true;
	private boolean isSended = true;
	private String commandSend;
	private Serializable valSend;
	
	private TextMessage msgT;
	private ObjectMessage msgO;
	private TopicPublisher publisher;
	private TopicConnection connection = null;
	private TopicSession pubSession = null;
	
	public SendProtocolTopic() {
		ConnectionFactory factory;

		try {			
			factory = new com.sun.messaging.ConnectionFactory();
			factory.setProperty(ConnectionConfiguration.imqAddressList, "mq://127.0.0.1:7676,mq://127.0.0.1:7676");
			connection = (TopicConnection) factory.createTopicConnection("admin","admin");
			pubSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
			
			connection.start();	
			
			Topic topic = pubSession.createTopic("ProtocolTopic");
			publisher = pubSession.createPublisher(topic);
			msgT = pubSession.createTextMessage();
			msgO = pubSession.createObjectMessage();
			
			System.out.println(this);
		} catch (Exception e) {
			System.err.println("SendTopic");
			e.printStackTrace();
		}
	}
	
	public void closeSessionConnection() {
		try {
			pubSession.close();
	        connection.close();
	    } catch (Exception e) {
	    	System.out.println("Can't close JMS connection/session " + e.getMessage());
	    }
	}
	
	public void runCommand() {
		System.out.println("runCommand() started");
		while (isRun) {
			if (!isSended && commandSend.length() > 0) {
				try {
					msgT.setText(commandSend);
					publisher.publish(msgT);
					System.out.println("publish " + msgT.getText());
					setSended(true);
				} catch (JMSException e) {
					System.err.println("SendProtocolTopic runCommand()");
					e.printStackTrace();
					System.exit(0);
				}
			}
		}
		closeSessionConnection();
	}
	
	public void run() {
		while (isRun) {
			if (!isSended) {
				try {
					msgO.setObject(valSend);
					publisher.publish(msgO);
					isSended = true;
				} catch (JMSException e) {
					System.err.println("SendProtocolTopic run()");
				}
			}
		}
		closeSessionConnection();
	}

	public String getCommandSend() {
		return commandSend;
	}

	public void setCommandSend(String commandSend) {
		this.commandSend = commandSend;
		setSended(false);
	}

	public Serializable getValSend() {
		return valSend;
	}

	public void setValSend(Serializable valSend) {
		this.valSend = valSend;
	}

	public boolean isRun() {
		return isRun;
	}

	public void setRun(boolean isRun) {
		this.isRun = isRun;
	}

	public boolean isSended() {
		return isSended;
	}

	public void setSended(boolean isSended) {
		this.isSended = isSended;
		if (isSended) {
			setCommandSend("");
			setValSend(null);
		}
	}
}
