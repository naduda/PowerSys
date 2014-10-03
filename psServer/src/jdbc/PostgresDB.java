package jdbc;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.commons.dbcp2.cpdsadapter.DriverAdapterCPDS;
import org.apache.commons.dbcp2.datasources.SharedPoolDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import pr.model.Alarm;
import pr.model.ConfTree;
import pr.model.DvalTI;
import pr.model.DvalTS;
import pr.model.LinkedValue;
import pr.model.SPunit;
import pr.model.TSysParam;
import pr.model.TViewParam;
import pr.model.Transparant;
import pr.model.Tsignal;
import pr.model.TtranspLocate;
import pr.model.Ttransparant;

public class PostgresDB implements IMapper {

	private DataSource dataSource;
	private SqlSession session;
	private SqlSessionFactory sqlSessionFactory;
	
	public PostgresDB (String hostJNDI, String portJNDI, String dataSourceName) {
		Properties env = new Properties();
		env.setProperty("java.naming.factory.initial", "com.sun.enterprise.naming.SerialInitContextFactory");
		env.setProperty("java.naming.factory.url.pkgs", "com.sun.enterprise.naming");
		env.setProperty("java.naming.factory.state", "com.sun.corba.ee.impl.presentation.rmi.JNDIStateFactoryImpl");
		env.setProperty("org.omg.CORBA.ORBInitialHost", hostJNDI);
		env.setProperty("org.omg.CORBA.ORBInitialPort", portJNDI);
		
		try {	
			InitialContext ctx = new InitialContext(env);

			dataSource = (DataSource) ctx.lookup(dataSourceName);
			
			TransactionFactory transactionFactory = new JdbcTransactionFactory();
			Environment environment = new Environment("development", transactionFactory, dataSource);
			Configuration configuration = new Configuration(environment);
			configuration.addMapper(IMapper.class);
			sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public PostgresDB (String host, String port, String dbName, String user, String pass) {		
		try {
			DriverAdapterCPDS cpds = new DriverAdapterCPDS();
	        cpds.setDriver("org.postgresql.Driver");
	        String dbConnect = String.format("jdbc:postgresql://%s:%s/%s", host, port, dbName);
	        cpds.setUrl(dbConnect);
	        cpds.setUser(user);
	        cpds.setPassword(pass);

	        SharedPoolDataSource tds = new SharedPoolDataSource();
	        tds.setConnectionPoolDataSource(cpds);
	        tds.setMaxTotal(3);
	        tds.setDefaultMaxWaitMillis(50);
	        tds.setValidationQuery("select 1");
	        tds.setDefaultTestOnBorrow(true); 
	        tds.setDefaultTestOnReturn(true);
	        tds.setDefaultTestWhileIdle(true);

	        dataSource = tds;
			
			TransactionFactory transactionFactory = new JdbcTransactionFactory();
			Environment environment = new Environment("development", transactionFactory, dataSource);
			Configuration configuration = new Configuration(environment);
			configuration.addMapper(IMapper.class);
			sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	@Override
	public Map<Integer, Tsignal> getTsignalsMap() {
		try {
			session = sqlSessionFactory.openSession();
			return session.getMapper(IMapper.class).getTsignalsMap();
		} catch (Exception e) {
			System.err.println("getTsignalsMap");
			return null;
		} finally {
			session.close();
		}
	}
	
	@Override
	public List<DvalTI> getLastTI(Timestamp servdt) {
		try {
			session = sqlSessionFactory.openSession();
			return session.getMapper(IMapper.class).getLastTI(servdt);
		} catch (Exception e) {
			return null;
		} finally {
			session.close();
		}
	}
	
	@Override
	public List<DvalTI> getOldTI() {
		try {
			session = sqlSessionFactory.openSession();
			return session.getMapper(IMapper.class).getOldTI();
		} catch (Exception e) {
			System.out.println("getOldTI");
			return null;
		} finally {
			session.close();
		}
	}
	
	@Override
	public List<DvalTS> getLastTS(Timestamp servdt) {
		try {
			session = sqlSessionFactory.openSession();
			return session.getMapper(IMapper.class).getLastTS(servdt);
		} catch (Exception e) {
			return null;
		} finally {
			session.close();
		}
	}
	
	@Override
	public List<DvalTS> getOldTS() {
		try {
			session = sqlSessionFactory.openSession();
			return session.getMapper(IMapper.class).getOldTS();
		} catch (Exception e) {
			System.out.println("getOldTS");
			return null;
		} finally {
			session.close();
		}
	}

	@Override
	public Integer setTS(int idsignal, double val, int schemeref) {
		try {
			session = sqlSessionFactory.openSession();
			return session.getMapper(IMapper.class).setTS(idsignal, val, schemeref);
		} catch (Exception e) {
			return null;
		} finally {
			session.close();
		}
	}
	
	@Override
	public List<Alarm> getAlarms(Timestamp eventdt) {
		try {
			session = sqlSessionFactory.openSession();
			return session.getMapper(IMapper.class).getAlarms(eventdt);
		} catch (Exception e) {
			System.out.println("getAlarms");
			return null;
		} finally {
			session.close();
		}
	}
	
	@Override
	public List<Alarm> getAlarmsConfirm(Timestamp confirmdt) {
		try {
			session = sqlSessionFactory.openSession();
			return session.getMapper(IMapper.class).getAlarmsConfirm(confirmdt);
		} catch (Exception e) {
			System.out.println("getAlarmsConfirm");
			return null;
		} finally {
			session.close();
		}
	}
	
	@Override
	public Map<Integer, ConfTree> getConfTreeMap() {
		try {
			session = sqlSessionFactory.openSession();
			return session.getMapper(IMapper.class).getConfTreeMap();
		} catch (Exception e) {
			System.out.println("getConfTreeMap");
			return null;
		} finally {
			session.close();
		}
	}

	@Override
	public List<TSysParam> getTSysParam() {
		try {
			session = sqlSessionFactory.openSession();
			return session.getMapper(IMapper.class).getTSysParam();
		} catch (Exception e) {
			System.out.println("getTSysParam");
			return null;
		} finally {
			session.close();
		}
	}

	@Override
	public List<TViewParam> getTViewParam() {
		try {
			session = sqlSessionFactory.openSession();
			return session.getMapper(IMapper.class).getTViewParam();
		} catch (Exception e) {
			System.out.println("getTSysParam");
			return null;
		} finally {
			session.close();
		}
	}

	@Override
	public void confirmAlarm(Timestamp recorddt, Timestamp eventdt, int objref, Timestamp confirmdt, String lognote, int userref) {
		try {
			session = sqlSessionFactory.openSession(true);
			session.getMapper(IMapper.class).confirmAlarm(recorddt, eventdt, objref, confirmdt, lognote, userref);
			session.getMapper(IMapper.class).deleteLastUserAck();
			session.getMapper(IMapper.class).insertLastUserAck();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

	@Override
	public void deleteLastUserAck() {
		
	}

	@Override
	public void insertLastUserAck() {
		
	}
	
	@Override
	public void confirmAlarmAll(String lognote, int userref) {
		try {
			session = sqlSessionFactory.openSession(true);
			session.getMapper(IMapper.class).confirmAlarmAll(lognote, userref);
			session.getMapper(IMapper.class).deleteLastUserAck();
			session.getMapper(IMapper.class).insertLastUserAck();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

	@Override
	public List<LinkedValue> getData(int signalref) {
		try {
			session = sqlSessionFactory.openSession();
			return session.getMapper(IMapper.class).getData(signalref);
		} catch (Exception e) {
			System.out.println("getData");
			return null;
		} finally {
			session.close();
		}
	}

	@Override
	public Map<Integer, SPunit> getSPunitMap() {
		try {
			session = sqlSessionFactory.openSession();
			return session.getMapper(IMapper.class).getSPunitMap();
		} catch (Exception e) {
			System.err.println("getSPunitMap");
			return null;
		} finally {
			session.close();
		}
	}

	@Override
	public List<LinkedValue> getDataIntegr(int idSignal, Timestamp dtBeg, Timestamp dtEnd, int period) {
		try {
			session = sqlSessionFactory.openSession();
			return session.getMapper(IMapper.class).getDataIntegr(idSignal, dtBeg, dtEnd, period);
		} catch (Exception e) {
			System.out.println("getDataIntegr");
			return null;
		} finally {
			session.close();
		}
	}
	
	@Override
	public List<LinkedValue> getDataIntegrArc(int idSignal, Timestamp dtBeg, Timestamp dtEnd, int period) {
		try {
			session = sqlSessionFactory.openSession();
			return session.getMapper(IMapper.class).getDataIntegrArc(idSignal, dtBeg, dtEnd, period);
		} catch (Exception e) {
			System.out.println("getDataIntegrArc");
			return null;
		} finally {
			session.close();
		}
	}

	@Override
	public List<LinkedValue> getDataArc(int idSignal, Timestamp dtBeg, Timestamp dtEnd) {
		try {
			session = sqlSessionFactory.openSession();
			return session.getMapper(IMapper.class).getDataArc(idSignal, dtBeg, dtEnd);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("getDataArc");
			return null;
		} finally {
			session.close();
		}
	}

	@Override
	public Object getTransparantById(int idTransparant) {
		try {
			session = sqlSessionFactory.openSession();
			return session.getMapper(IMapper.class).getTransparantById(idTransparant);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("getTransparantById");
			return null;
		} finally {
			session.close();
		}
	}

	@Override
	public Map<Integer, Transparant> getTransparants() {
		try {
			session = sqlSessionFactory.openSession();
			return session.getMapper(IMapper.class).getTransparants();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("getTransparants");
			return null;
		} finally {
			session.close();
		}
	}

	@Override
	public List<Ttransparant> getTtransparantsActive(int idScheme) {
		try {
			session = sqlSessionFactory.openSession();
			return session.getMapper(IMapper.class).getTtransparantsActive(idScheme);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("getTtransparantsActive");
			return null;
		} finally {
			session.close();
		}
	}
	
	@Override
	public List<Ttransparant> getTtransparantsNew(Timestamp settime) {
		try {
			session = sqlSessionFactory.openSession();
			return session.getMapper(IMapper.class).getTtransparantsNew(settime);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("getTtransparantsNew");
			return null;
		} finally {
			session.close();
		}
	}

	@Override
	public TtranspLocate getTransparantLocate(int trref) {
		try {
			session = sqlSessionFactory.openSession();
			return session.getMapper(IMapper.class).getTransparantLocate(trref);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("getTransparantLocate");
			return null;
		} finally {
			session.close();
		}
	}
}
