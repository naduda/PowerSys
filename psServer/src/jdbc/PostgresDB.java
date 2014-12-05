package jdbc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.stream.Collectors;

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
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import pr.common.WMF2PNG;
import pr.log.LogFiles;
import pr.model.Alarm;
import pr.model.ConfTree;
import pr.model.ControlJournalItem;
import pr.model.DvalTI;
import pr.model.DvalTS;
import pr.model.LinkedValue;
import pr.model.NormalModeJournalItem;
import pr.model.SPunit;
import pr.model.SpTuCommand;
import pr.model.SpTypeSignal;
import pr.model.SwitchEquipmentJournalItem;
import pr.model.TSysParam;
import pr.model.TViewParam;
import pr.model.TalarmParam;
import pr.model.Transparant;
import pr.model.Tsignal;
import pr.model.TtranspHistory;
import pr.model.TtranspLocate;
import pr.model.Ttransparant;
import pr.model.Tuser;
import pr.model.UserEventJournalItem;
import pr.model.VsignalView;

public class PostgresDB implements IMapperSP, IMapperT, IMapperAction, IMapperV {
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
	
//	=============================================================================================
	public static String getQuery(Map<String, Object> params)
    {
        return params.get("query").toString();
    }

    public interface BaseMapper
    {
        @SelectProvider(type = PostgresDB.class, method = "getQuery")
        void update(@Param("query") String query);

        @SelectProvider(type = PostgresDB.class, method = "getQuery")
		List<NormalModeJournalItem> getListNormalModeItems(@Param("query") String query);
        
        @SelectProvider(type = PostgresDB.class, method = "getQuery")
        List<SwitchEquipmentJournalItem> getSwitchJournalItems(@Param("query") String query);
    }
//    -----------------------------------------------------------------------------------------
	public void update(String query) {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession(true);
			session.getMapper(BaseMapper.class).update(query);			
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
			LogFiles.log.log(Level.SEVERE, query);
		} finally {
			session.close();
		}
	}
    
	public List<NormalModeJournalItem> getListNormalModeItems(String query) {
		SqlSession session = null;
    	try {
			session = sqlSessionFactory.openSession(true);
			return session.getMapper(BaseMapper.class).getListNormalModeItems(query);			
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
			LogFiles.log.log(Level.SEVERE, query);
			return null;
		} finally {
			session.close();
		}
	}
    
    public List<SwitchEquipmentJournalItem> getSwitchJournalItems(String query) {
    	SqlSession session = null;
    	try {
			session = sqlSessionFactory.openSession(true);
			return session.getMapper(BaseMapper.class).getSwitchJournalItems(query);			
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
			LogFiles.log.log(Level.SEVERE, query);
			return null;
		} finally {
			session.close();
		}
    }
