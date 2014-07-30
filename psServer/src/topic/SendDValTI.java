package topic;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.jms.TopicSession;

import jdbc.PostgresDB;
import model.DvalTI;
import model.Tsignal;
import ua.pr.common.ToolsPrLib;

public class SendDValTI extends SendTopic implements Runnable {
	
	private boolean isRun = true;
	private List<DvalTI> ls = null;
	private Map<Integer, Tsignal> signals;
	
	public SendDValTI(TopicSession pubSession, String nameTopic, Map<Integer, Tsignal> signals) {
		super();
		
		this.signals = signals;
		setTopic(pubSession, nameTopic);
	}
	
	@Override
	public void run() {
		Timestamp dt = new Timestamp(new Date().getTime());
		PostgresDB pdb = getPdb();
		
		while (isRun) {
			try {
				ls = pdb.getLastTI(dt);

				if (ls != null) {
					for (int i = 0; i < ls.size(); i++) {
						DvalTI ti = ls.get(i);
						if (i == 0) dt = ti.getServdt();
						
						ti.setVal(ti.getVal() * signals.get(ti.getSignalref()).getKoef());
						long diff = Math.abs(ToolsPrLib.dateDiff(ti.getDt(), ti.getServdt(), 2)); //2 = minutes
						if (diff > 3) {
							ti.setActualData(false);
							System.err.println("No actual data - " + ti.getSignalref() + 
									"   [Diff = " + diff + " min;   servdt: " + 
									new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(ti.getServdt()) + "]");
						}
						msgO.setObject(ti);
						publisher.publish(msgO);
					}
				}
			} catch (Exception e) {
				System.err.println("SendDValTI");
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
