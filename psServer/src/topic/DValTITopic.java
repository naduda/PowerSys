package topic;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import com.sun.messaging.ConnectionFactory;

import jdbc.BatisJDBC;
import jdbc.PostgresDB;
import jdbc.mappers.IMapper;
import pr.model.DvalTI;
import pr.model.Tsignal;
import pr.topic.ASender;
import pr.topic.JMSConnection;
import pr.log.LogFiles;

public class DValTITopic extends ASender {

	private List<DvalTI> ls = null;
	private Map<Integer, Tsignal> signals;

	public DValTITopic(ConnectionFactory factory, JMSConnection jConn, String topicName, Map<Integer, Tsignal> signals, PostgresDB pdb) {
		super(factory, jConn, topicName);
		this.signals = signals;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Timestamp senderMessage(Timestamp dt) {		
		try {
			Timestamp newDT = dt;
			ls = (List<DvalTI>) new BatisJDBC(s -> s.getMapper(IMapper.class).getLastTI(newDT)).get();
	
			if (ls != null) {
				for (int i = 0; i < ls.size(); i++) {
					DvalTI ti = ls.get(i);
					if (i == 0) dt = ti.getServdt();
					
					ti.setVal(ti.getVal() * signals.get(ti.getSignalref()).getKoef());
					long diff = Duration.between(ti.getDt().toInstant(), ti.getServdt().toInstant()).toMinutes();
					if (diff > 3) {
						ti.setActualData(false);
						System.err.println("No actual data - " + ti.getSignalref() + 
								"   [Diff = " + diff + " min;   servdt: " + 
								new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(ti.getServdt()) + "]");
					}
					msgObject.setObject(ti);
					producer.send(topic, msgObject);
				}
			}
		} catch (Exception e) {
			try {
				if (ls == null) {
					LogFiles.log.log(Level.WARNING, "Connection broken -> sleep 1 min");
					Thread.sleep(60000); //Connection broken
				} else {
					LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
				}
			} catch (InterruptedException e1) {
				LogFiles.log.log(Level.SEVERE, e1.getMessage(), e1);
			}
		}
		return dt;
	}
}
