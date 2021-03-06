package topic;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.sun.messaging.ConnectionFactory;

import jdbc.BatisJDBC;
import jdbc.PostgresDB;
import jdbc.mappers.IMapperT;
import pr.model.Ttransparant;
import pr.topic.ASender;
import pr.topic.JMSConnection;
import pr.log.LogFiles;

public class TransparantsTopic extends ASender {

	private List<Ttransparant> ls = new ArrayList<>();
	private List<Ttransparant> confirmed = new ArrayList<>();
	private Timestamp dtConfirmed;
	private List<Ttransparant> updated = new ArrayList<>();
	private Timestamp dtUpdated;
	
	public TransparantsTopic(ConnectionFactory factory, JMSConnection jConn, String topicName, PostgresDB pdb) {
		super(factory, jConn, topicName);
		dtConfirmed = dt;
		dtUpdated = dt;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Timestamp senderMessage(Timestamp dt) {
		try {
			Timestamp newDT = dt;
			ls = (List<Ttransparant>) new BatisJDBC(s -> s.getMapper(IMapperT.class).getTtransparantsNew(newDT)).get();
			
			confirmed = (List<Ttransparant>) new BatisJDBC(s -> s.getMapper(IMapperT.class).getTtransparantsClosed(dtConfirmed)).get();
			updated = (List<Ttransparant>) new BatisJDBC(s -> s.getMapper(IMapperT.class).getTtransparantsUpdated(dtUpdated)).get();
			
			if (ls != null && confirmed != null) ls.removeAll(confirmed);
			if (ls != null && ls.size() > 0) {
				dt = ls.get(0).getSettime();
				ls.forEach(a -> {
					try {
						msgObject.setObject(a);
						producer.send(topic, msgObject);
					} catch (Exception e) {
						LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
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
						LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
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
						LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
					}
				});
			}
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
			try {
				if (ls == null) {
					LogFiles.log.log(Level.WARNING, "Connection broken -> sleep 1 min");
					Thread.sleep(60000); //Connection broken
				}
			} catch (InterruptedException e1) {
				LogFiles.log.log(Level.SEVERE, e1.getMessage(), e1);
			}
		}
		return dt;
	}
	
}
