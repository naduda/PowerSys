package topic;

import java.sql.Timestamp;
import java.util.List;
import java.util.logging.Level;

import com.sun.messaging.ConnectionFactory;

import jdbc.BatisJDBC;
import jdbc.PostgresDB;
import jdbc.mappers.IMapper;
import pr.model.DvalTS;
import pr.topic.ASender;
import pr.topic.JMSConnection;
import pr.log.LogFiles;

public class DValTSTopic extends ASender {

	private List<DvalTS> ls = null;
	
	public DValTSTopic(ConnectionFactory factory, JMSConnection jConn, String topicName, PostgresDB pdb) {
		super(factory, jConn, topicName);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Timestamp senderMessage(Timestamp dt) {
		try {
			Timestamp newDT = dt;
			ls = (List<DvalTS>) new BatisJDBC(s -> s.getMapper(IMapper.class).getLastTS(newDT)).get();
			
			if (ls != null) {
				for (int i = 0; i < ls.size(); i++) {
					DvalTS ts = ls.get(i);
					if (i == 0) dt = ts.getServdt();

					msgObject.setObject(ts);
					producer.send(topic, msgObject);
				}
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
