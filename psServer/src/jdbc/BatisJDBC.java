package jdbc;

import java.util.logging.Level;

import org.apache.ibatis.session.SqlSession;

import pr.log.LogFiles;
import single.SingleFromDB;

public class BatisJDBC {
	private IBatisJDBC iBatis;
	
	public BatisJDBC(IBatisJDBC iBatis) {
		this.iBatis = iBatis;
	}
	
	public Object get() {
		SqlSession session = null;
		try {
			session = SingleFromDB.getPdb().getSqlSessionFactory().openSession(true);
			return iBatis.getResult(session);
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, "BatisJDBC.get() ", e);
			SingleFromDB.setPdb(null);
			return null;
		} finally {
			session.close();
		}
	}
}