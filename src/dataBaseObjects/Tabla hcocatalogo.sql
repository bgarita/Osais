
ALTER TABLE `hcocatalogo`
	CHANGE COLUMN `fecha_cierre` `fecha_cierre` DATE NOT NULL COMMENT 'Fecha de cierre' AFTER `activa`;