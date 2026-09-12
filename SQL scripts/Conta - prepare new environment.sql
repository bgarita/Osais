
-- Cambios conta cloud solamente

--- 24/06/2026
USE contabosco;

UPDATE cocatalogo
SET nom_cta = 'Libre'
WHERE nom_cta = '0';

DELETE FROM cabys;

SELECT * FROM cajero;
DELETE FROM cajero WHERE USER <> 'bgarita';

SELECT * FROM usuario;
DELETE FROM cajero WHERE USER <> 'bgarita';

--- 
ALTER TABLE `usuario`
	ADD COLUMN `clave` VARCHAR(255) NOT NULL DEFAULT ' ' AFTER `user`;
