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
	private List<Ttransparant> confirmed = new ArrayList<>();
	private Timestamp dtConfirmed;
	private List<Ttransparant> updated = new ArrayList<>();
	private Timestamp dtUpdated;
	private PostgresDB pdb;
	
	public TransparantsTopic(ConnectionFactory factory, JMSConnection jConn, String topicName, PostgresDB pdb) {
		super(factory, jConn, topicName);
		this.pdb = pdb;
		dtConfirmed = dt;
		dtUpdated = dt;
	}

	@Override
	public Timestamp senderMessage(Timestamp dt) {
		try {
			ls = pdb.getTtransparantsNew(dt);
			confirmed = pdb.getTtransparantsClosed(dtConfirmed);
			updated = pdb.getTtransparantsUpdated(dtUpdated);
			
			ls.removeAll(confirmed);
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
			
			if (updated != null && updated.size() > 0) {
				dtUpdated = updated.get(0).getLastupdate();
				updated.forEach(a -> {
					try {
						msgObject.setObject(a);
						producer.send(topic, msgObject);
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
			}
			
			if (confirmed != null && confirmed.size() > 0) {
				dtConfirmed = confirmed.get(0).getClosetime();
				confirmed.forEach(a -> {
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