//	==============================================================================================
	public List<DvalTI> getLastTI(Timestamp servdt) {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession(true);
			return session.getMapper(IMapper.class).getLastTI(servdt);
		} catch (Exception e) {
			return null;
		} finally {
			session.close();
		}
	}
	
	public List<DvalTI> getOldTI(String idSignals) {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession(true);
			return session.getMapper(IMapper.class).getOldTI(idSignals).stream().filter(f -> f != null).collect(Collectors.toList());
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
			return null;
		} finally {
			if (session != null) session.close();
		}
	}
	
	public List<DvalTS> getLastTS(Timestamp servdt) {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession(true);
			return session.getMapper(IMapper.class).getLastTS(servdt);
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
			return null;
		} finally {
			session.close();
		}
	}
	
	public List<DvalTS> getOldTS(String idSignals) {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession(true);
			return session.getMapper(IMapper.class).getOldTS(idSignals).stream().filter(f -> f != null).collect(Collectors.toList());
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
			return null;
		} finally {
			session.close();
		}
	}
	
	public Integer setTS(int idsignal, double val, int schemeref) {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession(true);
			return session.getMapper(IMapper.class).setTS(idsignal, val, schemeref);
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
			return null;
		} finally {
			session.close();
		}
	}
	
	public List<Alarm> getAlarms(Timestamp eventdt) {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession(true);
			return session.getMapper(IMapper.class).getAlarms(eventdt);
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
			return null;
		} finally {
			session.close();
		}
	}
	
	public List<Alarm> getAlarmsConfirm(Timestamp confirmdt) {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession(true);
			return session.getMapper(IMapper.class).getAlarmsConfirm(confirmdt);
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
			return null;
		} finally {
			session.close();
		}
	}
	
	public Alarm getHightPriorityAlarm() {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession(true);
			return session.getMapper(IMapper.class).getHightPriorityAlarm();
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
			return null;
		} finally {
			session.close();
		}
	}
	
	public List<TalarmParam> getTalarmParams(int alarmref) {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession(true);
			return session.getMapper(IMapper.class).getTalarmParams(alarmref);
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
			return null;
		} finally {
			session.close();
		}
	}
	
	public List<LinkedValue> getData(int signalref) {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession(true);
			return session.getMapper(IMapper.class).getData(signalref);
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
			return null;
		} finally {
			session.close();
		}
	}
	
	public List<LinkedValue> getDataIntegr(int idSignal, Timestamp dtBeg, Timestamp dtEnd, int period) {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession(true);
			return session.getMapper(IMapper.class).getDataIntegr(idSignal, dtBeg, dtEnd, period);
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
			return null;
		} finally {
			session.close();
		}
	}
	
	public List<LinkedValue> getDataIntegrArc(int idSignal, Timestamp dtBeg, Timestamp dtEnd, int period) {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession(true);
			return session.getMapper(IMapper.class).getDataIntegrArc(idSignal, dtBeg, dtEnd, period);
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
			return null;
		} finally {
			session.close();
		}
	}
	
	public List<LinkedValue> getDataArc(int idSignal, Timestamp dtBeg, Timestamp dtEnd) {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession(true);
			return session.getMapper(IMapper.class).getDataArc(idSignal, dtBeg, dtEnd);
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
			return null;
		} finally {
			session.close();
		}
	}
	
	public List<Alarm> getAlarmsPeriod(Timestamp dtBeg, Timestamp dtEnd) {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession(true);
			return session.getMapper(IMapper.class).getAlarmsPeriod(dtBeg, dtEnd);
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
			return null;
		} finally {
			session.close();
		}
	}
	
	public List<Alarm> getAlarmsPeriodById(Timestamp dtBeg, Timestamp dtEnd, int idSignal) {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession(true);
			return session.getMapper(IMapper.class).getAlarmsPeriodById(dtBeg, dtEnd, idSignal);
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
			return null;
		} finally {
			session.close();
		}
	}
	
	public List<Integer> getNotConfirmedSignals(String idSignals) {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession(true);
			return session.getMapper(IMapper.class).getNotConfirmedSignals(idSignals);
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
			return null;
		} finally {
			session.close();
		}
	}
	
	public List<ControlJournalItem> getJContrlItems(Timestamp dtBeg, Timestamp dtEnd) {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession(true);
			return session.getMapper(IMapper.class).getJContrlItems(dtBeg, dtEnd);
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
			return null;
		} finally {
			session.close();
		}
	}
	
	public List<UserEventJournalItem> getUserEventJournalItems(Timestamp dtBeg, Timestamp dtEnd) {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession(true);
			return session.getMapper(IMapper.class).getUserEventJournalItems(dtBeg, dtEnd);
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
			return null;
		} finally {
			session.close();
		}
	}
//	----- SP -----
	@Override
	public Map<Integer, SPunit> getSPunitMap() {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession(true);
			return session.getMapper(IMapperSP.class).getSPunitMap();
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
			return null;
		} finally {
			session.close();
		}
	}
	
	@Override
	public List<SpTuCommand> getSpTuCommand() {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession(true);
			return session.getMapper(IMapperSP.class).getSpTuCommand();
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
			return null;
		} finally {
			session.close();
		}
	}
	
	@Override
	public Map<Integer, SpTypeSignal> getSpTypeSignalMap() {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession(true);
			return session.getMapper(IMapperSP.class).getSpTypeSignalMap();
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
			return null;
		} finally {
			session.close();
		}
	}
