package pr.topic;

import pr.inter.IJMSConnection;
import pr.log.LogFiles;

import java.sql.Timestamp;
import java.util.Date;
import java.util.logging.Level;

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
	
	private long sleepTimeout = 100;
	
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
					Thread.sleep(sleepTimeout);
				} catch (Exception e) {
					LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
				}
			}
	   } catch (JMSRuntimeException ex) {
	      LogFiles.log.log(Level.SEVERE, "ASender(ConnectionFactory factory, String topicName) ...", ex);
	   }
	}
	
	abstract public Timestamp senderMessage(Timestamp dt);
	
	public boolean isRun() {
		return isRun;
	}

	public void setRun(boolean isRun) {
		this.isRun = isRun;
	}

	public long getSleepTimeout() {
		return sleepTimeout;
	}

	public void setSleepTimeout(long sleepTimeout) {
		this.sleepTimeout = sleepTimeout;
	}
}
