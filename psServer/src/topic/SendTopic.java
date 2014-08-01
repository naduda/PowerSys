package topic;

import java.util.Map;

import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.ObjectMessage;
import javax.jms.TopicPublisher;

import jdbc.PostgresDB;
import model.Tsignal;

import com.sun.messaging.ConnectionConfiguration;
import com.sun.messaging.ConnectionFactory;

public class SendTopic {
	
	private String dbServer;
	private String dbPort;
	private String dbName;
	private String dbUser;
	private String dbPassword;
	
	public ObjectMessage msgO;
	public TopicPublisher publisher;
	public JMSContext context;
	public JMSProducer producer;
	private ConnectionFactory factory;

	public SendTopic(String dbServer, String dbName, String dbUser, String dbPassword) {
		try {
			setConnectionParams(dbServer, dbName, dbUser, dbPassword);
			factory = new com.sun.messaging.ConnectionFactory();
			factory.setProperty(ConnectionConfiguration.imqAddressList, "mq://127.0.0.1:7676,mq://127.0.0.1:7676");

			Map<Integer, Tsignal> signals = getPdb().getTsignalsMap();

			new Thread(new SendDValTI(factory, "DvalTI", signals, getPdb()), "SendDValTI_Thread").start();
			new Thread(new SendDValTS(factory, "DvalTS", getPdb()), "SendDValTS_Thread").start();
			new Thread(new SendAlarms(factory, "Alarms", false, getPdb()), "SendAlarms_Thread").start();
			new Thread(new SendAlarms(factory, "Alarms", true, getPdb()), "SendAlarmsConfirm_Thread").start();	
			
			System.out.println("Send ...");
		} catch (JMSException e) {
			System.err.println("SendTopic()");
		}
		
	}
	
	public PostgresDB getPdb() {
		return new PostgresDB(dbServer, dbPort, dbName, dbUser, dbPassword);
	}
	
	public void setConnectionParams(String dbServer, String dbName, String dbUser, String dbPassword) {
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
