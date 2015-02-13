select times.dt as "Дата/время", v1.val as "Выходной ток", v2.val as "Выходное напряжение", v3.val as "Датчик потенциала", 
case getval_ts(25,times.dt) when 1 then 'Ток' when 2 then 'Напряжение' else 'Потенциал' end as "Режим стабилизации", 
v5.val as "Значение стабилизации", case getval_ts(193,times.dt) when 0 then 'Отключен' else 'Включен' end as "Сотояние силового ", 
v7.val as "Текущие показания А+", v8.val as "Текущие показания R+" 
from (
select  to_date('dtBeg','YYYY-MM-DD') + cast((i::varchar || ' hour') as interval) as dt 
from generate_series(0,(to_date('dtEnd','YYYY-MM-DD')-to_date('dtBeg','YYYY-MM-DD'))*48) as i 
) times 
left join(select dt, val, signalref from f_arcvalti(19, 'dtBeg', 'dtEnd', 60) order by dt) as v1 
on v1.dt = times.dt 
left join( select dt, val, signalref from f_arcvalti(18, 'dtBeg', 'dtEnd', 60) order by dt) as v2 
on v2.dt = times.dt 
left join( select dt, val, signalref from f_arcvalti(21, 'dtBeg', 'dtEnd', 60) order by dt) as v3 
on v3.dt = times.dt 
left join( select dt, val, signalref from f_arcvalti(26, 'dtBeg', 'dtEnd', 60) order by dt) as v5 
on v5.dt = times.dt 
left join( select dt, val, signalref from f_arcvalti(129, 'dtBeg', 'dtEnd', 60) order by dt) as v7 
on v7.dt = times.dt 
left join( select dt, val, signalref from f_arcvalti(131, 'dtBeg', 'dtEnd', 60) order by dt) as v8 
on v8.dt = times.dt 
where v1.val is not null