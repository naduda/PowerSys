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

import model.Alarm;
import model.ConfTree;
import model.DvalTI;
import model.DvalTS;
import model.LinkedValue;
import model.TSysParam;
import model.TViewParam;
import model.Tsignal;

public class PostgresDB {

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
	
	public Map<Integer, Tsignal> getTsignalsMap() {
		try {
			session = sqlSessionFactory.openSession();
			return session.getMapper(IMapper.class).getTsignalsMap();
		} catch (Exception e) {
			System.out.println("getTsignalsMap");
			return null;
		} finally {
			session.close();
		}
	}
	
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
	
	public Map<Integer, LinkedValue> getOldTI() {
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
	
	public Map<Integer, LinkedValue> getOldTS() {
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

	public Integer setTS(int idsignal, int val, int schemeref) {
		try {
			session = sqlSessionFactory.openSession();
			return session.getMapper(IMapper.class).setTS(idsignal, val, schemeref);
		} catch (Exception e) {
			return null;
		} finally {
			session.close();
		}
	}
	
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
	
	public Map<String, TSysParam> getTSysParamMap(String paramname) {
		try {
			session = sqlSessionFactory.openSession();
			return session.getMapper(IMapper.class).getTSysParamMap(paramname);
		} catch (Exception e) {
			System.out.println("getTSysParamMap");
			return null;
		} finally {
			session.close();
		}
	}
	
	public List<TViewParam> getTViewParam(String objdenom, String paramdenom, int userref) {
		try {
			session = sqlSessionFactory.openSession();
			return session.getMapper(IMapper.class).getTViewParam(objdenom, paramdenom, userref);
		} catch (Exception e) {
			System.out.println("getTViewParam");
			return null;
		} finally {
			session.close();
		}
	}
}