//	----- T -----
	@Override
	public Map<Integer, Tsignal> getTsignalsMap() {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession(true);
			return session.getMapper(IMapperT.class).getTsignalsMap();
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, "getTsignalsMap()", e);
			return null;
		} finally {
			session.close();
		}
	}
	
	@Override
	public Map<Integer, Tuser> getTuserMap() {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession(true);
			return session.getMapper(IMapperT.class).getTuserMap();
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
			return null;
		} finally {
			session.close();
		}
	}
	
	@Override
	public Map<Integer, ConfTree> getConfTreeMap() {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession(true);
			return session.getMapper(IMapperT.class).getConfTreeMap();
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
			return null;
		} finally {
			session.close();
		}
	}

	@Override
	public List<TSysParam> getTSysParam() {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession(true);
			return session.getMapper(IMapperT.class).getTSysParam();
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
			return null;
		} finally {
			session.close();
		}
	}

	@Override
	public List<TViewParam> getTViewParam() {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession(true);
			return session.getMapper(IMapperT.class).getTViewParam();
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
			return null;
		} finally {
			session.close();
		}
	}
	
	@Override
	public Object getTransparantById(int idTransparant) {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession(true);
			return session.getMapper(IMapperT.class).getTransparantById(idTransparant);
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
			return null;
		} finally {
			session.close();
		}
	}

	@Override
	public Map<Integer, Transparant> getTransparants() {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession(true);
			Map<Integer, Transparant> transp = session.getMapper(IMapperT.class).getTransparants();
			transp.values().forEach(t -> {
				byte[] bytes = (byte[]) t.getImg();
				InputStream is = WMF2PNG.convert(new ByteArrayInputStream(bytes), 250);
				t.setImageByteArray(InputStream2ByteArray(is));
			});
			return transp;
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
			return null;
		} finally {
			session.close();
		}
	}
	
	private byte[] InputStream2ByteArray(InputStream is) {
		int nRead;
		byte[] data = new byte[1024];

		try (ByteArrayOutputStream buffer = new ByteArrayOutputStream();) {
			while ((nRead = is.read(data, 0, data.length)) != -1) {
			  buffer.write(data, 0, nRead);
			}
			buffer.flush();
			
			return buffer.toByteArray();
		} catch (IOException e) {
			LogFiles.log.log(Level.SEVERE, "InputStream2ByteArray(InputStream is)", e);
		}

		return null;
	}

	@Override
	public List<Ttransparant> getTtransparantsActive(int idScheme) {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession(true);
			return session.getMapper(IMapperT.class).getTtransparantsActive(idScheme);
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
			return null;
		} finally {
			session.close();
		}
	}
	
	@Override
	public List<Ttransparant> getTtransparantsNew(Timestamp settime) {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession(true);
			return session.getMapper(IMapperT.class).getTtransparantsNew(settime);
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
			return null;
		} finally {
			session.close();
		}
	}

	@Override
	public TtranspLocate getTransparantLocate(int trref) {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession(true);
			return session.getMapper(IMapperT.class).getTransparantLocate(trref);
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
			return null;
		} finally {
			session.close();
		}
	}

	@Override
	public List<Ttransparant> getTtransparantsClosed(Timestamp closetime) {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession(true);
			return session.getMapper(IMapperT.class).getTtransparantsClosed(closetime);
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
			return null;
		} finally {
			session.close();
		}
	}

	@Override
	public List<Ttransparant> getTtransparantsUpdated(Timestamp lastupdate) {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession(true);
			return session.getMapper(IMapperT.class).getTtransparantsUpdated(lastupdate);
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
			return null;
		} finally {
			session.close();
		}
	}
	
	@Override
	public int getMaxTranspID() {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession(true);
			return session.getMapper(IMapperT.class).getMaxTranspID();
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
			return 0;
		} finally {
			session.close();
		}
	}

	@Override
	public void updateTtransparantCloseTime(int idtr) {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession(true);
			session.getMapper(IMapperT.class).updateTtransparantCloseTime(idtr);
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
		} finally {
			session.close();
		}
	}

	@Override
	public TtranspHistory getTtranspHistory(int trref) {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession(true);
			return session.getMapper(IMapperT.class).getTtranspHistory(trref);
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
			return null;
		} finally {
			session.close();
		}
	}

	@Override
	public Ttransparant getTtransparantById(int idtr) {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession(true);
			return session.getMapper(IMapperT.class).getTtransparantById(idtr);
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
			return null;
		} finally {
			session.close();
		}
	}
	
