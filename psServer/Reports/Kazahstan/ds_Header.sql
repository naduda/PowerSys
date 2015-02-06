select namesignal from t_signal 
where idsignal in (87,86,89,95,96,99,27,29) 
order by position(idsignal::text in '87,86,89,95,96,99,27,29')