package topic;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.ObjectMessage;
import javax.jms.TopicPublisher;

import jdbc.PostgresDB;
import model.Alarm;
import model.ConfTree;
import model.DvalTI;
import model.DvalTS;
import model.TSysParam;
import model.TViewParam;
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
	
	boolean isDataFromDB = false;
	@Override
	public void run() {
		System.out.println("START at " + LocalDateTime.now());
		while (!isDataFromDB) {
			Map<Integer, Tsignal> signals = pdb.getTsignalsMap();
			LastData.setSignals(signals);
			isDataFromDB = signals == null ? false : true;
		}
		isDataFromDB = false;
		System.out.println("signals");
		
		while (!isDataFromDB) {
			Map<Integer, ConfTree> ctm = pdb.getConfTreeMap();
			LastData.setConfTree(ctm);
			isDataFromDB = ctm == null ? false : true;
		}
		isDataFromDB = false;
		System.out.println("ConfTree");
		
		while (!isDataFromDB) {
			SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
			Timestamp dt = null;
			try {
				dt = new Timestamp(formatter.parse(formatter.format(new Date())).getTime()); // 00 min, 00 sec
			} catch (ParseException e) {
				e.printStackTrace();
			}

			List<Alarm> alsm = pdb.getAlarms(dt);
			alsm.forEach(a -> { LastData.addAlarm(a); });
			isDataFromDB = alsm == null ? false : true;
		}
		isDataFromDB = false;
		System.out.println("Alarms");
		
		while (!isDataFromDB) {
			List<TSysParam> spm = pdb.getTSysParam();
			LastData.setTsysparmams(spm);
			isDataFromDB = spm == null ? false : true;
		}
		isDataFromDB = false;
		System.out.println("SysParam");
		
		while (!isDataFromDB) {
			List<TViewParam> tvpm = pdb.getTViewParam();
			LastData.setTviewparams(tvpm);
			isDataFromDB = tvpm == null ? false : true;
		}
		isDataFromDB = false;
		System.out.println("ViewParam");
		
		while (!isDataFromDB) {
			Map<Integer, DvalTI> oldTIs = pdb.getOldTI().stream().filter(it -> it != null).collect(Collectors.toMap(DvalTI::getSignalref, obj -> obj));
			oldTIs.values().forEach(ti -> { ti.setVal(ti.getVal() * LastData.getSignals().get(ti.getSignalref()).getKoef()); });
			LastData.setOldTI(oldTIs);
			isDataFromDB = oldTIs == null ? false : true;
		}
		isDataFromDB = false;
		System.out.println("oldTI");
		
		while (!isDataFromDB) {
			Map<Integer, DvalTS> oldTSs = pdb.getOldTS().stream().filter(it -> it != null).collect(Collectors.toMap(DvalTS::getSignalref, obj -> obj));
			oldTSs.values().forEach(ti -> { ti.setVal(ti.getVal() * LastData.getSignals().get(ti.getSignalref()).getKoef()); });
			LastData.setOldTS(oldTSs);
			isDataFromDB = oldTSs == null ? false : true;
		}
		System.out.println("oldTS");
		
		new Thread(new SendDValTI(factory, jConn, "DvalTI", LastData.getSignals(), getPdb()), "SendDValTI_Thread").start();
		new Thread(new SendDValTS(factory, jConn, "DvalTS", getPdb()), "SendDValTS_Thread").start();
		new Thread(new SendAlarms(factory, jConn, "Alarms", getPdb()), "SendAlarms_Thread").start();
		
		System.out.println("Send... " + LocalDateTime.now());
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
