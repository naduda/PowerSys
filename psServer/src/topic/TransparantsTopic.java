package topic;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.sun.messaging.ConnectionFactory;

import jdbc.PostgresDB;
import pr.model.Ttransparant;
import pr.topic.ASender;
import pr.topic.JMSConnection;

public class TransparantsTopic extends ASender {

	private List<Ttransparant> ls = new ArrayList<>();
	private PostgresDB pdb;
	
	public TransparantsTopic(ConnectionFactory factory, JMSConnection jConn, String topicName, PostgresDB pdb) {
		super(factory, jConn, topicName);
		this.pdb = pdb;
	}

	@Override
	public Timestamp senderMessage(Timestamp dt) {
		try {
			ls = pdb.getTtransparantsNew(dt);
			
			if (ls != null && ls.size() > 0) {
				dt = ls.get(0).getSettime();
				ls.forEach(a -> {
					try {
						msgObject.setObject(a);
						producer.send(topic, msgObject);
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
			}
		} catch (Exception e) {
			System.err.println("TransparantsTopic");
			e.printStackTrace();
			try {
				if (ls == null) Thread.sleep(60000); //Connection broken
			} catch (InterruptedException e1) {
	
			}
		}
		return dt;
	}
	
}
