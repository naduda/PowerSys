package topic;

import java.sql.Timestamp;
import java.util.List;

import actualdata.LastData;

import com.sun.messaging.ConnectionFactory;

import jdbc.PostgresDB;
import pr.model.DvalTS;
import pr.topic.ASender;
import pr.topic.JMSConnection;

public class DValTSTopic extends ASender {

	private List<DvalTS> ls = null;
	private PostgresDB pdb;
	
	public DValTSTopic(ConnectionFactory factory, JMSConnection jConn, String topicName, PostgresDB pdb) {
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
					DvalTS oldTS = LastData.getOldTS().get(ts.getSignalref());
					if (oldTS != null) {
						oldTS.setVal(ts.getVal());
					} else {
						LastData.getOldTS().put(ts.getSignalref(), ts);
					}
				}
			}
		} catch (Exception e) {
			System.err.println("SendDValTS");
			e.printStackTrace();
			try {
				if (ls == null) Thread.sleep(60000); //Connection broken
			} catch (InterruptedException e1) {
	
			}
		}
		return dt;
	}

}
