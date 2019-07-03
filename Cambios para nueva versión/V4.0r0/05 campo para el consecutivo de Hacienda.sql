use `LaFlor`;
ALTER TABLE `faencabe` 
ADD COLUMN `consHacienda` VARCHAR(20) NOT NULL DEFAULT ' ' COMMENT 'Consecutivo generado para Hacienda' AFTER `claveHacienda`;
