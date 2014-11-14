package topic;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

import actualdata.LastData;

import com.sun.messaging.ConnectionFactory;

import jdbc.PostgresDB;
import pr.log.LogFiles;
import pr.model.Alarm;
import pr.topic.ASender;
import pr.topic.JMSConnection;

public class AlarmsTopic extends ASender {

	private List<Alarm> ls = new ArrayList<>();
	private List<Alarm> confirmed = new ArrayList<>();
	private Timestamp dtConfirmed;
	private PostgresDB pdb;
	
	public AlarmsTopic(ConnectionFactory factory, JMSConnection jConn, String topicName, PostgresDB pdb) {
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
				dt = ls.get(0).getRecorddt();
				ls.forEach(a -> {
					try {
						msgObject.setObject(a);
						producer.send(topic, msgObject);
						LastData.addAlarm(a);
					} catch (Exception e) {
						LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
					}
				});
			}
			
			if (confirmed != null && confirmed.size() > 0) {
				dtConfirmed = confirmed.get(0).getConfirmdt();
				confirmed.forEach(a -> {
					try {
						msgObject.setObject(a);
						producer.send(topic, msgObject);
						
						Alarm oldAlarm = LastData.getAlarms().stream()
								.filter(f -> f.getEventdt().equals(a.getEventdt()) && f.getRecorddt().equals(a.getRecorddt()))
								.collect(Collectors.toList()).get(0);
						LastData.getAlarms().remove(oldAlarm);
						LastData.addAlarm(a);
					} catch (Exception e) {
						LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
					}
				});
			}
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
			try {
				if (ls == null) Thread.sleep(60000); //Connection broken
			} catch (InterruptedException e1) {
				LogFiles.log.log(Level.SEVERE, e1.getMessage(), e1);
			}
		}
		return dt;
	}
	
}
