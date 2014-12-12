package topic;

import java.util.StringTokenizer;
import java.util.logging.Level;

import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.ObjectMessage;
import javax.jms.TopicPublisher;

import jdbc.PostgresDB;
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
	
	@Override
	public void run() {
		new SingleFromDB(pdb);
		
		new Thread(new DValTITopic(factory, jConn, "DvalTI", SingleFromDB.signals, getPdb()), "SendDValTI_Thread").start();
		new Thread(new DValTSTopic(factory, jConn, "DvalTS", getPdb()), "SendDValTS_Thread").start();
		new Thread(new AlarmsTopic(factory, jConn, "Alarms", getPdb()), "SendAlarms_Thread").start();
		new Thread(new TransparantsTopic(factory, jConn, "Transparants", getPdb()), "Transparants_Thread").start();
		
		LogFiles.log.log(Level.INFO, "Send... ");
	}
	
	public PostgresDB getPdb() {
		SingleFromDB.setSqlConnect(new SQLConnect(dbServer, dbPort, dbName, dbUser, dbPassword));
		return new PostgresDB(dbServer, dbPort, dbName, dbUser, dbPassword);
//		...\glassfish\glassfish\lib\gf-client.jar
//		return new PostgresDB("127.0.0.1", "3700", "dimitrovEU");
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
		if (dbServer.equals("3")) {
			this.dbServer = "vik-soft.com.ua";
			this.dbPort = "5432";
			this.dbName = "PowerSys_Kazahstan";
			this.dbUser = "postgres";
			this.dbPassword = "314159265";
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
