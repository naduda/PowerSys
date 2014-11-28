package topic;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;

import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.ObjectMessage;
import javax.jms.TopicPublisher;

import jdbc.PostgresDB;
import pr.model.Alarm;
import pr.model.TSysParam;
import pr.model.TViewParam;
import pr.model.Transparant;
import pr.model.Tsignal;
import pr.topic.JMSConnection;
import pr.log.LogFiles;
import single.SQLConnect;
import single.SingleFromDB;

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
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
		}
		
	}
	
	boolean isDataFromDB = false;
	@Override
	public void run() {
		double start = System.currentTimeMillis();
		
		Map<Integer, Tsignal> signals2 = null;
		while (!isDataFromDB) {
			signals2 = pdb.getTsignalsMap();
			isDataFromDB = signals2 == null ? false : true;
		}
		isDataFromDB = false;
		LogFiles.log.log(Level.INFO, "t_signal - " + (System.currentTimeMillis() - start) / 1000 + " s");
		final Map<Integer, Tsignal> signals = signals2;
		start = System.currentTimeMillis();
		
		while (!isDataFromDB) {
			SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
			Timestamp dt = null;
			try {
				dt = new Timestamp(formatter.parse(formatter.format(new Date())).getTime()); // 00 min, 00 sec
			} catch (ParseException e) {
				LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
			}

			List<Alarm> alsm = pdb.getAlarms(dt);
			alsm.forEach(a -> { SingleFromDB.addAlarm(a); });
			isDataFromDB = alsm == null ? false : true;
		}
		isDataFromDB = false;
		LogFiles.log.log(Level.INFO, "Alarms - " + (System.currentTimeMillis() - start) / 1000 + " s");
		start = System.currentTimeMillis();
		
		while (!isDataFromDB) {
			List<TSysParam> spm = pdb.getTSysParam();
			SingleFromDB.setTsysparmams(spm);
			isDataFromDB = spm == null ? false : true;
		}
		isDataFromDB = false;
		LogFiles.log.log(Level.INFO, "SysParam - " + (System.currentTimeMillis() - start) / 1000 + " s");
		start = System.currentTimeMillis();
		
		while (!isDataFromDB) {
			List<TViewParam> tvpm = pdb.getTViewParam();
			SingleFromDB.setTviewparams(tvpm);
			isDataFromDB = tvpm == null ? false : true;
		}
		isDataFromDB = false;
		LogFiles.log.log(Level.INFO, "ViewParam - " + (System.currentTimeMillis() - start) / 1000 + " s");
		start = System.currentTimeMillis();
				
		while (!isDataFromDB) {
			Map<Integer, Transparant> transparants = pdb.getTransparants();
			SingleFromDB.setTransparants(transparants);
			isDataFromDB = transparants == null ? false : true;
		}
		LogFiles.log.log(Level.INFO, "transparants - " + (System.currentTimeMillis() - start) / 1000 + " s");
		start = System.currentTimeMillis();
		
		new Thread(new DValTITopic(factory, jConn, "DvalTI", signals, getPdb()), "SendDValTI_Thread").start();
		new Thread(new DValTSTopic(factory, jConn, "DvalTS", getPdb()), "SendDValTS_Thread").start();
		new Thread(new AlarmsTopic(factory, jConn, "Alarms", getPdb()), "SendAlarms_Thread").start();
		new Thread(new TransparantsTopic(factory, jConn, "Transparants", getPdb()), "Transparants_Thread").start();
		
		LogFiles.log.log(Level.INFO, "Send... ");
	}
	
	public PostgresDB getPdb() {
		SingleFromDB.setSqlConnect(new SQLConnect(dbServer, dbPort, dbName, dbUser, dbPassword));
		return new PostgresDB(dbServer, dbPort, dbName, dbUser, dbPassword);
//		return new PostgresDB("127.0.0.1", "7676", "dimitrovEU");
	}
	
	public void setConnectionParams(String dbServer, String dbName, String dbUser, String dbPassword) {
		if (dbServer.equals("1")) {
			this.dbServer = "localhost";
			this.dbPort = "5432";
			this.dbName = "PowerSys_Donetsk_25";
			//this.dbName = "Zaporizh";
			this.dbUser = "postgres";
			this.dbPassword = "12345678";
			return;
		}
		if (dbServer.equals("2")) {
			this.dbServer = "10.248.194.104";
			this.dbPort = "5432";
			this.dbName = "PowerSys";
			this.dbUser = "postgres";
			this.dbPassword = "12345678";
			return;
		}
		if (dbServer.indexOf(":") != -1) {
			StringTokenizer st = new StringTokenizer(dbServer, ":");
			this.dbServer = st.nextToken();
			this.dbPort = st.nextToken();
		} else {
			this.dbServer = dbServer;
			this.dbPort = "5432";
		}
		this.dbName = dbName;
		this.dbUser = dbUser;
		this.dbPassword = dbPassword;
	}

}
