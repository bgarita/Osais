
-- Totalizar montos. Se usa para revisar el reporte de ventas por artículo.
SELECT 
	inarticu.artdesc,
	fadetall.artcode,
	SUM(fadetall.facimve) AS IVA,
	SUM(fadetall.facdesc) AS facdesc,
	SUM(fadetall.facmont) AS facmont,
	SUM(fadetall.facmont - fadetall.facdesc + fadetall.facimve) AS Total
FROM fadetall
INNER JOIN faencabe ON fadetall.facnume = faencabe.facnume AND fadetall.facnd = faencabe.facnd
INNER JOIN inarticu ON fadetall.artcode = inarticu.artcode
-- WHERE fadetall.artcode = '0035'
AND YEAR(faencabe.facfech) = 2020 AND MONTH(facfech) = 10
AND faencabe.facestado = ''
GROUP BY 1,2
ORDER BY 1,2;

SELECT 
	SUM(fadetall.facimve) AS IVA,
	SUM(fadetall.facdesc) AS facdesc,
	SUM(fadetall.facmont) AS facmont,
	SUM(fadetall.facmont - fadetall.facdesc + fadetall.facimve) AS Total
FROM fadetall
INNER JOIN faencabe ON fadetall.facnume = faencabe.facnume AND fadetall.facnd = faencabe.facnd
INNER JOIN inarticu ON fadetall.artcode = inarticu.artcode
-- WHERE fadetall.artcode = '0035'
AND YEAR(faencabe.facfech) = 2020 AND MONTH(facfech) = 10
AND DAY(faencabe.facfech) BETWEEN 16 AND 31
AND faencabe.facestado = '';

-- Totalizar por factura. Se usa para revisar el reporte de resumen.
SELECT 
	fadetall.facnume, 
	fadetall.facnd,
	SUM(fadetall.facmont) AS facmont,
	Sum(fadetall.facdesc) AS facdesc,
	SUM(fadetall.facimve) AS IVA,
	SUM(fadetall.facmont - fadetall.facdesc + fadetall.facimve) AS Total,
	faencabe.facmont AS Total2
FROM fadetall
INNER JOIN faencabe ON fadetall.facnume = faencabe.facnume AND fadetall.facnd = faencabe.facnd
-- WHERE fadetall.facnume = 10006946
AND YEAR(faencabe.facfech) = 2020 AND MONTH(facfech) = 10
AND faencabe.facestado = ''
GROUP BY 1,2
ORDER BY 1,2;


-- Resumen de ventas por tipo de impuesto.
SELECT 
	tarifa_iva.descrip,
	fadetall.facpive,
	SUM(fadetall.facmont) AS subtotal,
	SUM(fadetall.facdesc) AS facdesc,
	SUM(fadetall.facimve) AS IVA,
	SUM(fadetall.facmont - fadetall.facdesc + fadetall.facimve) AS Total
FROM fadetall
INNER JOIN faencabe ON fadetall.facnume = faencabe.facnume AND fadetall.facnd = faencabe.facnd
INNER JOIN inarticu ON fadetall.artcode = inarticu.artcode
INNER JOIN tarifa_iva ON fadetall.codigoTarifa = tarifa_iva.codigoTarifa
-- WHERE fadetall.artcode = '0035'
AND YEAR(faencabe.facfech) = 2020 AND MONTH(facfech) = 10
AND faencabe.facestado = ''
GROUP BY 1,2
ORDER BY 1,2;