package topic;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jms.TopicSession;

import jdbc.PostgresDB;
import model.Alarm;

public class SendAlarms extends SendTopic implements Runnable {
	
	private boolean isRun = true;
	private boolean isConfirm;
	private List<Alarm> ls = null;
	private List<Alarm> previos = new ArrayList<Alarm>();
	
	public SendAlarms(TopicSession pubSession, String nameTopic, boolean isConfirm) {
		super();
		
		this.isConfirm = isConfirm;
		setTopic(pubSession, nameTopic);		
	}
	
	@Override
	public void run() {		
		Timestamp dt = new Timestamp(new Date().getTime());
		PostgresDB pdb = getPdb();
		
		while (isRun) {
			try {
				ls = isConfirm ? pdb.getAlarmsConfirm(dt) : pdb.getAlarms(dt);

				ls.removeAll(previos);
				if (ls != null && ls.size() > 0) {				
					for (int i = 0; i < ls.size(); i++) {
						Alarm a = ls.get(i);
						if (i == 0) dt = isConfirm ? a.getConfirmdt() : a.getEventdt();

						msgO.setObject(a);
						publisher.publish(msgO);
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
		}
	}

	public boolean isRun() {
		return isRun;
	}

	public void setRun(boolean isRun) {
		this.isRun = isRun;
	}
	
}
