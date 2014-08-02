package topic;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.sun.messaging.ConnectionFactory;

import jdbc.PostgresDB;
import model.Alarm;

public class SendAlarms extends ASender {

	private List<Alarm> ls = null;
	private List<Alarm> previos = new ArrayList<Alarm>();
	private boolean isConfirm;
	private PostgresDB pdb;
	
	public SendAlarms(ConnectionFactory factory, JMSConnection jConn, String topicName, boolean isConfirm, PostgresDB pdb) {
		super(factory, jConn, topicName);
		this.isConfirm = isConfirm;
		this.pdb = pdb;
	}

	@Override
	public Timestamp senderMessage(Timestamp dt) {
		try {
			ls = isConfirm ? pdb.getAlarmsConfirm(dt) : pdb.getAlarms(dt);
	
			ls.removeAll(previos);
			if (ls != null && ls.size() > 0) {				
				for (int i = 0; i < ls.size(); i++) {
					Alarm a = ls.get(i);
					if (i == 0) dt = isConfirm ? a.getConfirmdt() : a.getEventdt();

					msgObject.setObject(a);
					producer.send(topic, msgObject);
				}
				previos = ls;
			}
		} catch (Exception e) {
			System.err.println("SendAlarms");
			try {
				if (ls == null) Thread.sleep(60000); //Connection broken
			} catch (InterruptedException e1) {
	
			}
		}
			return null;
		}
	
}
