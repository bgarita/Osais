

-- Agrupar los datos por número de asiento y verificar débitos contra créditos
CREATE TEMPORARY TABLE review
	SELECT
		d.no_comprob,
		d.tipo_comp, 
		IfNull(sum(If(d.db_cr = 1, monto, 0)),0) as debito, 
		IfNull(sum(If(d.db_cr = 0, monto, 0)),0) as credito 
	from coasientod d 
	Inner join coasientoe e on  
	d.no_comprob = e.no_comprob and d.tipo_comp = e.tipo_comp 
	Where date(e.fecha_comp) BETWEEN '2022-03-01' AND '2022-03-31' 
	and d.colect > 0 
	GROUP BY d.no_comprob, d.tipo_comp ;
                
SELECT * FROM review WHERE debito <> credito;
