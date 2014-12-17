package jdbc;

import org.apache.ibatis.session.SqlSession;

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
			e.printStackTrace();
			return null;
		} finally {
			session.close();
		}
	}
}
