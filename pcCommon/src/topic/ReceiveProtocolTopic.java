package topic;

import java.io.Serializable;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;

import com.sun.messaging.ConnectionConfiguration;
import com.sun.messaging.ConnectionFactory;
import com.sun.messaging.jms.Session;
import com.sun.messaging.jms.TopicConnection;

public class ReceiveProtocolTopic implements MessageListener {

	private boolean isRun = true;
	private Serializable valObject;
	private String command;
	
	private TopicSession session = null;
	private TopicConnection connection = null;
	private ConnectionFactory factory;
	
	public ReceiveProtocolTopic() {
		try {
			factory = new com.sun.messaging.ConnectionFactory();
			String prop = "mq://127.0.0.1:7676,mq://127.0.0.1:7676";
			factory.setProperty(ConnectionConfiguration.imqAddressList, prop);
			connection = (TopicConnection) factory.createTopicConnection("admin","admin");
			session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
			connection.start();
			
			Topic protocolTopic = session.createTopic("ProtocolTopic");
			TopicSubscriber subscriber = session.createSubscriber(protocolTopic);						
			subscriber.setMessageListener(this);

			System.out.println(this);
		} catch (Exception e) {
			System.err.println("ReceiveTopic ");
			e.printStackTrace();
		} finally {
			try {
				session.close();
		        connection.close();
		    } catch (Exception e) {
		    	System.out.println("Can't close JMS connection/session " + e.getMessage());
		    }
		}
	}
	
	public void run() {
		while (isRun) {
			
		}
	}
	@Override
	public void onMessage(Message msg) {
		try {
			if (msg instanceof ObjectMessage) {
				setValObject(((ObjectMessage)msg).getObject());
			} else if (msg instanceof TextMessage) {
				setCommand(((TextMessage)msg).getText());
			}
		 } catch (Exception e){
		      System.out.println("Error while consuming a message: " + e.getMessage());
		      e.printStackTrace();
		 }
	}

	public Serializable getValObject() {
		return valObject;
	}

	public void setValObject(Serializable valObject) {
		this.valObject = valObject;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public boolean isRun() {
		return isRun;
	}

	public void setRun(boolean isRun) {
		this.isRun = isRun;
	}
}
