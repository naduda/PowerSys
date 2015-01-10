package topic;

import java.util.StringTokenizer;
import java.util.logging.Level;

import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.ObjectMessage;
import javax.jms.TopicPublisher;

import pr.topic.JMSConnection;
import pr.log.LogFiles;
import single.SQLConnect;
import single.SingleFromDB;
import single.SingleObject;

import com.sun.messaging.ConnectionConfiguration;
import com.sun.messaging.ConnectionFactory;

public class SendTopic implements Runnable {	
	private String dbPort;
	
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
			
			factory = new com.sun.messaging.ConnectionFactory();
			factory.setProperty(ConnectionConfiguration.imqAddressList, jConn.getConnConfiguration());
			LogFiles.log.log(Level.INFO, "<JMSConnection> is ready. Port = 7676");
		} catch (JMSException e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	@Override
	public void run() {
		new SingleFromDB();
		
		new Thread(new DValTITopic(factory, jConn, "DvalTI", SingleFromDB.getSignals(), SingleFromDB.getPdb()), "SendDValTI_Thread").start();
		new Thread(new DValTSTopic(factory, jConn, "DvalTS", SingleFromDB.getPdb()), "SendDValTS_Thread").start();
		new Thread(new AlarmsTopic(factory, jConn, "Alarms", SingleFromDB.getPdb()), "SendAlarms_Thread").start();
		new Thread(new TransparantsTopic(factory, jConn, "Transparants", SingleFromDB.getPdb()), "Transparants_Thread").start();
		SingleObject.chatTopic = new ChatTopic(factory, jConn, "ChatTopic");
		new Thread(SingleObject.chatTopic, "ChatTopic_Thread").start();
		
		LogFiles.log.log(Level.INFO, "Send... ");
	}
	
	private void setConnectionParams(String dbServer, String dbName, String dbUser, String dbPassword) {
		if (dbServer.equals("1")) {
			dbServer = "localhost";
			dbPort = "5432";
			dbName = "PowerSys_Donetsk_25";
			//this.dbName = "Zaporizh";
			dbUser = "postgres";
			dbPassword = "12345678";
			SingleFromDB.setSqlConnect(new SQLConnect(dbServer, dbPort, dbName, dbUser, dbPassword));
			return;
		}
		if (dbServer.equals("2")) {
			dbServer = "10.248.194.104";
			dbPort = "5432";
			dbName = "PowerSys";
			dbUser = "postgres";
			dbPassword = "12345678";
			SingleFromDB.setSqlConnect(new SQLConnect(dbServer, dbPort, dbName, dbUser, dbPassword));
			return;
		}
		if (dbServer.equals("3")) {
			dbServer = "vik-soft.com.ua";
			dbPort = "5432";
			dbName = "PowerSys_Kazahstan";
			dbUser = "postgres";
			dbPassword = "314159265";
			SingleFromDB.setSqlConnect(new SQLConnect(dbServer, dbPort, dbName, dbUser, dbPassword));
			return;
		}
		if (dbServer.equals("4")) {
			dbServer = "7.95.146.102";
			dbPort = "5432";
			dbName = "PowerSys";
			dbUser = "postgres";
			dbPassword = "12345678";
			SingleFromDB.setSqlConnect(new SQLConnect(dbServer, dbPort, dbName, dbUser, dbPassword));
			return;
		}
		if (dbServer.indexOf(":") != -1) {
			StringTokenizer st = new StringTokenizer(dbServer, ":");
			dbServer = st.nextToken();
			dbPort = st.nextToken();
		} else {
			dbPort = "5432";
		}
		SingleFromDB.setSqlConnect(new SQLConnect(dbServer, dbPort, dbName, dbUser, dbPassword));
	}

}
