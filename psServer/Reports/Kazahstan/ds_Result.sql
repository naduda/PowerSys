select times.dt, v1.val, v2.val as val2, v3.val as val3, 
case getval_ts(95,times.dt) when 1 then 'Ток' when 2 then 'Напряжение' else 'Потенциал' end as val4, v5.val as val5, 
case getval_ts(99,times.dt) when 0 then 'Отключен' else 'Включен' end as val6, v7.val as val7, v8.val as val8 
from (
select  to_date('dtBeg','YYYY-MM-DD') + cast((i::varchar || ' hour') as interval) as dt 
from generate_series(0,(to_date('dtEnd','YYYY-MM-DD')-to_date('dtBeg','YYYY-MM-DD'))*48) as i 
) times 
left join(select dt, val, signalref from f_arcvalti(87, 'dtBeg', 'dtEnd', 60) order by dt) as v1 
on v1.dt = times.dt 
left join( select dt, val, signalref from f_arcvalti(86, 'dtBeg', 'dtEnd', 60) order by dt) as v2 
on v2.dt = times.dt 
left join( select dt, val, signalref from f_arcvalti(89, 'dtBeg', 'dtEnd', 60) order by dt) as v3 
on v3.dt = times.dt 
left join( select dt, val, signalref from f_arcvalti(96, 'dtBeg', 'dtEnd', 60) order by dt) as v5 
on v5.dt = times.dt 
left join( select dt, val, signalref from f_arcvalti(27, 'dtBeg', 'dtEnd', 60) order by dt) as v7 
on v7.dt = times.dt 
left join( select dt, val, signalref from f_arcvalti(29, 'dtBeg', 'dtEnd', 60) order by dt) as v8 
on v8.dt = times.dt 
where v1.val is not null