-- Consultar un asiento
SELECT 
	*
FROM coasientod d
INNER JOIN coasientoe e ON 
	d.no_comprob = e.no_comprob AND 
	d.tipo_comp = e.tipo_comp
INNER JOIN cocatalogo c ON
	d.mayor = c.mayor AND 
	d.sub_cta = c.sub_cta AND 
	d.sub_sub = c.sub_sub AND 
	d.colect = c.colect
WHERE d.no_comprob = '0001252014'
AND e.tipo_comp = 5;