-- Visa vistaconsecutivoasientos
CREATE OR REPLACE VIEW vistaconsecutivoasientos
AS 
SELECT 
	no_comprob, 
	tipo_comp
FROM coasientoe
UNION 
SELECT 
	no_comprob, 
	tipo_comp
FROM hcoasientoe;