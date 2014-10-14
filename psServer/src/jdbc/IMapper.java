package jdbc;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import pr.model.Alarm;
import pr.model.ConfTree;
import pr.model.DvalTI;
import pr.model.DvalTS;
import pr.model.LinkedValue;
import pr.model.SPunit;
import pr.model.SpTuCommand;
import pr.model.TSysParam;
import pr.model.TViewParam;
import pr.model.Transparant;
import pr.model.Tsignal;
import pr.model.TtranspHistory;
import pr.model.TtranspLocate;
import pr.model.Ttransparant;
import pr.model.Tuser;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface IMapper {
	@Select("select * from sp_unit")
	@MapKey("idunit")
	Map<Integer, SPunit> getSPunitMap();
	
	@Select("select * from sp_tu_command")
	List<SpTuCommand> getSpTuCommand();
	
	@Select("select * from t_signal")
	@MapKey("idsignal")
	Map<Integer, Tsignal> getTsignalsMap();
	
	@Select("select * from t_user")
	@MapKey("iduser")
	Map<Integer, Tuser> getTuserMap();
	
	@Select("select * from d_valti where servdt > #{servdt} order by servdt desc")
	List<DvalTI> getLastTI(Timestamp servdt);
	
	@Select("select (getlast_ti(idsignal, null, 0)).* from t_signal where typesignalref = 1")
	@Results(value = {
			@Result(property="signalref", column="id"),
			@Result(property="val", column="val"),
			@Result(property="dt", column="dt"),
			@Result(property="rcode", column="rcode"),
			@Result(property="servdt", column="servdt")
	})
	List<DvalTI> getOldTI();

	@Select("select * from d_valts where servdt > #{servdt} order by servdt desc")
	List<DvalTS> getLastTS(Timestamp servdt);
	
	@Select("select (getlast_ts(idsignal, null, 0)).* from t_signal where typesignalref = 2")
	@Results(value = {
			@Result(property="signalref", column="id"),
			@Result(property="val", column="val"),
			@Result(property="dt", column="dt"),
			@Result(property="rcode", column="rcode"),
			@Result(property="servdt", column="servdt")
	})
	List<DvalTS> getOldTS();
	
	@Select("select set_ts(#{idsignal}, #{val}, now()::timestamp without time zone, 107, -1, #{schemeref})")
	Integer setTS(@Param("idsignal") int idsignal, @Param("val") double val, @Param("schemeref") int schemeref);
	
	@Select("select * from "
			+ "(select *, alarms_for_event(eventtype,objref,objStatus) as alarms "
			+ "from d_eventlog) as eloginner join t_alarm on alarmid in (alarms) "
			+ "where recorddt > #{recorddt} order by recorddt desc")
	List<Alarm> getAlarms(Timestamp recorddt);
	
	@Select("select * from "
			+ "(select *, alarms_for_event(eventtype,objref,objStatus) as alarms "
			+ "from d_eventlog) as eloginner join t_alarm on alarmid in (alarms) "
			+ "where confirmdt > #{confirmdt} order by confirmdt desc")
	List<Alarm> getAlarmsConfirm(Timestamp confirmdt);
	
	@Select("select * from t_conftree")
	@MapKey("idnode")
	Map<Integer, ConfTree> getConfTreeMap();
	
	@Select("select * from t_sysparam")
	List<TSysParam> getTSysParam();
	
	@Select("select * from t_viewparam")
	List<TViewParam> getTViewParam();
	
	@Update("update d_eventlog set logstate = 2, confirmdt = #{confirmdt}, lognote = #{lognote}, userref = #{userref} "
			+ "where recorddt = #{recorddt} and  eventdt = #{eventdt} and objref = #{objref}")
	void confirmAlarm(@Param("recorddt")Timestamp recorddt, @Param("eventdt")Timestamp eventdt, 
					  @Param("objref")int objref, @Param("confirmdt")Timestamp confirmdt, 
					  @Param("lognote")String lognote, @Param("userref")int userref);
	
	@Delete("delete from t_sysparam where paramname = 'LAST_USR_ACK';")
	void deleteLastUserAck();
	
	@Insert("insert into t_sysparam values ( 3, 'LAST_USR_ACK', extract(epoch FROM now()), 'Время последнего квитирования');")
	void insertLastUserAck();
	
	@Update("update d_eventlog set logstate = 2, confirmdt = now(), lognote = #{lognote}, userref = #{userref} "
			+ "where logstate = 1")
	void confirmAlarmAll(@Param("lognote")String lognote, @Param("userref")int userref);
	
	@Select("select dt, val from d_valti where signalref = #{signalref} order by dt")
	List<LinkedValue> getData(int signalref);
	
	@Select("select dt, val from d_arcvalti where signalref = #{signalref} and dt > #{dtBeg} and dt < #{dtEnd} order by dt")
	List<LinkedValue> getDataArc(@Param("signalref")int signalref, @Param("dtBeg")Timestamp dtBeg, @Param("dtEnd")Timestamp dtEnd);
	
	@Select("select dt, val from f_valti(#{id}, #{dtbeg}, #{dtend}, #{inter}) order by dt")
	List<LinkedValue> getDataIntegr(@Param("id") int idSignal, @Param("dtbeg") Timestamp dtBeg, 
			@Param("dtend") Timestamp dtEnd, @Param("inter") int period);
	
	@Select("select dt, val from f_arcvalti(#{id}, #{dtbeg}, #{dtend}, #{inter}) order by dt")
	List<LinkedValue> getDataIntegrArc(@Param("id") int idSignal, @Param("dtbeg") Timestamp dtBeg, 
			@Param("dtend") Timestamp dtEnd, @Param("inter") int period);
	
	@Select("select img from t_transp_type where idtr = #{idtr}")
	Object getTransparantById(@Param("idtr")int idTransparant);
	
	@Select("select * from t_transp_type")
	@MapKey("idtr")
	Map<Integer, Transparant> getTransparants();
	
	@Select("select * from t_transparant where schemeref = #{schemeref} and closetime is null")
	List<Ttransparant> getTtransparantsActive(@Param("schemeref")int idScheme);
	
	@Select("select * from t_transparant where settime > #{settime} and closetime is null")
	List<Ttransparant> getTtransparantsNew(@Param("settime")Timestamp settime);
	
	@Select("select * from t_transp_locate where trref = #{trref}")
	TtranspLocate getTransparantLocate(@Param("trref")int trref);
	
	@Select("select * from t_transparant where closetime > #{closetime}")
	List<Ttransparant> getTtransparantsClosed(@Param("closetime")Timestamp closetime);
	
	@Select("select * from t_transparant where lastupdate > #{lastupdate} and settime != lastupdate and closetime is null")
	List<Ttransparant> getTtransparantsUpdated(@Param("lastupdate")Timestamp lastupdate);
	
//	@Select("insert into t_transparant(idtr, signRef, objName, tp, SchemeRef) "
//			+ "values (#{idtr}, #{signref}, #{objname}, #{tp}, #{schemeref})")
	@Select("insert into t_transparant(idtr, signRef, objName, tp, SchemeRef, settime, lastupdate) "
			+ "values (#{idtr}, #{signref}, #{objname}, #{tp}, #{schemeref}, now(), now())")
	void insertTtransparant(@Param("idtr")int idtr, @Param("signref")int signref, @Param("objname")String objname, 
			@Param("tp")int tp, @Param("schemeref")int schemeref);
	
	@Select("insert into t_transp_history(trRef, infoType, tm, userRef, txt, trtype) "
			+ "values (#{trref}, 0, now(), #{userref}, #{txt}, #{trtype})")
	void insertTtranspHistory(@Param("trref")int trref, @Param("userref")int userref, 
			@Param("txt")String txt, @Param("trtype")int trtype);
	
	@Select("delete from t_transp_locate where trRef=#{trref} and scRef=#{scref}")
	void deleteTtranspLocate(@Param("trref")int trref, @Param("scref")int scref);
	
	@Select("insert into t_transp_locate values (#{trref}, #{scref}, #{x}, #{y}, #{h}, #{w})")
	void insertTtranspLocate(@Param("trref")int trref, @Param("scref")int scref, 
			@Param("x")int x, @Param("y")int y, @Param("h")int h, @Param("w")int w);
	
	@Select("update t_transp_locate set scref = #{scref}, "
			+ "x = #{x}, y = #{y}, h = #{h}, w = #{w} where trref = #{trref}")
	void updateTtranspLocate(@Param("trref")int trref, @Param("scref")int scref, 
			@Param("x")int x, @Param("y")int y, @Param("h")int h, @Param("w")int w);
	
	@Select("update t_transparant set lastupdate = now() where idtr = #{idtr}")
	void updateTtransparantLastUpdate(@Param("idtr")int idtr);
	
	@Select("select max(idtr) + 1 from t_transparant")
	int getMaxTranspID();
	
	@Select("update t_transparant set closetime = now() where idtr = #{idtr}")
	void updateTtransparantCloseTime(@Param("idtr")int idtr);
	
	@Select("select * from t_transp_history where trref = #{trref}")
	TtranspHistory getTtranspHistory(@Param("trref")int trref);
	
	@Select("update t_transp_history set txt=#{txt} where trref = #{trref}")
	void updateTtranspHistory(@Param("trref")int trref, @Param("txt")String txt);
	
	@Select("select * from t_transparant where idtr = #{idtr}")
	Ttransparant getTtransparantById(@Param("idtr")int idtr);
}