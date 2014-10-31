package jdbc;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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
import pr.model.Transparant;
import pr.model.Tsignal;
import pr.model.TtranspHistory;
import pr.model.TtranspLocate;
import pr.model.Ttransparant;
import pr.model.Tuser;
import pr.model.UserEventJournalItem;
import pr.model.VsignalView;

public class PostgresDB implements IMapper, IMapperSP, IMapperT, IMapperAction, IMapperV {

	private DataSource dataSource;
	private SqlSession session;
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
			
			setMappers(dataSource);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	private void setMappers(DataSource dataSource) {
		TransactionFactory transactionFactory = new JdbcTransactionFactory();
		Environment environment = new Environment("development", transactionFactory, dataSource);
		Configuration configuration = new Configuration(environment);
		configuration.addMappers("jdbc.mappers");
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
    @Override
	public void update(String query) {
		try {
			session = sqlSessionFactory.openSession(true);
			session.getMapper(BaseMapper.class).update(query);			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("update --> query = " + query);
		} finally {
			session.close();
		}
	}
    
    @Override
	public List<NormalModeJournalItem> getListNormalModeItems(String query) {
    	try {
			session = sqlSessionFactory.openSession(true);
			return session.getMapper(BaseMapper.class).getListNormalModeItems(query);			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("getList --> query = " + query);
			return null;
		} finally {
			session.close();
		}
	}
    
    @Override
    public List<SwitchEquipmentJournalItem> getSwitchJournalItems(String query) {
    	try {
			session = sqlSessionFactory.openSession(true);
			return session.getMapper(BaseMapper.class).getSwitchJournalItems(query);			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("getList --> query = " + query);
			return null;
		} finally {
			session.close();
		}
    }
//	==============================================================================================
	
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
	public List<Alarm> getAlarmsPeriod(Timestamp dtBeg, Timestamp dtEnd) {
		try {
			session = sqlSessionFactory.openSession();
			return session.getMapper(IMapper.class).getAlarmsPeriod(dtBeg, dtEnd);
		} catch (Exception e) {
			System.err.println("getAlarmsPeriod(Timestamp, Timestamp)");
			e.printStackTrace();
			return null;
		} finally {
			session.close();
		}
	}
	
	@Override
	public List<Alarm> getAlarmsPeriodById(Timestamp dtBeg, Timestamp dtEnd, int idSignal) {
		try {
			session = sqlSessionFactory.openSession();
			return session.getMapper(IMapper.class).getAlarmsPeriodById(dtBeg, dtEnd, idSignal);
		} catch (Exception e) {
			System.err.println("getAlarmsPeriodById(...)");
			e.printStackTrace();
			return null;
		} finally {
			session.close();
		}
	}

	@Override
	public List<ControlJournalItem> getJContrlItems(Timestamp dtBeg, Timestamp dtEnd) {
		try {
			session = sqlSessionFactory.openSession();
			return session.getMapper(IMapper.class).getJContrlItems(dtBeg, dtEnd);
		} catch (Exception e) {
			System.err.println("getJContrlItems(Timestamp, Timestamp)");
			e.printStackTrace();
			return null;
		} finally {
			session.close();
		}
	}
	
	@Override
	public List<UserEventJournalItem> getUserEventJournalItems(Timestamp dtBeg, Timestamp dtEnd) {
		try {
			session = sqlSessionFactory.openSession();
			return session.getMapper(IMapper.class).getUserEventJournalItems(dtBeg, dtEnd);
		} catch (Exception e) {
			System.err.println("getUserEventJournalItems()");
			e.printStackTrace();
			return null;
		} finally {
			session.close();
		}
	}
//	----- SP -----
	@Override
	public Map<Integer, SPunit> getSPunitMap() {
		try {
			session = sqlSessionFactory.openSession();
			return session.getMapper(IMapperSP.class).getSPunitMap();
		} catch (Exception e) {
			System.err.println("getSPunitMap");
			return null;
		} finally {
			session.close();
		}
	}
	
	@Override
	public List<SpTuCommand> getSpTuCommand() {
		try {
			session = sqlSessionFactory.openSession();
			return session.getMapper(IMapperSP.class).getSpTuCommand();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("getSpTuCommand");
			return null;
		} finally {
			session.close();
		}
	}
	
	@Override
	public Map<Integer, SpTypeSignal> getSpTypeSignalMap() {
		try {
			session = sqlSessionFactory.openSession();
			return session.getMapper(IMapperSP.class).getSpTypeSignalMap();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("getSpTypeSignalMap");
			return null;
		} finally {
			session.close();
		}
	}
//	----- T -----
	@Override
	public Map<Integer, Tsignal> getTsignalsMap() {
		try {
			session = sqlSessionFactory.openSession();
			return session.getMapper(IMapperT.class).getTsignalsMap();
		} catch (Exception e) {
			System.err.println("getTsignalsMap");
			e.printStackTrace();
			System.exit(0);
			return null;
		} finally {
			session.close();
		}
	}
	
	@Override
	public Map<Integer, Tuser> getTuserMap() {
		try {
			session = sqlSessionFactory.openSession();
			return session.getMapper(IMapperT.class).getTuserMap();
		} catch (Exception e) {
			System.err.println("getTsignalsMap");
			return null;
		} finally {
			session.close();
		}
	}
	
	@Override
	public Map<Integer, ConfTree> getConfTreeMap() {
		try {
			session = sqlSessionFactory.openSession();
			return session.getMapper(IMapperT.class).getConfTreeMap();
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
			return session.getMapper(IMapperT.class).getTSysParam();
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
			return session.getMapper(IMapperT.class).getTViewParam();
		} catch (Exception e) {
			System.out.println("getTSysParam");
			return null;
		} finally {
			session.close();
		}
	}
	
	@Override
	public Object getTransparantById(int idTransparant) {
		try {
			session = sqlSessionFactory.openSession();
			return session.getMapper(IMapperT.class).getTransparantById(idTransparant);
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
			return session.getMapper(IMapperT.class).getTransparants();
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
			return session.getMapper(IMapperT.class).getTtransparantsActive(idScheme);
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
			return session.getMapper(IMapperT.class).getTtransparantsNew(settime);
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
			return session.getMapper(IMapperT.class).getTransparantLocate(trref);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("getTransparantLocate + trref=" + trref);
			return null;
		} finally {
			session.close();
		}
	}

	@Override
	public List<Ttransparant> getTtransparantsClosed(Timestamp closetime) {
		try {
			session = sqlSessionFactory.openSession();
			return session.getMapper(IMapperT.class).getTtransparantsClosed(closetime);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("getTtransparantsClosed");
			return null;
		} finally {
			session.close();
		}
	}

	@Override
	public List<Ttransparant> getTtransparantsUpdated(Timestamp lastupdate) {
		try {
			session = sqlSessionFactory.openSession();
			return session.getMapper(IMapperT.class).getTtransparantsUpdated(lastupdate);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("getTtransparantsClosed");
			return null;
		} finally {
			session.close();
		}
	}
	
	@Override
	public int getMaxTranspID() {
		try {
			session = sqlSessionFactory.openSession();
			return session.getMapper(IMapperT.class).getMaxTranspID();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("getMaxTranspID");
			return 0;
		} finally {
			session.close();
		}
	}

	@Override
	public void updateTtransparantCloseTime(int idtr) {
		try {
			session = sqlSessionFactory.openSession(true);
			session.getMapper(IMapperT.class).updateTtransparantCloseTime(idtr);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

	@Override
	public TtranspHistory getTtranspHistory(int trref) {
		try {
			session = sqlSessionFactory.openSession();
			return session.getMapper(IMapperT.class).getTtranspHistory(trref);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("getTtranspHistory");
			return null;
		} finally {
			session.close();
		}
	}

	@Override
	public Ttransparant getTtransparantById(int idtr) {
		try {
			session = sqlSessionFactory.openSession();
			return session.getMapper(IMapperT.class).getTtransparantById(idtr);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("getTtransparantById");
			return null;
		} finally {
			session.close();
		}
	}
	
//	----- Action -----
	@Override
	public void confirmAlarm(Timestamp recorddt, Timestamp eventdt, int objref, Timestamp confirmdt, String lognote, int userref) {
		try {
			session = sqlSessionFactory.openSession(true);
			session.getMapper(IMapperAction.class).confirmAlarm(recorddt, eventdt, objref, confirmdt, lognote, userref);
			session.getMapper(IMapperAction.class).deleteLastUserAck();
			session.getMapper(IMapperAction.class).insertLastUserAck();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

	@Override
	public void deleteLastUserAck() {
		try {
			session = sqlSessionFactory.openSession(true);
			session.getMapper(IMapperAction.class).deleteLastUserAck();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

	@Override
	public void insertLastUserAck() {
		try {
			session = sqlSessionFactory.openSession(true);
			session.getMapper(IMapperAction.class).insertLastUserAck();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			session.close();
		}
	}
	
	@Override
	public void confirmAlarmAll(String lognote, int userref) {
		try {
			session = sqlSessionFactory.openSession(true);
			session.getMapper(IMapperAction.class).confirmAlarmAll(lognote, userref);
			session.getMapper(IMapperAction.class).deleteLastUserAck();
			session.getMapper(IMapperAction.class).insertLastUserAck();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			session.close();
		}
	}
	
	@Override
	public void insertTtransparant(int idtr, int signref, String objname, int tp, int schemeref) {
		try {
			session = sqlSessionFactory.openSession(true);
			session.getMapper(IMapperAction.class).insertTtransparant(idtr, signref, objname, tp, schemeref);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

	@Override
	public void insertTtranspHistory(int trref, int userref, String txt, int trtype) {
		try {
			session = sqlSessionFactory.openSession(true);
			session.getMapper(IMapperAction.class).insertTtranspHistory(trref, userref, txt, trtype);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

	@Override
	public void deleteTtranspLocate(int trref, int scref) {
		try {
			session = sqlSessionFactory.openSession(true);
			session.getMapper(IMapperAction.class).deleteTtranspLocate(trref, scref);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

	@Override
	public void insertTtranspLocate(int trref, int scref, int x, int y, int h, int w) {
		try {
			session = sqlSessionFactory.openSession(true);
			session.getMapper(IMapperAction.class).insertTtranspLocate(trref, scref, x, y, h, w);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			session.close();
		}
	}
	
	@Override
	public void updateTtranspLocate(int trref, int scref, int x, int y, int h, int w) {
		try {
			session = sqlSessionFactory.openSession(true);
			session.getMapper(IMapperAction.class).updateTtranspLocate(trref, scref, x, y, h, w);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

	@Override
	public void updateTtransparantLastUpdate(int idtr) {
		try {
			session = sqlSessionFactory.openSession(true);
			session.getMapper(IMapperAction.class).updateTtransparantLastUpdate(idtr);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			session.close();
		}
	}
	
	@Override
	public void updateTtranspHistory(int trref, String txt) {
		try {
			session = sqlSessionFactory.openSession(true);
			session.getMapper(IMapperAction.class).updateTtranspHistory(trref, txt);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			session.close();
		}
	}
	
	@Override
	public void setBaseVal(int idSignal, double val) {
		try {
			session = sqlSessionFactory.openSession(true);
			session.getMapper(IMapperAction.class).setBaseVal(idSignal, val);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			session.close();
		}
	}
	
	@Override
	public void updateTsignalStatus(int idSignal, int status) {
		try {
			session = sqlSessionFactory.openSession(true);
			session.getMapper(IMapperAction.class).updateTsignalStatus(idSignal, status);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			session.close();
		}
	}
	
	@Override
	public void insertDeventLog(int eventtype, int objref, Timestamp eventdt, double objval, int objstatus, int authorref) {
		try {
			session = sqlSessionFactory.openSession(true);
			session.getMapper(IMapperAction.class).insertDeventLog(eventtype, objref, eventdt, objval, objstatus, authorref);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			session.close();
		}
	}
//	----- V -----
	@Override
	public Map<Integer, VsignalView> getVsignalViewMap() {
		try {
			session = sqlSessionFactory.openSession();
			return session.getMapper(IMapperV.class).getVsignalViewMap();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("getVsignalViewMap");
			return null;
		} finally {
			session.close();
		}
	}
}
