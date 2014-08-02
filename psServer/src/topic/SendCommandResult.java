package topic;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.jms.JMSException;

import inter.IJMSConnection;

import com.sun.messaging.ConnectionFactory;

public class SendCommandResult extends ASender {
	
	private Object result;

	public SendCommandResult(ConnectionFactory factory, IJMSConnection jConn, String topicName) {
		super(factory, jConn, topicName);
	}

	@Override
	public Timestamp senderMessage(Timestamp dt) {
		if (result != null) {
			try {
				msgObject.setObject((Serializable) result);
				producer.send(topic, msgObject);
				System.out.println("SEND Result");
				result = null;
			} catch (JMSException e) {
				System.err.println("SendCommandResult ...");
			}
		} else {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

}