//	----- Action -----
	@Override
	public void confirmAlarm(Timestamp recorddt, Timestamp eventdt, int objref, Timestamp confirmdt, String lognote, int userref) {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession(true);
			session.getMapper(IMapperAction.class).confirmAlarm(recorddt, eventdt, objref, confirmdt, lognote, userref);
			session.getMapper(IMapperAction.class).deleteLastUserAck();
			session.getMapper(IMapperAction.class).insertLastUserAck();
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
		} finally {
			session.close();
		}
	}

	@Override
	public void deleteLastUserAck() {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession(true);
			session.getMapper(IMapperAction.class).deleteLastUserAck();
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
		} finally {
			session.close();
		}
	}

	@Override
	public void insertLastUserAck() {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession(true);
			session.getMapper(IMapperAction.class).insertLastUserAck();
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
		} finally {
			session.close();
		}
	}
	
	@Override
	public void confirmAlarmAll(String lognote, int userref) {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession(true);
			session.getMapper(IMapperAction.class).confirmAlarmAll(lognote, userref);
			session.getMapper(IMapperAction.class).deleteLastUserAck();
			session.getMapper(IMapperAction.class).insertLastUserAck();
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
		} finally {
			session.close();
		}
	}
	
	@Override
	public void insertTtransparant(int idtr, int signref, String objname, int tp, int schemeref) {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession(true);
			session.getMapper(IMapperAction.class).insertTtransparant(idtr, signref, objname, tp, schemeref);
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
		} finally {
			session.close();
		}
	}

	@Override
	public void insertTtranspHistory(int trref, int userref, String txt, int trtype) {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession(true);
			session.getMapper(IMapperAction.class).insertTtranspHistory(trref, userref, txt, trtype);
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
		} finally {
			session.close();
		}
	}

	@Override
	public void deleteTtranspLocate(int trref, int scref) {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession(true);
			session.getMapper(IMapperAction.class).deleteTtranspLocate(trref, scref);
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
		} finally {
			session.close();
		}
	}

	@Override
	public void insertTtranspLocate(int trref, int scref, int x, int y, int h, int w) {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession(true);
			session.getMapper(IMapperAction.class).insertTtranspLocate(trref, scref, x, y, h, w);
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
		} finally {
			session.close();
		}
	}
	
	@Override
	public void updateTtranspLocate(int trref, int scref, int x, int y, int h, int w) {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession(true);
			session.getMapper(IMapperAction.class).updateTtranspLocate(trref, scref, x, y, h, w);
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
		} finally {
			session.close();
		}
	}

	@Override
	public void updateTtransparantLastUpdate(int idtr) {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession(true);
			session.getMapper(IMapperAction.class).updateTtransparantLastUpdate(idtr);
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
		} finally {
			session.close();
		}
	}
	
	@Override
	public void updateTtranspHistory(int trref, String txt) {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession(true);
			session.getMapper(IMapperAction.class).updateTtranspHistory(trref, txt);
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
		} finally {
			session.close();
		}
	}
	
	@Override
	public void setBaseVal(int idSignal, double val) {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession(true);
			session.getMapper(IMapperAction.class).setBaseVal(idSignal, val);
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
		} finally {
			session.close();
		}
	}
	
	@Override
	public void updateTsignalStatus(int idSignal, int status) {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession(true);
			session.getMapper(IMapperAction.class).updateTsignalStatus(idSignal, status);
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
		} finally {
			session.close();
		}
	}
	
	@Override
	public void insertDeventLog(int eventtype, int objref, Timestamp eventdt, double objval, int objstatus, int authorref) {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession(true);
			session.getMapper(IMapperAction.class).insertDeventLog(eventtype, objref, eventdt, objval, objstatus, authorref);
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
		} finally {
			session.close();
		}
	}
//	----- V -----
	@Override
	public Map<Integer, VsignalView> getVsignalViewMap() {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession(true);
			return session.getMapper(IMapperV.class).getVsignalViewMap();
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
			return null;
		} finally {
			session.close();
		}
	}
}
