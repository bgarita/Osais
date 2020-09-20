-- Verificar datos migrados

-- Catálogo
SELECT COUNT(*) as cuentas FROM cocatalogo; -- 157  Fox: SELECT COUNT(*) FROM ..\migration\cat2

-- Movimientos totales
SELECT COUNT(*) FROM coasientod; -- 4,189 Fox: SELECT COUNT(*) FROM ..\migration\aslcg02a
SELECT 
	SUM(if(db_cr = 0, monto, 0)) AS DB, -- 510,876,547.010
	SUM(if(db_cr = 1, monto, 0)) AS CR  -- 510,876,547.010
FROM coasientod;
-- Fox: SELECT SUM(iif(db_cr = 0, monto, 0)) AS DB, SUM(iif(db_cr = 1, monto, 0)) AS CR FROM ..\migration\aslcg02a
-- DB  510,876,547.00
-- CR  510,876,547.01


-- Movimiento febrero 2010
SELECT 
	d.no_comprob,	-- 2992010 
	SUM(d.monto)   -- 268,307,228.64
FROM coasientod d
INNER JOIN coasientoe e ON e.no_comprob = d.no_comprob AND e.tipo_comp = d.tipo_comp
WHERE YEAR(e.fecha_comp) = 2010
AND MONTH(e.fecha_comp) = 2
GROUP BY 1
ORDER BY 1;
-- Fox: SELECT no_comprob, SUM(monto) FROM ..\migration\aslcg02a WHERE YEAR(fecha_comp) == 2010 AND MONTH(fecha_comp) == 2 GROUP BY 1 ORDER BY 1
-- 2992010   
-- 268,307,228.64

-- Cantidad de asientos para un mes
SELECT COUNT(*) FROM coasientoe 	-- 1 Fox: SELECT distinct no_comprob FROM ..\migration\aslcg02a WHERE YEAR(fecha_comp) == 2010 AND MONTH(fecha_comp) == 2
WHERE YEAR(fecha_comp) = 2010
AND MONTH(fecha_comp) = 2; 

SELECT COUNT(*) FROM coasientoe 	-- 6 Fox: SELECT distinct no_comprob FROM ..\migration\aslcg02a WHERE YEAR(fecha_comp) == 2010 AND MONTH(fecha_comp) == 2 ORDER BY 1
WHERE YEAR(fecha_comp) = 2010
AND MONTH(fecha_comp) = 3;

-- Total por asiento (Se debe enviar a excel y comparar con fox)
SELECT 
	d.no_comprob,
	d.tipo_comp,
	Sum(if(d.db_cr = 0, d.monto, 0)) AS DB,
	sum(if(d.db_cr = 1, d.monto, 0)) AS CR
FROM coasientod d
INNER join coasientoe e ON d.no_comprob = e.no_comprob AND d.tipo_comp = e.tipo_comp
WHERE YEAR(e.fecha_comp) = 2010
AND MONTH(e.fecha_comp) = 3
GROUP BY 1,2
ORDER BY 1,2;
-- Fox: SELECT no_comprob, tipo_comp, SUM(iif(db_cr = 0, monto, 0)) AS DB, SUM(iif(db_cr = 1, monto, 0)) AS CR FROM ..\migration\aslcg02a WHERE YEAR(fecha_comp) = 2010 AND MONTH(fecha_comp) = 3 GROUP BY no_comprob, tipo_comp ORDER BY 1,2

