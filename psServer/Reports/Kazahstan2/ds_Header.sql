select namesignal from t_signal 
where idsignal in (19,18,21,25,26,193,129,131) 
order by position(idsignal::text in '19,18,21,25,26,193,129,131')