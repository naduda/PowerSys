package jdbc.mappers;

import java.sql.Timestamp;
import java.util.List;

import pr.model.Alarm;
import pr.model.ControlJournalItem;
import pr.model.DvalTI;
import pr.model.DvalTS;
import pr.model.LinkedValue;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

public interface IMapper {
//	==============================================================================
	void update(String query);
//	==============================================================================
	
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
			+ "where recorddt >= #{dtBeg} and recorddt <= #{dtEnd} order by recorddt desc")
	List<Alarm> getAlarmsPeriod(@Param("dtBeg")Timestamp dtBeg, @Param("dtEnd")Timestamp dtEnd);
	
	@Select("select * from "
			+ "(select *, alarms_for_event(eventtype,objref,objStatus) as alarms "
			+ "from d_eventlog) as eloginner join t_alarm on alarmid in (alarms) "
			+ "where confirmdt > #{confirmdt} order by confirmdt desc")
	List<Alarm> getAlarmsConfirm(Timestamp confirmdt);
	
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
	
	@Select("select dt, schemeName, nameSignal, formatValue(idsignal, d.val) as val, paramdescr as status, "
			+ "statedt-servdt as duration, fio from (select * from d_arcvaltu tu union all "
			+ "(select dt, signalref, val, 3 as sendok, servdt, rcode, userref, statedt, "
			+ "ts.schemeRef from d_arcvalts ts where rcode = 107 )) as d left join t_signal s "
			+ "on signalref=idsignal left join t_scheme sc on d.schemeref=idscheme left join "
			+ "(select -1 as idUser, 'Admin' as fio union select iduser, fio from t_user) u on "
			+ "userRef=idUser left join t_sysparam v on v.val=d.sendOK and paramname='SENDTU_STATUS' "
			+ "where dt >= #{dtBeg} and dt <= #{dtEnd} order by dt")
	List<ControlJournalItem> getJContrlItems(@Param("dtBeg")Timestamp dtBeg, @Param("dtEnd")Timestamp dtEnd);
}