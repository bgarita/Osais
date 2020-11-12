-- Agregar el consecutivo por tipo de asiento
ALTER TABLE `cotipasient`
	ADD COLUMN `consecutivo` INT NOT NULL DEFAULT '0' COMMENT 'Número consecutivo' AFTER `descrip`;
