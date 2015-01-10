package topic;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.logging.Level;

import javax.jms.JMSException;

import com.sun.messaging.ConnectionFactory;

import pr.inter.IJMSConnection;
import pr.log.LogFiles;
import pr.model.ChatMessage;
import pr.topic.ASender;

public class ChatTopic extends ASender {
	
	public ChatTopic(ConnectionFactory factory, IJMSConnection jConn, String topicName) {
		super(factory, jConn, topicName);
	}

	@Override
	public Timestamp senderMessage(Timestamp dt) {
		return Timestamp.valueOf(LocalDateTime.now());
	}

	public void setChatMessage(ChatMessage message) {
		message.setDt(dt);
		try {
			msgObject.setObject(message);
			producer.send(topic, msgObject);
		} catch (JMSException e) {
			LogFiles.log.log(Level.SEVERE, "ChatTopic ...", e);
		}
	}
}
