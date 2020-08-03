USE laflor;

SELECT * FROM htarifa_iva
WHERE periodo > '2018-10-31';

DELETE FROM htarifa_iva
WHERE periodo > '2018-10-31';

Select * from hbodexis 
where artperi > '2018-10-31';

Delete from hbodexis 
where artperi > '2018-10-31';

Select * from hinarticu
where artperi > '2018-10-31'
order by artcode;

Delete from hinarticu
where artperi > '2018-10-31';

Select * from hinclient
where cliperi > '2018-10-31'
order by clicode;

Delete from hinclient
where cliperi > '2018-10-31';

Select * from hinproved
where properi > '2018-10-31'
order by procode;

Delete from hinproved
where properi > '2018-10-31';

-- Determinar la última fecha de cierre
Select max(properi) from hinproved; -- 2018-09-30 23:59:59

Select * from bodegas;

Update bodegas set cerrada = '2018-09-30 23:59:59' Where bodega = '001';

Select * from config;

Update config set cierre = '2018-09-30 23:59:59', mescerrado = 9, anocerrado = 2018;

Select * from hbodexis 
where artcode = '11239'
and artperi > '2019-02-28';

show processlist;
show status like 'Threads%';

