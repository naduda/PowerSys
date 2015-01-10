package topic;

import javax.jms.Topic;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;

import pr.log.LogFiles;
import topic.messagelisteners.AlarmMessageListener;
import topic.messagelisteners.ChatMessageListener;
import topic.messagelisteners.TIMessageListener;
import topic.messagelisteners.TSMessageListener;
import topic.messagelisteners.TransparantMessageListener;

import com.sun.messaging.ConnectionConfiguration;
import com.sun.messaging.ConnectionFactory;
import com.sun.messaging.jms.Session;
import com.sun.messaging.jms.TopicConnection;

import java.util.logging.Level;

public class ReceiveTopic extends javafx.concurrent.Task<Void> {

	private TopicSession session = null;
	private TopicConnection connection = null;
	private ConnectionFactory factory;
	private boolean isRun = true;
	private String ip = "";
	
	public void setRun(boolean r) {
		isRun = r;
	}
	
	public ReceiveTopic(String ip) {
		this.ip = ip;
	}
	
	@Override
	protected Void call() throws Exception {
		try {
			factory = new com.sun.messaging.ConnectionFactory();
			String prop = ip.length() == 0 ? "mq://127.0.0.1:7676,mq://127.0.0.1:7676" : "mq://" + ip + ",mq://" + ip;
			
			factory.setProperty(ConnectionConfiguration.imqAddressList, prop);
			connection = (TopicConnection) factory.createTopicConnection("admin","admin");
			session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
			connection.start();
			
			Topic tDvalTI = session.createTopic("DvalTI");
			TopicSubscriber subscriberDvalTI = session.createSubscriber(tDvalTI);						
			subscriberDvalTI.setMessageListener(new TIMessageListener());
			
			Topic tDvalTS = session.createTopic("DvalTS");
			TopicSubscriber subscriberDvalTS = session.createSubscriber(tDvalTS);						
			subscriberDvalTS.setMessageListener(new TSMessageListener());
			
			Topic tAlarms = session.createTopic("Alarms");
			TopicSubscriber subscribertAlarms = session.createSubscriber(tAlarms);						
			subscribertAlarms.setMessageListener(new AlarmMessageListener());
			
			Topic tTransparants = session.createTopic("Transparants");
			TopicSubscriber subscribertTransparants = session.createSubscriber(tTransparants);						
			subscribertTransparants.setMessageListener(new TransparantMessageListener());
			
			Topic tChat = session.createTopic("ChatTopic");
			TopicSubscriber subscribertChat = session.createSubscriber(tChat);						
			subscribertChat.setMessageListener(new ChatMessageListener());
			
			int k = 0;
			while (isRun) {
				Thread.sleep(600000);
				System.out.println((++k) * 10 + " min");
			}
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
		} finally {
			try {
				session.close();
		        connection.close();
		    } catch (Exception e) {
				LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
		    }
		}
		return null;
	}
}
