package topic;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import actualdata.LastData;

import com.sun.messaging.ConnectionFactory;

import jdbc.PostgresDB;
import model.DvalTI;
import model.Tsignal;
import ua.pr.common.ToolsPrLib;

public class SendDValTI extends ASender {

	private List<DvalTI> ls = null;
	private Map<Integer, Tsignal> signals;
	private PostgresDB pdb;

	public SendDValTI(ConnectionFactory factory, JMSConnection jConn, String topicName, Map<Integer, Tsignal> signals, PostgresDB pdb) {
		super(factory, jConn, topicName);
		this.signals = signals;
		this.pdb = pdb;
	}
	
	@Override
	public Timestamp senderMessage(Timestamp dt) {		
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
					msgObject.setObject(ti);
					producer.send(topic, msgObject);
					LastData.getOldTI().get(ti.getSignalref()).setVal(ti.getVal());
				}
			}
		} catch (Exception e) {
			try {
				if (ls == null) {
					Thread.sleep(60000); //Connection broken
					System.out.println(new Date());
				} else {
					e.printStackTrace();
					System.exit(0);
				}
			} catch (InterruptedException e1) {
	
			}
		}
		return dt;
	}
}
