package topic;

import java.util.Map;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Topic;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;

import jdbc.PostgresDB;
import model.Tsignal;

import com.sun.messaging.ConnectionConfiguration;
import com.sun.messaging.ConnectionFactory;
import com.sun.messaging.jms.Session;
import com.sun.messaging.jms.TopicConnection;

public class SendTopic {
	
	private String dbServer;
	private String dbServerPort;
	private String dbPort;
	private String dbName;
	private String dbUser;
	private String dbPassword;
	
	public ObjectMessage msgO;
	public TopicPublisher publisher;

	public void run(TextField tfdbServer, TextField tfdbName, TextField tfdbUser, 
			PasswordField pfpassword, Button btnSignin) {
//		org.apache.log4j.BasicConfigurator.configure();
		ConnectionFactory factory;
		TopicConnection connection = null;
		TopicSession pubSession = null;

		try {
			new Thread(new Runnable() {
	            @Override public void run() {
	                Platform.runLater(new Runnable() {
	                    @Override public void run() {
	                    	tfdbServer.setDisable(true);
	                    	tfdbName.setDisable(true);
	                    	tfdbUser.setDisable(true);
	                    	pfpassword.setDisable(true);
	                    	btnSignin.setDisable(true);
	                    }
	                });
	            }
	        }, "disableElements").start();
			
			factory = new com.sun.messaging.ConnectionFactory();
			factory.setProperty(ConnectionConfiguration.imqAddressList, "mq://127.0.0.1:7676,mq://127.0.0.1:7676");
			connection = (TopicConnection) factory.createTopicConnection("admin","admin");
			pubSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
			
			connection.start();

			Map<Integer, Tsignal> signals = getPdb().getTsignalsMap();
			
			SendDValTI sDvalTI = new SendDValTI(pubSession, "DvalTI", signals);
			sDvalTI.setConnectionParams(dbServerPort, dbName, dbUser, dbPassword);
			new Thread(sDvalTI, "SendDValTI_Thread").start();
			
			SendDValTS sDvalTS = new SendDValTS(pubSession, "DvalTS");
			sDvalTS.setConnectionParams(dbServerPort, dbName, dbUser, dbPassword);
			new Thread(sDvalTS, "SendDValTS_Thread").start();
			
			SendAlarms sAlarms = new SendAlarms(pubSession, "Alarms", false);
			sAlarms.setConnectionParams(dbServerPort, dbName, dbUser, dbPassword);
			new Thread(sAlarms, "SendAlarms_Thread").start();
			
			SendAlarms sAlarmsConfirm = new SendAlarms(pubSession, "Alarms", true);
			sAlarmsConfirm.setConnectionParams(dbServerPort, dbName, dbUser, dbPassword);
			new Thread(sAlarmsConfirm, "SendAlarmsConfirm_Thread").start();			
			
			System.out.println("Sending ...");
			
			while (true) {

			}
		} catch (Exception e) {
			System.err.println("SendTopic");
			e.printStackTrace();
		} finally {
			try {
				pubSession.close();
		        connection.close();
		    } catch (Exception e) {
		    	System.out.println("Can't close JMS connection/session " + e.getMessage());
		    }
		}
		
	}
	
	public PostgresDB getPdb() {
		return new PostgresDB(dbServer, dbPort, dbName, dbUser, dbPassword);
	}
	
	public void setConnectionParams(String dbServer, String dbName, String dbUser, String dbPassword) {
		dbServerPort = dbServer;

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
	
	public void setTopic(TopicSession pubSession, String nameTopic) {
		try {
			Topic topic = pubSession.createTopic(nameTopic);
			publisher = pubSession.createPublisher(topic);
			msgO = pubSession.createObjectMessage();
		} catch (JMSException e) {
			System.err.println("public void setTopic ...");
		}
	}
}
