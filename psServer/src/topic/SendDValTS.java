package topic;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.jms.TopicSession;

import jdbc.PostgresDB;
import model.DvalTS;

public class SendDValTS extends SendTopic implements Runnable {
	
	private boolean isRun = true;	
	private List<DvalTS> ls = null;
	
	public SendDValTS(TopicSession pubSession, String nameTopic) {		
		super();		
		setTopic(pubSession, nameTopic);
	}
	
	@Override
	public void run() {
		Timestamp dt = new Timestamp(new Date().getTime());
		PostgresDB pdb = getPdb();
		
		while (isRun) {
			try {
				ls = pdb.getLastTS(dt);
				if (ls != null) {
					for (int i = 0; i < ls.size(); i++) {
						DvalTS ts = ls.get(i);
						if (i == 0) dt = ts.getServdt();

						msgO.setObject(ts);
						publisher.publish(msgO);
					}
				}
			} catch (Exception e) {
				System.err.println("SendDValTS");
				try {
					if (ls == null) Thread.sleep(60000); //Connection broken
				} catch (InterruptedException e1) {

				}
			}
		}
	}

	public boolean isRun() {
		return isRun;
	}

	public void setRun(boolean isRun) {
		this.isRun = isRun;
	}
	
}
