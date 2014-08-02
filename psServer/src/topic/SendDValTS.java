package topic;

import java.sql.Timestamp;
import java.util.List;

import com.sun.messaging.ConnectionFactory;

import jdbc.PostgresDB;
import model.DvalTS;

public class SendDValTS extends ASender {

	private List<DvalTS> ls = null;
	private PostgresDB pdb;
	
	public SendDValTS(ConnectionFactory factory, JMSConnection jConn, String topicName, PostgresDB pdb) {
		super(factory, jConn, topicName);
		this.pdb = pdb;
	}

	@Override
	public Timestamp senderMessage(Timestamp dt) {
		try {
			ls = pdb.getLastTS(dt);
			if (ls != null) {
				for (int i = 0; i < ls.size(); i++) {
					DvalTS ts = ls.get(i);
					if (i == 0) dt = ts.getServdt();

					msgObject.setObject(ts);
					producer.send(topic, msgObject);
				}
			}
		} catch (Exception e) {
			System.err.println("SendDValTS");
			try {
				if (ls == null) Thread.sleep(60000); //Connection broken
			} catch (InterruptedException e1) {
	
			}
		}
		return dt;
	}

}
