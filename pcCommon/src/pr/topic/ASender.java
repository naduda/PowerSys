package pr.topic;

import pr.inter.IJMSConnection;

import java.sql.Timestamp;
import java.util.Date;

import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.JMSRuntimeException;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;

import com.sun.messaging.ConnectionFactory;

public abstract class ASender implements Runnable {

	private boolean isRun = true;
	private ConnectionFactory factory;
	private IJMSConnection jConn;
	private String topicName;
	
	public ObjectMessage msgObject;
	public Topic topic;
	public JMSProducer producer;
	
	public Timestamp dt = new Timestamp(new Date().getTime());
	
	public ASender(ConnectionFactory factory, IJMSConnection jConn, String topicName) {
		this.factory = factory;
		this.jConn = jConn;
		this.topicName = topicName;
	}

	@Override
	public void run() {
		try (JMSContext context = factory.createContext(jConn.getUser(),jConn.getPassword(), Session.AUTO_ACKNOWLEDGE);) {
			producer = context.createProducer();
			topic = context.createTopic(topicName);
			msgObject = context.createObjectMessage();
			
			while (isRun) {
				try {
					dt = senderMessage(dt);
					Thread.sleep(100);
				} catch (Exception e) {
					System.err.println("ASender()");
				}
			}
	   } catch (JMSRuntimeException ex) {
	      System.out.println("ASender(ConnectionFactory factory, String topicName) ...");
	   }
	}
	
	abstract public Timestamp senderMessage(Timestamp dt);
	
	public boolean isRun() {
		return isRun;
	}

	public void setRun(boolean isRun) {
		this.isRun = isRun;
	}
	
}
