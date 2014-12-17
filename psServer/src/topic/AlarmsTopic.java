package topic;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

import com.sun.messaging.ConnectionFactory;

import jdbc.BatisJDBC;
import jdbc.PostgresDB;
import jdbc.mappers.IMapper;
import pr.log.LogFiles;
import pr.model.Alarm;
import pr.topic.ASender;
import pr.topic.JMSConnection;
import single.SingleFromDB;

public class AlarmsTopic extends ASender {

	private List<Alarm> ls = new ArrayList<>();
	private List<Alarm> confirmed = new ArrayList<>();
	private Timestamp dtConfirmed;
	
	public AlarmsTopic(ConnectionFactory factory, JMSConnection jConn, String topicName, PostgresDB pdb) {
		super(factory, jConn, topicName);
		dtConfirmed = dt;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Timestamp senderMessage(Timestamp dt) {
		try {
			Timestamp newDT = dt;
			ls = (List<Alarm>) new BatisJDBC(s -> s.getMapper(IMapper.class).getAlarms(newDT)).get();
			Timestamp newDTconf = dtConfirmed;
			confirmed = (List<Alarm>) new BatisJDBC(s -> s.getMapper(IMapper.class).getAlarmsConfirm(newDTconf)).get(); 
			
			if (ls != null && confirmed != null) ls.removeAll(confirmed);
			if (ls != null && ls.size() > 0) {
				dt = ls.get(0).getRecorddt();
				ls.forEach(a -> {
					try {
						msgObject.setObject(a);
						producer.send(topic, msgObject);
						SingleFromDB.addAlarm(a);
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
						
						List<Alarm> filterData = SingleFromDB.getAlarms().stream()
								.filter(f -> f.getEventdt().equals(a.getEventdt()) && f.getRecorddt().equals(a.getRecorddt()))
								.collect(Collectors.toList());
						if (filterData.size() > 0) {
							Alarm oldAlarm = filterData.get(0);
							SingleFromDB.getAlarms().remove(oldAlarm);
							SingleFromDB.addAlarm(a);
						}
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
