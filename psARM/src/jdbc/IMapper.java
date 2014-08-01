package jdbc;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import model.Alarm;
import model.ConfTree;
import model.DvalTI;
import model.DvalTS;
import model.LinkedValue;
import model.TSysParam;
import model.TViewParam;
import model.Tsignal;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface IMapper {
	@Select("select * from t_signal")
	@MapKey("idsignal")
	Map<Integer, Tsignal> getTsignalsMap();
	
	@Select("select * from t_signal where idsignal = #{id}")
	Tsignal getTsignalById(int id);
	
	@Select("select * from d_valti where servdt > #{servdt} order by servdt desc")
	List<DvalTI> getLastTI(Timestamp servdt);
	
	@Select("select idsignal, (getlast_ti(idsignal, null, 0)).val from t_signal where typesignalref = 1")
	@MapKey("idsignal")
	Map<Integer, LinkedValue> getOldTI();

	@Select("select * from d_valts where servdt > #{servdt} order by servdt desc")
	List<DvalTS> getLastTS(Timestamp servdt);
	
	@Select("select idsignal, (getlast_ts(idsignal, null, 0)).val from t_signal where typesignalref = 2")
	@MapKey("idsignal")
	Map<Integer, LinkedValue> getOldTS();
	
	@Select("select set_ts(#{idsignal}, #{val}, now()::timestamp without time zone, 107, -1, #{schemeref})")
	Integer setTS(@Param("idsignal") int idsignal, @Param("val") double val, @Param("schemeref") int schemeref);
	
	@Select("select * from "
			+ "(select *, alarms_for_event(eventtype,objref,objStatus) as alarms "
			+ "from d_eventlog) as eloginner join t_alarm on alarmid in (alarms) "
			+ "where recorddt >= #{recorddt} order by recorddt desc")
	List<Alarm> getAlarms(Timestamp recorddt);
	
	@Select("select * from "
			+ "(select *, alarms_for_event(eventtype,objref,objStatus) as alarms "
			+ "from d_eventlog) as eloginner join t_alarm on alarmid in (alarms) "
			+ "where confirmdt >= #{confirmdt} order by confirmdt desc")
	List<Alarm> getAlarmsConfirm(Timestamp confirmdt);
	
	@Select("select * from t_conftree")
	@MapKey("idnode")
	Map<Integer, ConfTree> getConfTreeMap();
	
	@Select("select * from t_sysparam where paramname = #{paramname}")
	@MapKey("val")
	Map<String, TSysParam> getTSysParamMap(String paramname);
	
	@Select("select * from t_viewparam where objdenom = #{objdenom} and paramdenom = #{paramdenom} and userref = #{userref}")
	List<TViewParam> getTViewParam(@Param("objdenom") String objdenom, @Param("paramdenom") String paramdenom, @Param("userref") int userref);
}
