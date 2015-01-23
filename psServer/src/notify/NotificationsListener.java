package notify;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.logging.Level;

import javax.jms.JMSException;

import org.postgresql.PGConnection;
import org.postgresql.PGNotification;

import com.sun.messaging.ConnectionFactory;

import pr.inter.IJMSConnection;
import pr.log.LogFiles;
import pr.model.DvalTI;
import pr.topic.ASender;
import single.SingleFromDB;

public class NotificationsListener extends ASender {
	public NotificationsListener(ConnectionFactory factory, IJMSConnection jConn, String topicName) {
		super(factory, jConn, topicName);
	}

	private PGConnection pgconn;
	private Connection conn;
	
	private PGConnection getPGConnection() {
		if (pgconn == null) {
			try {
				Class.forName("org.postgresql.Driver");
				String dbConnect = String.format("jdbc:postgresql://%s:%s/%s", 
						SingleFromDB.getSqlConnect().getIpAddress(), SingleFromDB.getSqlConnect().getPort(), SingleFromDB.getSqlConnect().getDbName());
				
				conn = DriverManager.getConnection(dbConnect, 
						SingleFromDB.getSqlConnect().getUser(), SingleFromDB.getSqlConnect().getPassword());
				
				pgconn = (PGConnection)conn; 
				Statement stmt = conn.createStatement();
				stmt.execute("LISTEN t_device_au; LISTEN tx_add; LISTEN tr_update; LISTEN t_appstates_ai;");
				stmt.close();
			} catch (ClassNotFoundException | SQLException e) {
				LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
			}
		}
		return pgconn;
	}

	public Timestamp senderMessage(Timestamp dt) {
		try {
			if (conn != null && !conn.isValid(10)) {
				System.out.println("Not valid Connection !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				pgconn = null;
			}
			
//			try (Statement stmt = conn.createStatement();) {
//				stmt.executeQuery("SELECT 1");
//			} catch (Exception e) {
//				pgconn = null;
//				System.out.println("**********************");
//				System.out.println("***********" + conn + "***********");
//				System.out.println("***********" + conn.isValid(10) + "***********");
//				System.out.println("**********************");
//			}
			
			PGNotification notifications[] = getPGConnection().getNotifications();
			if (notifications != null) {
				for (int i = 0; i < notifications.length; i++) {
					LogFiles.log.log(Level.INFO, LocalTime.now() + " -> " + notifications[i].getParameter());
					
					String nameNotify = notifications[i].getName().toLowerCase().trim();
					String parameters = notifications[i].getParameter().toLowerCase().trim();
					
					switch (nameNotify) {
						case "tx_add":
							DvalTI dti = getTX(parameters);
							msgObject.setObject(dti);
							break;
						case "t_device_au":
							msgObject.setObject("device;" + parameters);
							break;
						case "tr_update":
							//LogFiles.log.log(Level.INFO, "tr_update;" + parameters);
							break;
						case "t_appstates_ai":
							//LogFiles.log.log(Level.INFO, "t_appstates_ai;" + parameters);
							break;
					}
					
					if (msgObject.getObject() != null) producer.send(topic, msgObject);
				}
			}
		} catch (SQLException | JMSException e) {
			pgconn = null;
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e1) {
				LogFiles.log.log(Level.SEVERE, e1.getMessage(), e1);
			}
		}
		
		return null;
	}
	
	private DvalTI getTX(String parameters) {
		DvalTI dti = new DvalTI();
		StringTokenizer st = new StringTokenizer(parameters, ";");
		String tt = st.nextElement().toString();
		dti.setTypeSignal(tt);
		
		dti.setSignalref(Integer.parseInt(st.nextElement().toString()));
		dti.setVal(Double.parseDouble(st.nextElement().toString()));
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String strDate = st.nextElement().toString();
			if (strDate.length() == 19) strDate = strDate + ".000";
		    Date parsedDate = dateFormat.parse(strDate);
		    Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
		    dti.setDt(timestamp);
		    
		    dti.setRcode(Integer.parseInt(st.nextElement().toString()));
		    
		    strDate = st.nextElement().toString();
			if (strDate.length() == 19) strDate = strDate + ".000";
		    parsedDate = dateFormat.parse(strDate);
		    timestamp = new java.sql.Timestamp(parsedDate.getTime());
		    dti.setServdt(timestamp);
		} catch(Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
		}
				
		return dti;
	}

}
