
-- Borrar transaccione y catálogo para volver a cargar

-- Borrar los datos históricos
TRUNCATE TABLE hcoasientod;
DELETE FROM hcoasientoe;
DELETE FROM hcocatalogo;

-- Borrar los datos actuales
TRUNCATE TABLE coasientod;
DELETE FROM coasientoe;
DELETE FROM cocatalogo;

-- Actualizar los periodos
UPDATE coperiodoco SET cerrado = 0;
UPDATE configcuentas SET mesactual = 2, añoactual = 2010, mescierrea = 9;