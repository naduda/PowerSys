package jdbc;

import java.util.logging.Level;

import org.apache.ibatis.session.SqlSession;

import pr.log.LogFiles;
import single.SingleFromDB;

public class BatisJDBC {
	private static final int MAX_REPET = 3;
	private IBatisJDBC iBatis;
	private int count;
	
	public BatisJDBC(IBatisJDBC iBatis) {
		this.iBatis = iBatis;
	}
	
	public Object get() {
		while (count < MAX_REPET) {
			SqlSession session = null;
			try {
				session = SingleFromDB.getPdb().getSqlSessionFactory().openSession(true);
				return iBatis.getResult(session);
			} catch (Exception e) {
				if (count == MAX_REPET - 1) {
					LogFiles.log.log(Level.SEVERE, "BatisJDBC.get() ", e);
				} else {
					LogFiles.log.log(Level.WARNING, "Try " + count);
				}
				SingleFromDB.setPdb(null);
			} finally {
				session.close();
			}
			count++;
		}
		return null;
	}
}