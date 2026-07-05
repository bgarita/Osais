-- Cambios Osais

-- Agregar campo para la clave del usuario
ALTER TABLE `usuario`
	ADD COLUMN `clave` VARCHAR(255) NOT NULL DEFAULT '' AFTER `user`;