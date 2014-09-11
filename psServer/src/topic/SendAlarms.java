package topic;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import actualdata.LastData;

import com.sun.messaging.ConnectionFactory;

import jdbc.PostgresDB;
import model.Alarm;

public class SendAlarms extends ASender {

	private List<Alarm> ls = new ArrayList<>();
	private List<Alarm> confirmed = new ArrayList<>();
	private Timestamp dtConfirmed;
	private PostgresDB pdb;
	
	public SendAlarms(ConnectionFactory factory, JMSConnection jConn, String topicName, PostgresDB pdb) {
		super(factory, jConn, topicName);
		this.pdb = pdb;
		dtConfirmed = dt;
	}

	@Override
	public Timestamp senderMessage(Timestamp dt) {
		try {
			ls = pdb.getAlarms(dt);
			confirmed = pdb.getAlarmsConfirm(dtConfirmed);
			
			ls.removeAll(confirmed);
			if (ls != null && ls.size() > 0) {				
				for (int i = 0; i < ls.size(); i++) {
					Alarm a = ls.get(i);
					if (i == 0) dt = a.getEventdt();

					msgObject.setObject(a);
					producer.send(topic, msgObject);
					LastData.addAlarm(a);
				}
			}
			
			if (confirmed != null && confirmed.size() > 0) {				
				for (int i = 0; i < confirmed.size(); i++) {
					Alarm a = confirmed.get(i);
					if (i == 0) dtConfirmed = a.getConfirmdt();

					msgObject.setObject(a);
					producer.send(topic, msgObject);
					
					Alarm oldAlarm = LastData.getAlarms().stream()
							.filter(f -> f.getEventdt().equals(a.getEventdt()) && f.getRecorddt().equals(a.getRecorddt()))
							.collect(Collectors.toList()).get(0);
					LastData.getAlarms().remove(oldAlarm);
					LastData.addAlarm(a);
				}
			}
		} catch (Exception e) {
			System.err.println("SendAlarms");
			e.printStackTrace();
			System.exit(0);
			try {
				if (ls == null) Thread.sleep(60000); //Connection broken
			} catch (InterruptedException e1) {
	
			}
		}
		return dt;
	}
	
}
