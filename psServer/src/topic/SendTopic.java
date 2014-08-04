package topic;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.ObjectMessage;
import javax.jms.TopicPublisher;

import jdbc.PostgresDB;
import model.DvalTI;
import model.Tsignal;
import actualdata.LastData;

import com.sun.messaging.ConnectionConfiguration;
import com.sun.messaging.ConnectionFactory;

public class SendTopic implements Runnable {
	
	private String dbServer;
	private String dbPort;
	private String dbName;
	private String dbUser;
	private String dbPassword;
	
	private PostgresDB pdb;
	
	public ObjectMessage msgO;
	public TopicPublisher publisher;
	public JMSContext context;
	public JMSProducer producer;
	
	public ConnectionFactory factory;
	public JMSConnection jConn;

	public SendTopic(String dbServer, String dbName, String dbUser, String dbPassword) {
		try {
			jConn = new JMSConnection("127.0.0.1", "7676", "admin", "admin");
			setConnectionParams(dbServer, dbName, dbUser, dbPassword);
			pdb = getPdb();
			
			factory = new com.sun.messaging.ConnectionFactory();
			factory.setProperty(ConnectionConfiguration.imqAddressList, jConn.getConnConfiguration());
		} catch (JMSException e) {
			System.err.println("SendTopic()");
		}
		
	}
	
	@Override
	public void run() {
		Map<Integer, Tsignal> signals = pdb.getTsignalsMap();

		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
		Timestamp dt = new Timestamp(new Date().getTime());
		try {
			dt = new Timestamp(formatter.parse(formatter.format(new Date())).getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}

		pdb.getAlarms(dt).forEach(a -> { LastData.addAlarm(a); });
		LastData.setTsysparmams(pdb.getTSysParam());
		LastData.setTviewparams(pdb.getTViewParam());
		//ti.setVal(ti.getVal() * signals.get(ti.getSignalref()).getKoef());
		List<DvalTI> oldTIs = pdb.getOldTI().stream().filter(it -> it != null).collect(Collectors.toList());
		oldTIs.forEach(ti -> { ti.setVal(ti.getVal() * signals.get(ti.getSignalref()).getKoef()); });
		LastData.setOldTI(oldTIs);
		LastData.setOldTS(pdb.getOldTS());
		
		new Thread(new SendDValTI(factory, jConn, "DvalTI", signals, getPdb()), "SendDValTI_Thread").start();
		new Thread(new SendDValTS(factory, jConn, "DvalTS", getPdb()), "SendDValTS_Thread").start();
		new Thread(new SendAlarms(factory, jConn, "Alarms", false, getPdb()), "SendAlarms_Thread").start();
		new Thread(new SendAlarms(factory, jConn, "Alarms", true, getPdb()), "SendAlarmsConfirm_Thread").start();	
		
		System.out.println("Send ...");
	}
	
	public PostgresDB getPdb() {
		return new PostgresDB(dbServer, dbPort, dbName, dbUser, dbPassword);
	}
	
	public void setConnectionParams(String dbServer, String dbName, String dbUser, String dbPassword) {
		if (dbServer.equals("1")) {
			this.dbServer = "localhost";
			this.dbPort = "5432";
			this.dbName = "PowerSys_Donetsk_25";
			this.dbUser = "postgres";
			this.dbPassword = "12345678";
			return;
		}
		if (dbServer.indexOf(":") != -1) {
			this.dbServer = dbServer.split(":")[0];
			this.dbPort = dbServer.split(":")[1];
		} else {
			this.dbServer = dbServer;
			this.dbPort = "5432";
		}
		this.dbName = dbName;
		this.dbUser = dbUser;
		this.dbPassword = dbPassword;
	}

}
