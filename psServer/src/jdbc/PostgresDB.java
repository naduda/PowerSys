package jdbc;

import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import jdbc.mappers.IMapper;
import jdbc.mappers.IMapperAction;
import jdbc.mappers.IMapperSP;
import jdbc.mappers.IMapperT;
import jdbc.mappers.IMapperV;

import org.apache.commons.dbcp2.cpdsadapter.DriverAdapterCPDS;
import org.apache.commons.dbcp2.datasources.SharedPoolDataSource;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import pr.log.LogFiles;

public class PostgresDB {
	private static final int MAX_POOL_COUNT = 10;
	private DataSource dataSource;
	private SqlSessionFactory sqlSessionFactory;
	
	public PostgresDB() {
		
	}
	
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
			
			setMappers(dataSource);
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
			LogFiles.log.log(Level.INFO, "Exit ...");
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
	        tds.setMaxTotal(MAX_POOL_COUNT);
	        tds.setDefaultMaxWaitMillis(50);
	        tds.setValidationQuery("select 1");
	        tds.setDefaultTestOnBorrow(true); 
	        tds.setDefaultTestOnReturn(true);
	        tds.setDefaultTestWhileIdle(true);

	        dataSource = tds;
	        
			setMappers(dataSource);
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
			LogFiles.log.log(Level.INFO, "Exit ...");
			System.exit(0);
		}
	}
	
	private void setMappers(DataSource dataSource) {
		TransactionFactory transactionFactory = new JdbcTransactionFactory();
		Environment environment = new Environment("development", transactionFactory, dataSource);
		Configuration configuration = new Configuration(environment);
		configuration.addMapper(IMapper.class);
		configuration.addMapper(IMapperAction.class);
		configuration.addMapper(IMapperSP.class);
		configuration.addMapper(IMapperT.class);
		configuration.addMapper(IMapperV.class);
		//configuration.addMappers("jdbc.mappers");
		configuration.addMapper(BaseMapper.class);
		sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
	}
	
	public SqlSessionFactory getSqlSessionFactory() {
		return sqlSessionFactory;
	}
//	=============================================================================================
	public static String getQuery(Map<String, Object> params) {
        return params.get("query").toString();
    }

    public interface BaseMapper {
        @SelectProvider(type = PostgresDB.class, method = "getQuery")
        void update(@Param("query") String query);
    }
}
