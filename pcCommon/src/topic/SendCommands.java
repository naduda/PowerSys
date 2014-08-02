package topic;

import java.sql.Timestamp;

import inter.IJMSConnection;

import com.sun.messaging.ConnectionFactory;

public class SendCommands extends ASender {
	
	private String command;

	public SendCommands(ConnectionFactory factory, IJMSConnection jConn, String topicName) {
		super(factory, jConn, topicName);
	}

	@Override
	public Timestamp senderMessage(Timestamp dt) {
		if (command != null) {
			producer.send(topic, command);
			setCommand(null);
		}
		return null;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

}
