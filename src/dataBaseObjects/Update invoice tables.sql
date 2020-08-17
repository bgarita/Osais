
-- Actualizar el código de impuesto
START TRANSACTION;
UPDATE inarticu
	SET codigoTarifa = '08'
WHERE artusaIVG = 1;


UPDATE inarticu
	SET codigoTarifa = '02'
WHERE artusaIVG = 0
AND artimpv = 1;
-- COMMIT;

START TRANSACTION;
UPDATE hinarticu
	SET codigoTarifa = '08'
WHERE artusaIVG = 1;


UPDATE hinarticu
	SET codigoTarifa = '02'
WHERE artusaIVG = 0
AND artimpv = 1;
-- COMMIT;


-- Actualizar los registros de ventas
START TRANSACTION;
UPDATE fadetall
	SET codigoTarifa = '02'
WHERE facpive = 1;

UPDATE fadetall
	SET codigoTarifa = '08'
WHERE facpive = 13;
-- Commit;
